/*
 * (c) Copyright 2004, Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.server.http;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.LogFactory;
import org.joseki.Joseki;

/** org.joseki.server.http.HttpUtils
 * 
 * @author Andy Seaborne
 * @version $Id: HttpUtils.java,v 1.1 2004-11-04 15:44:59 andy_seaborne Exp $
 */

public class HttpUtils
{
    static public final String ENC_UTF8 = "UTF-8" ;
    
    public static String chooseMimeType(HttpServletRequest httpRequest)
    {
        String mimeType = Joseki.serverContentType ;
        
        if ( Joseki.serverDebug )
            mimeType = "text/plain" ;
        
        // Decide MIME type.
        // Based on exact match - no */* stuff -
        // so browsers (text/plain) and the Joseki library works (application/???)
        
        // See also: Accept-Charset
        // Currently, we ignore this and just do UTF-8.
        
        Enumeration enum = httpRequest.getHeaders("Accept") ;
        for ( ; enum.hasMoreElements() ; )
        {
            String s = (String)enum.nextElement() ;
            String m = Joseki.getWriterType(s) ;
            if ( m != null )
            {
                mimeType = s ;
                break ;
            }
        }
        
        String acceptCharset = httpRequest.getHeader("Accept-Charset") ;
        if ( acceptCharset != null )
        {
            if ( ! acceptCharset.equalsIgnoreCase(ENC_UTF8) )
                LogFactory.getLog(HttpUtils.class).warn("Accept-Charset: "+acceptCharset) ;
        }
        return mimeType;
    }

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