/*
 * (c) Copyright 2004, Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.server.http;

import java.util.* ;

import org.joseki.util.StringUtils ;
import org.apache.commons.logging.*;


/** A class to handle a list of accept types
 * 
 * @author Andy Seaborne
 * @version $Id: AcceptList.java,v 1.1 2004-11-26 16:58:57 andy_seaborne Exp $
 */

public class AcceptList
{
    private static Log log = LogFactory.getLog(AcceptList.class) ;
    List list ; // List of ranges
    
    public AcceptList(String[]a)
    {
        list = new ArrayList() ;
        for ( int i = 0 ; i < a.length ; i++ )
            list.add(new AcceptRange(a[i])) ;
    }
    
    public AcceptList(String s)
    {
        try {
            list = stringToAcceptList(s) ;
        } catch (Exception ex)
        {
            log.warn("Unrecognized accept string (ignored): "+s) ;
            list = new ArrayList() ;
        }
    }
    
    public boolean accepts(AcceptItem aItem)
    {
        for ( Iterator iter = list.iterator() ; iter.hasNext() ; )
        {
            AcceptRange i = (AcceptRange)iter.next() ;
            //System.out.println("Check: "+i+" accepts "+aItem) ;
            // Not commutative
            if ( i.getAcceptItem().accepts(aItem) )
                return true ;
        }
        return false ;
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
    
    public static class AcceptTypeCompare implements Comparator
    {
        public int compare(Object arg1, Object arg2)
        {
            if ( ! (arg1 instanceof AcceptRange) )
                throw new ClassCastException("Not a AcceptItem: "+arg1.getClass().getName()) ;
            if ( ! (arg2 instanceof AcceptRange) )
                throw new ClassCastException("Not a AcceptItem: "+arg2.getClass().getName()) ;
            AcceptRange mType1 = (AcceptRange)arg1 ; 
            AcceptRange mType2 = (AcceptRange)arg2 ;
            
            int r = Double.compare(mType1.q, mType2.q) ;
            
            if ( r == 0 )
                r = subCompare(mType1.acceptItem.getAcceptType(), mType2.acceptItem.getAcceptType()) ;
            
            if ( r == 0 )
                r = subCompare(mType1.acceptItem.getAcceptType(), mType2.acceptItem.getAcceptType()) ;
            
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
            AcceptRange mType = new AcceptRange(x[i]) ;
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
            tmp = tmp + ((AcceptRange)iter.next()).toHeaderString() ;
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
            if ( ! first )
                tmp = tmp +" " ;
            tmp = tmp + (AcceptRange)iter.next() ;
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


/*
 * (c) Copyright 2004 Hewlett-Packard Development Company, LP
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
