/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;
import java.io.IOException;

import org.apache.commons.logging.* ;
//import java.io.IOException;

import org.joseki.Joseki ;
import org.joseki.server.http.HttpResultSerializer ;
import org.joseki.server.http.HttpUtils ;

import com.hp.hpl.jena.rdf.model.Model; 
import com.hp.hpl.jena.shared.JenaException;

//import javax.servlet.ServletOutputStream; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Abstaction of an operation response
 * @author      Andy Seaborne
 * @version     $Id: Response.java,v 1.10 2004-11-14 18:38:59 andy_seaborne Exp $
 */
public class Response extends ExecutionError
{
    // This is really "ResponseHttp" - will become that and have an interface for Response. 
    static Log log = LogFactory.getLog(Response.class) ;
    String mimeType = null ;
    int responseCode = rcOK ;
    String responseMessage = null ;
    
    Request request ;
    boolean responseCommitted = false ;
    boolean responseSent = false ;
    
    HttpServletRequest httpRequest = null ;
    HttpServletResponse httpResponse = null ;
    
    public Response(Request request,
                    HttpServletRequest httpRequest,
                    HttpServletResponse httpResponse)
    {
        this.request = request ;
        this.httpRequest = httpRequest ;
        this.httpResponse = httpResponse ;
//        try { output = resp.getOutputStream() ;}
//        catch (IOException ex)
//        { LogFactory.getLog(Response.class).fatal("Can't get ServletOutputStream", ex) ; }
    }
    
    public void startResponse() { responseCommitted = true ; }
    public void finishResponse() { responseSent = true ; }
    
    // TODO Tidy up
    
    public void doResponse(Model resultModel)
    {
        if ( this.responseSent )
        {
            log.fatal("doResponse: Response already sent: "+request.getRequestURL()) ;
            return ;
        }
        
        HttpResultSerializer ser = new HttpResultSerializer() ;
        if (resultModel == null)
        {
            log.warn("Result is null pointer for result model") ;
            ser.sendPanic(request, httpRequest, httpResponse, null,
                      "Server internal error: processor returned a null pointer, not a model") ;
            return ;                  
        }

        String mimeType = HttpUtils.chooseMimeType(httpRequest);

        ser.setHttpResponse(httpRequest, httpResponse, mimeType);        
        try {
            try {
                ser.writeModel(resultModel, request, httpRequest, httpResponse, mimeType) ;
            }
            catch (JenaException jEx)
            {
                //msg(Level.WARNING, "RDFException", rdfEx);
                log.warn("JenaException", jEx);
                //printStackTrace(printName + "(execute)", rdfEx);
                httpResponse.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "JenaException: " + jEx);
                return;
            } catch (Exception ex)
            {
                try {
                    log.warn("Internal server error", ex) ;
                    httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR) ;
                } catch (Exception e) {}
            }
        } catch (IOException ioEx)
        {
            //msg(Level.WARNING,"IOException in normal response") ;
            log.warn("IOException in normal response") ;
            try {
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                httpResponse.flushBuffer();
                httpResponse.getWriter().close();
            } catch (Exception e) { }
        }
        responseSent = true ;
    }

    public void doException(ExecutionException execEx) 
    {
        if ( this.responseSent )
        {
            log.fatal("doException: Response already sent: "+request.getRequestURL()) ;
            return ;
        }
        
        HttpResultSerializer httpSerializer = new HttpResultSerializer() ;
        httpSerializer.setHttpResponse(httpRequest, httpResponse, null) ;
        
        String httpMsg = ExecutionError.errorString(execEx.returnCode);
        //msg("Error in operation: URI = " + uri + " : " + httpMsg);
        log.info("Error: URI = " + request.getModelURI() + " : " + httpMsg);
        httpSerializer.sendError(execEx, httpResponse) ;
        responseSent = true ;
    }
    
    // Desparate way to reply
    
    protected void doPanic(int reason)
    {
        if ( this.responseSent )
        {
            log.fatal("doPanic: Response already sent: "+request.getRequestURL()) ;
            return ;
        }
        try {                
            httpResponse.setHeader(Joseki.httpHeaderField, Joseki.httpHeaderValue) ;
            httpResponse.setStatus(reason) ;
            httpResponse.flushBuffer() ;
            httpResponse.getWriter().close() ;
        } catch (Exception e) {}
        responseSent = true ;

    }

    
    /**
     * @return Returns the mimeType.
     */
    public String getMimeType()
    {
        return mimeType;
    }
    /**
     * @param mimeType The mimeType to set.
     */
    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }
    

    /**
     * @return Returns the responseCode.
     */
    public int getResponseCode()
    {
        return responseCode;
    }
    /**
     * @param responseCode The responseCode to set.
     */
    public void setResponseCode(int responseCode)
    {
        if ( responseCommitted )
        {
            log.warn("Response started - can't set responseCode to "+
                     HttpUtils.httpResponseCode(responseCode)) ;
            return ;
        }
        this.responseCode = responseCode;
    }
    /**
     * @return Returns the responseMessage.
     */
    public String getResponseMessage()
    {
        return responseMessage;
    }
    /**
     * @param responseMessage The responseMessage to set.
     */
    public void setResponseMessage(String responseMessage)
    {
        this.responseMessage = responseMessage;
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
 
