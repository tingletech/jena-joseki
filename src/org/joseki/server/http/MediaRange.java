/*
 * (c) Copyright 2004, Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.server.http;
import java.util.* ;
import org.apache.commons.logging.* ;

/** A class to handle HTTP media types and media ranges and accept parameters 
 * 
 * @author Andy Seaborne
 * @version $Id: MediaRange.java,v 1.2 2004-11-21 16:43:03 andy_seaborne Exp $
 */

public class MediaRange
{
    // TODO Rename - this works on more than media types. 
    // Documentation at end.
    
    MediaType mediaType = null;
    
    Map params = new HashMap() ;
    double q = 1.0 ;
    int posn = 0 ;
    
    /** Returns a list of headers, sorted so that the most significant is first
     */
    
    static List multiMediaRange(String s)
    {
        //s = s.trim() ;
        List l = new ArrayList() ;
        if ( s == null )
            return l ;
        
        String[] x = split(s, ",") ;
        for ( int i = 0 ; i < x.length ; i++ )
        {
            if ( x[i].equals(""))
                continue ;
            MediaRange mType = new MediaRange(x[i]) ;
            mType.posn = i ;
            l.add(mType) ;
        }
        multiMediaRangeSort(l);
        return l ;
    }

    static void multiMediaRangeSort(List l)
    {
        Collections.sort(l, new MediaTypeCompare()) ;
    }
                                   
    
    /** Debug form */
    
    static String multiMediaRangeToString(List x)
    {
        if ( x.size() == 0)
            return "(empty)" ;
        
        String tmp = "" ;
        
        boolean first = true ;
        for ( Iterator iter = x.iterator() ; iter.hasNext() ; )
        {
            if ( ! first )
                tmp = tmp +" " ;
            tmp = tmp + (MediaRange)iter.next() ;
            first = false ;
        }
        return tmp ;
        
    }
    
    static String multiMediaRangeToHeaderString(List x)
    {
        if ( x.size() == 0)
            return "" ;
        
        String tmp = "" ;
        
        boolean first = true ;
        for ( Iterator iter = x.iterator() ; iter.hasNext() ; )
        {
            if ( ! first )
                tmp = tmp +"," ;
            tmp = tmp + ((MediaRange)iter.next()).toHeaderString() ;
            first = false ;
        }
        return tmp ;
    }
    
    static List multiCombineMT(List list1, List list2)
    {
        List r = new ArrayList() ;
        r.add(list1) ;
        r.add(list2) ;
        Collections.sort(r, new MediaTypeCompare()) ;
        return r ;
    }
    
    
    public MediaRange(String s)
    {
        process1(s) ;
    }
    
    public MediaType getMediaType() { return mediaType ; } 
    
    private void process1(String s)
    {
        String[] x = split(s, ";") ;
        mediaType = new MediaType(x[0]) ;
        
        for ( int i = 1 ; i < x.length ; i++ )
        {
            // Each a parameter
            String z[] = split(x[i], "=") ;
            if ( z.length == 2 )
            {
                this.params.put(z[0], z[1]) ;
                if ( z[0].equals("q") )
                    try {
                        q = Double.parseDouble(z[1]) ;
                    } catch (NumberFormatException ex)
                    {}
            }
            else
                LogFactory.getLog(MediaRange.class).warn("Duff parameter: "+x[i]+" in "+s) ;
        }
    }
    
    /** Format for use in HTTP header
     */
    
    public String toHeaderString()
    {
        StringBuffer b = new StringBuffer() ;
        b.append(mediaType.toString()) ;
        for ( Iterator iter = params.keySet().iterator() ; iter.hasNext() ; )
        {
            String k = (String)iter.next() ;
            String v = (String)params.get(k) ;
            b.append(";") ;
            b.append(k) ;
            b.append("=") ;
            b.append(v) ;
        }
        return b.toString() ;
    }
    
    /** Format to show structure - intentionally different from header
     *  form so you can tell parsing happned correctly
     */  
    
    public String toString()
    {
        StringBuffer b = new StringBuffer() ;
        b.append("[") ;
        b.append(mediaType.toString()) ;
        for ( Iterator iter = params.keySet().iterator() ; iter.hasNext() ; )
        {
            String k = (String)iter.next() ;
            String v = (String)params.get(k) ;
            b.append(" ") ;
            b.append(k) ;
            b.append("=") ;
            b.append(v) ;
        }
        b.append("]") ;
        return b.toString() ;
    }

    
    static String[] split(String s, String splitStr)
    {
        String[] x = s.split(splitStr) ;
        for ( int i = 0 ; i < x.length ; i++ )
        {
            x[i] = x[i].trim() ;
        }
        return x ;
    }
    

    
    
    // Sort - the rightmost element (highest) will be the preferred media type.
    
    public static class MediaTypeCompare implements Comparator
    {
        public int compare(Object arg1, Object arg2)
        {
            if ( ! (arg1 instanceof MediaRange) )
                throw new ClassCastException("Not a MediaType: "+arg1.getClass().getName()) ;
            if ( ! (arg2 instanceof MediaRange) )
                throw new ClassCastException("Not a MediaType: "+arg2.getClass().getName()) ;
            MediaRange mType1 = (MediaRange)arg1 ; 
            MediaRange mType2 = (MediaRange)arg2 ;
            
            int r = Double.compare(mType1.q, mType2.q) ;
            
            if ( r == 0 )
                r = subCompare(mType1.mediaType.type, mType2.mediaType.type) ;
            
            if ( r == 0 )
                r = subCompare(mType1.mediaType.subType, mType2.mediaType.subType) ;
            
            if ( r == 0 )
            {
                // This reverses the input order so that the rightmost elements is the
                // greatest and hence is the first mentioned in the media range.
                
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
    
    
    
    public static void main(String[] argv)
    {
        testOne("ISO-8859-1,utf-8;q=0.7,*;q=0.7") ;
        testOne("text/xml,  application/xml,  application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5") ;
        testOne("text/xml;charset=utf-8") ;
        testOne("text/*,text/plain;x=*,*/*,*/plain,text/*") ;
        testOne("image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*") ;
        testOne(" , a,,  ") ;
        
    }


    public static void testOne(String s)
    {
        List l = MediaRange.multiMediaRange(s) ;
        System.out.println(MediaRange.multiMediaRangeToString(l)) ;
        System.out.println(MediaRange.multiMediaRangeToHeaderString(l)) ;
        System.out.println() ;
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
