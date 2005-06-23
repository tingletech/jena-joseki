/*
 * (c) Copyright 2004, 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;
//import java.util.* ;

/** apps.ParseHttpHeaders
 * 
 * @author Andy Seaborne
 * @version $Id: ParseHttpHeaders.java,v 1.1 2005-06-23 10:16:16 andy_seaborne Exp $
 */

public class ParseHttpHeaders
{
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
    
    // Example: 
    // Accept           = text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5
    // Accept-Language  = en-us,en;q=0.5
    // Accept-Encoding  = gzip,deflate
    // Accept-Charset   = ISO-8859-1,utf-8;q=0.7,*;q=0.7
    
    String mediaType = null;
    String params[] = null;
    String charset = null;
    
    private static void parse(String s)
    {
        String[] x = {",", ";", "="} ;
        parse(s, 0, x) ;
    }
    
    private static void parse(String s, int index, String[] splits)
    {
        if ( index >= splits.length)
            return ;

        String indent = "" ;
        for ( int k = 0 ; k < index ; k++ )
            indent = indent+"  " ;
        //System.out.println(indent+"Parse("+splits[index]+"): "+s) ;
        
        String[] x = s.split(splits[index]) ;
        if ( x.length == 1 )
        {
            //System.out.println(indent+"  End: "+s) ;
            return ;
        }
        
        for ( int i = 0 ; i < x.length ; i++ )
        {
            System.out.println(indent+":"+x[i]+":") ;
            parse(x[i], index+1, splits) ;
        }
    }
        
    
    public static void main(String[] argv)
    {
        //parse("ISO-8859-1,utf-8;q=0.7,*;q=0.7") ;
        parse("text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5") ;
    }
        
//        
//        
//        if (s == null)
//            return;
//
//        int j = s.indexOf(';');
//        if (j == -1)
//        {
//            mediaType = s.trim() ;
//            return ;
//        }
//            
//        mediaType = s.substring(0, j).trim();
//        String sParam = s.substring(j + 1) ;
//        
//        params = s.split(";") ;
//        for ( int i = 0 ; i < params.length ; i++ )
//        {
//            params[i] = params[i].trim();
//
//            if ( params[i].matches("charset\\s*=.*") )
//            {    
//                int k = params[i].indexOf('=') ;
//                charset = params[i].substring(k+1).trim() ;
//            }
//        }
//    }
    
}

/*
 * (c) Copyright 2004, 2005 Hewlett-Packard Development Company, LP
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