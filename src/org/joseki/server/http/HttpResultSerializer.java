/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.http;

import java.util.* ;
import org.apache.commons.logging.* ;
import java.io.* ;

import javax.servlet.http.* ; 
import org.joseki.* ;
import org.joseki.util.NullOutputStream; 
import org.joseki.server.*;
import com.hp.hpl.jena.rdf.model.*;

/** Extracting operation data from HTTP servlet requests and formatting results for sending back.
 * 
 * @author      Andy Seaborne
 * @version     $Id: HttpResultSerializer.java,v 1.1 2004-11-03 10:15:02 andy_seaborne Exp $
 */
public class HttpResultSerializer
{
    static Log log = LogFactory.getLog(HttpResultSerializer.class.getName()) ;
    static public final String ENC_UTF8 = "UTF-8" ;
    
    
    public HttpResultSerializer() {}

    /** Send a response.  
     * @param resultModel
     * @param request
     * @param httpRequest
     * @param httpResponse
     * @return true for a successful send, false for any problem (ie.e HTTP repsonse if not 200)
     * @throws IOException
     */
    
    public boolean sendResponse(Model resultModel, Request request,
                             HttpServletRequest httpRequest,
                             HttpServletResponse httpResponse)
        throws IOException
    {
        // Shouldn't be null - should be empty model
        if (resultModel == null)
        {
            log.warn("Result is null pointer for result model") ;
            sendPanic(request, httpRequest, httpResponse, null,
                      "Server internal error: processor returned a null pointer, not a model") ;
            return false;                 
        }

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
                log.warn("Accept-Charset: "+acceptCharset) ;
        }
        
        String writerType = Joseki.getWriterType(mimeType) ; 

        if ( writerType == null )
        {
            // No writer found.  Default it ...
            writerType = Joseki.getWriterType(Joseki.serverContentType) ;
            //logger.warn("MIME type for response if null: force use of "+writerType) ;
        }   
             
        if (false)
        {
            FileOutputStream out = new FileOutputStream("response.n3");
            resultModel.write(out, "N3");
            out.close() ;
        }
        
        if ( false )
        {
            log.info("Result model ("+writerType+")") ;
            StringWriter sw = new StringWriter() ;
            //resultModel.write(sw, writerType);
            resultModel.write(sw, "N3");
            log.info("\n"+sw.toString()) ;
            
        }

        // Write result model.
        // Need to do this robustly.  The model may contain bad URIs
        // which may cause the writer to crash part way though.
        // To check this, we write to a null output sink first.  If this
        // works, we can create a HTTP response.
        
        RDFWriter rdfw = resultModel.getWriter(writerType) ;
        
        if ( writerType.equals("RDF/XML-ABBREV") || writerType.equals("RDF/XML") )
        {
            rdfw.setProperty("showXmlDeclaration", "true") ;

            if ( writerType.equals("RDF/XML-ABBREV") )
                // Workaround for the j.cook.up bug.
                rdfw.setProperty("blockRules", "propertyAttr") ;
        }
        
        // TODO: Allow a mode of write to buffer (memory, disk), write buffer later.
        // Time/space tradeoff.
        try {
            OutputStream out = new NullOutputStream() ;
            rdfw.write(resultModel, out, null) ;
            out.flush() ;
        } catch (Exception ex)
        {
            // Failed to write the model :-(
            log.warn("Exception test writing model: "+ex.getMessage(), ex) ;
            sendPanic(request, httpRequest, httpResponse, ex,
                    "Server internal error: can't write the model.") ;
            return false;
        }
        
        // Managed to write it.

        // Stop caching when debugging!
        if ( Joseki.serverDebug )
        {
            httpResponse.setHeader("Cache-Control", "no-cache") ;
            httpResponse.setHeader("Pragma", "no-cache") ;
        }

        httpResponse.setHeader(Joseki.httpHeaderField, Joseki.httpHeaderValue) ;

        // See: http://www.w3.org/International/O-HTTP-charset.html
        String contentType = mimeType+"; charset=UTF-8" ;
        log.trace("Content-Type for response: "+contentType) ;
        httpResponse.setContentType(contentType) ;
        
        log.trace("HTTP response 200") ;
        rdfw.write(resultModel, httpResponse.getOutputStream(), null) ;
        return true ;
    }

    

    public void sendError(ExecutionException execEx, HttpServletResponse response)
        throws IOException
    {
        int httpRC = -1;
        String httpMsg = ExecutionError.errorString(execEx.returnCode);
        if (execEx.shortMessage != null)
            httpMsg = execEx.shortMessage;

        // Map from internal error codes to HTTP ones.
        switch (execEx.returnCode)
        {
            case ExecutionError.rcOK :
                httpRC = 200;
                break;
            case ExecutionError.rcQueryParseFailure :
                httpRC = HttpServletResponse.SC_BAD_REQUEST;
                break;
            case ExecutionError.rcQueryExecutionFailure :
                httpRC = HttpServletResponse.SC_BAD_REQUEST;
                break;
            case ExecutionError.rcNoSuchQueryLanguage :
                httpRC = HttpServletResponse.SC_NOT_IMPLEMENTED ;
                break;
            case ExecutionError.rcInternalError :
                httpRC = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                break;
            case ExecutionError.rcRDFException :
                httpRC = HttpServletResponse.SC_BAD_REQUEST ;
                break ;
            case ExecutionError.rcNoSuchURI:
                httpRC = HttpServletResponse.SC_NOT_FOUND ;
                break ;
            case ExecutionError.rcSecurityError:
                httpRC = HttpServletResponse.SC_FORBIDDEN ;
                break ;
            case ExecutionError.rcOperationNotSupported:
                httpRC = HttpServletResponse.SC_NOT_IMPLEMENTED ;
                break ;
            case ExecutionError.rcArgumentUnreadable:
                httpRC = HttpServletResponse.SC_BAD_REQUEST ;
                break ;
            case ExecutionError.rcImmutableModel:
                httpRC = HttpServletResponse.SC_METHOD_NOT_ALLOWED ;
                break ;
            case ExecutionError.rcConfigurationError:
                httpRC = HttpServletResponse.SC_INTERNAL_SERVER_ERROR ;
                break ;
            case ExecutionError.rcArgumentError:
                httpRC = HttpServletResponse.SC_BAD_REQUEST ;
            default :
                httpRC = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                break;
        }
        response.setHeader(Joseki.httpHeaderField, Joseki.httpHeaderValue) ;
        response.sendError(httpRC, httpMsg) ;
    }
    
    
    // Things are going very badly 
    private void sendPanic( Request request,
                            HttpServletRequest httpRequest,
                            HttpServletResponse httpResponse,
                            Exception ex,
                            String msg)
        throws IOException
    {
        httpResponse.setContentType("text/plain");
        httpResponse.setHeader(Joseki.httpHeaderField, Joseki.httpHeaderValue);
        httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        PrintWriter pw = httpResponse.getWriter();
        pw.println(msg);
        pw.println();
        pw.println("URI = " + request.getModelURI());
        for (Iterator iter = request.getParams().keySet().iterator(); iter.hasNext();)
        {
            String p = (String)iter.next();
            pw.println(p + " = " + request.getParam(p));
        }
        pw.println() ;
        if ( ex != null )
            ex.printStackTrace(pw) ;
        pw.flush();
        return;
    }
}


/*
 *  (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
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
