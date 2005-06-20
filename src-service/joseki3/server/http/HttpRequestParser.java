/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package joseki3.server.http;

import org.apache.commons.logging.* ;
import java.io.* ;

import javax.servlet.http.* ; 
import org.joseki.* ;
import org.joseki.server.*;
import com.hp.hpl.jena.rdf.model.*;

/** Extracting of information from incomiong HTTP servlet request.
 * 
 * @author      Andy Seaborne
 * @version     $Id$
 */
public class HttpRequestParser
{
    static Log log = LogFactory.getLog(HttpRequestParser.class.getName()) ;
    
    public void setArgs(Request request, HttpServletRequest httpRequest) throws ExecutionException
    {
        try{
            int len = httpRequest.getContentLength();
            if ( len == 0 )
                return ;
                //log.warn("No data supplied - Content-length: "+len) ;
            
            HttpContentType ct = new HttpContentType(httpRequest.getContentType(),
                                                     Joseki.contentTypeXML, Joseki.charsetUTF8) ;
    
            // The reader uses encoding specified by the HTTP Header 
            BufferedReader r = httpRequest.getReader();
    
            // NB Error 406 if content encodings are not possible.
            // HttpServletResponse.SC_NOT_ACCEPTABLE
            
            if ( !ct.getCharset().equalsIgnoreCase(Joseki.charsetUTF8) )
                log.warn("Request charset is "+ct.getCharset()) ;
            
            // The reader may not be ready() (i.e. the body not yet in the local
            // I/O system).  Should we do safe reads into a StringWriter/Reader
            // as Jena readers may be a bit fragile on ready()??
            
            try
            {
                Model model = ModelFactory.createDefaultModel();
                
                model.read(r, "", Joseki.getReaderType(ct.getMediaType()));
                request.addArg(model);
            }
            catch (RDFException rdfEx)
            {
                throw new ExecutionException(
                    ExecutionError.rcArgumentUnreadable,
                    "Argument error",
                    rdfEx.getMessage());
            }
        } 
        catch (IOException ioEx)
        {
            throw new ExecutionException(
                ExecutionError.rcInternalError,
                "IOException",
                "IOExeception: " + ioEx);
        }
        catch (Exception ex)
        {
            log.warn("Exception: " + ex);
            throw new ExecutionException(
                ExecutionError.rcArgumentUnreadable,
                "Argument error",
                null);
        }
//        if ( request.getDataArgs().size() != request.getProcessor().argsNeeded() )
//            log.warn("Failed to get the argument(s)") ;
    }
    
    public void setParameters(Request request, HttpServletRequest httpRequest)
    {
        String s = httpRequest.getQueryString() ;
        // Don't use ServletRequest.getParameter as that reads form data.
        String[] params = s.split("&") ;
        for ( int i = 0 ; i < params.length ; i++ )
        {
            String p = params[i] ;
            String[] x = p.split("=",2) ;
            if ( x.length == 0 )
                request.setParam(p,"" ) ;
            else if ( x.length == 1 )
                request.setParam(x[0], "") ;
            else
                request.setParam(x[0], x[1]) ;
        }
    }
}


/*
 *  (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 *  All rights reserved.
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
