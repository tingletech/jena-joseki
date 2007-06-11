/*
 * (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.http;

import java.util.* ;

import org.joseki.util.StringUtils ;
import org.apache.commons.logging.*;


/** A class to handle a list of accept types (AcceptRanges)
 * 
 * @author Andy Seaborne
 * @version $Id: AcceptList.java,v 1.8 2007-06-11 18:50:14 andy_seaborne Exp $
 */

public class AcceptList
{
    // Documentation at end
    private static Log log = LogFactory.getLog(AcceptList.class) ;
    List list ; // List of AcceptRanges
    
    
    /**
     * Create a list of accept items from the give strings.
     * @param acceptStrings
     */
    
    public AcceptList(List acceptStrings)
    {
        list = new ArrayList(acceptStrings) ;
    }
    
    /**
     * Create a list of accept items from the give strings.
     * @param acceptStrings
     */
    
    public AcceptList(String[] acceptStrings)
    {
        list = new ArrayList() ;
        for ( int i = 0 ; i < acceptStrings.length ; i++ )
            list.add(new AcceptItem(acceptStrings[i])) ;
    }
    
    /**
     * Parse an HTTP Accept (or etc) header string. 
     * @param headerString
     */
    
    public AcceptList(String headerString)
    {
        try {
            list = stringToAcceptList(headerString) ;
        } catch (Exception ex)
        {
            log.warn("Unrecognized accept string (ignored): "+headerString) ;
            list = new ArrayList() ;
        }
    }
    
    public boolean accepts(AcceptItem aItem)
    {
        return match(aItem) != null ;
    }
    
    public AcceptItem match(AcceptItem aItem)
    {
        for ( Iterator iter = list.iterator() ; iter.hasNext() ; )
        {
            AcceptItem i = (AcceptItem)iter.next() ;
            //System.out.println("Check: "+i+" accepts "+aItem) ;
            if ( i.accepts(aItem) )
            {
                // Return the more grounded term
                // E.g. i = text/plain ; aItem = text/*
                
                if ( aItem.moreGroundedThan(i) )
                    i = new AcceptItem(aItem, i.q) ;
                return i ;
            }
        }
        return null ;
    }
    
    /** Find the best thing in offer list with the proposal  
     * 
     * @param proposalList Client list of possibilities
     * @param offerList    Server list of possibilities
     * @return AcceptItem
     */
    
    static public AcceptItem match(AcceptList proposalList, AcceptList offerList)
    {
        // TODO Need to find the best match, not the first offer match.
        // Find every offer match, choose the highest q factor
        
        AcceptItem choice = null ;
        
        for ( Iterator iter = offerList.iterator() ; iter.hasNext() ; )
        {
            AcceptItem i2 = (AcceptItem)iter.next() ;
            AcceptItem m = proposalList.match(i2) ; // XXX
            if ( m != null )
            {
                if ( choice != null && choice.q >= m.q )
                    continue ; 
                choice = m ;
            }
        }
        return choice ;
    }
    
    public AcceptItem first()
    {
        if ( list != null && list.size() > 0 )
            return (AcceptItem)list.get(0) ;
        return null ;
    }

    public Iterator iterator()
    { return list.iterator() ; }
    
    // Sort - the leftmost element (lowest index) will be the preferred accept type.
    
    private static class AcceptTypeCompare implements Comparator
    {
        public int compare(Object arg1, Object arg2)
        {
            if ( ! (arg1 instanceof AcceptItem) )
                throw new ClassCastException("Not a AcceptItem: "+arg1.getClass().getName()) ;
            if ( ! (arg2 instanceof AcceptItem) )
                throw new ClassCastException("Not a AcceptItem: "+arg2.getClass().getName()) ;
            AcceptItem mType1 = (AcceptItem)arg1 ; 
            AcceptItem mType2 = (AcceptItem)arg2 ;
            
            int r = Double.compare(mType1.q, mType2.q) ;
            
            if ( r == 0 )
                r = subCompare(mType1.getAcceptType(), mType2.getAcceptType()) ;
            
            if ( r == 0 )
                r = subCompare(mType1.getAcceptType(), mType2.getAcceptType()) ;
            
            if ( r == 0 )
            {
                // This reverses the input order so that the rightmost elements is the
                // greatest and hence is the first mentioned in the accept range.
                
                if ( mType1.posn < mType2.posn )
                    r = +1 ;
                if ( mType1.posn > mType2.posn )
                    r = -1 ;
            }
            
            // The most significant sorts to the first in a list.
            r = -r ;
            return r ;
        }
        
        public int subCompare(String a, String b)
        {
            if ( a == null )
                return 1 ;
            if ( b == null )
                return -1 ;
            if ( a.equals("*") && b.equals("*") )
                return 0 ;
            if ( a.equals("*") )
                return -1 ;
            if ( b.equals("*") )
                return 1 ;
            return 0 ;
        }
    }

    // Utilities
    
    /** Returns a list of headers, sorted so that the most significant is first */
    
    static List stringToAcceptList(String s)
    {
        //s = s.trim() ;
        List l = new ArrayList() ;
        if ( s == null )
            return l ;
        
        String[] x = StringUtils.split(s, ",") ;
        for ( int i = 0 ; i < x.length ; i++ )
        {
            if ( x[i].equals(""))
                continue ;
            AcceptItem mType = new AcceptItem(x[i]) ;
            mType.posn = i ;
            l.add(mType) ;
        }
        Collections.sort(l, new AcceptTypeCompare()) ;
        return l ;
    }
    
    public String toHeaderString()
    {
        if ( list.size() == 0)
            return "" ;
        
        String tmp = "" ;
        
        boolean first = true ;
        for ( Iterator iter = list.iterator() ; iter.hasNext() ; )
        {
            if ( ! first )
                tmp = tmp +"," ;
            tmp = tmp + ((AcceptItem)iter.next()).toHeaderString() ;
            first = false ;
        }
        return tmp ;
    }
    
    /** Debug form */
    
    public String toString() { return acceptListToString(list) ; }
    
    static String acceptListToString(List x)
    {
        if ( x.size() == 0)
            return "(empty)" ;
        
        String tmp = "" ;
        
        boolean first = true ;
        for ( Iterator iter = x.iterator() ; iter.hasNext() ; )
        {
            AcceptItem a = (AcceptItem)iter.next() ; 
            if ( ! first )
                tmp = tmp +" " ;
            a.toString();
            tmp = tmp + a ;
            first = false ;
        }
        return tmp ;
    }
    
//    static List multiCombineMT(List list1, List list2)
//    {
//        List r = new ArrayList() ;
//        r.add(list1) ;
//        r.add(list2) ;
//        Collections.sort(r, new AcceptTypeCompare()) ;
//        return r ;
//    }
}    

// RFC 2068(HTTP 1.1) defines the format:
//        media-type     = type "/" subtype *( ";" parameter )
//        type           = token
//        subtype        = token
//
// Parameters may follow the type/subtype in the form of attribute/value pairs.
//
//        parameter      = attribute "=" value
//        attribute      = token
//        value          = token | quoted-string

//    Accept         = "Accept" ":"
//        #( media-range [ accept-params ] )
//
//media-range    = ( "*/*"
//        | ( type "/" "*" )
//        | ( type "/" subtype )
//        ) *( ";" parameter )
//
//accept-params  = ";" "q" "=" qvalue *( accept-extension )
//
//accept-extension = ";" token [ "=" ( token | quoted-string ) ]
//
// Accept-Charset = "Accept-Charset" ":"
//                  1#( charset [ ";" "q" "=" qvalue ] )



// Examples:
// -- Firefox 1.0  
// Accept           = text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5
// Accept-Language  = en-us,en;q=0.5
// Accept-Encoding  = gzip,deflate
// Accept-Charset   = ISO-8859-1,utf-8;q=0.7,*;q=0.7
// -- IE 6
// Accept           = image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*
// Accept-Language  = en-gb
// Accept-Encoding  = gzip, deflate




/*
 * (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
