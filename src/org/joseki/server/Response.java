/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;
import org.apache.commons.logging.* ;
//import java.io.IOException;

import org.joseki.server.http.HttpResultSerializer ;
import org.joseki.server.http.HttpUtils ;

import com.hp.hpl.jena.rdf.model.Model; 

//import javax.servlet.ServletOutputStream; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Abstaction of an operation response
 * @author      Andy Seaborne
 * @version     $Id: Response.java,v 1.5 2004-11-12 16:41:47 andy_seaborne Exp $
 */
public class Response extends ExecutionError
{
    static Log log = LogFactory.getLog(Response.class) ;
    String mimeType = null ;
    int responseCode = rcOK ;
    String responseMessage = null ;
    
    Request request ;
    // Note whether we have started writing the reply.
    boolean committed = false ;
    HttpServletRequest httpRequest = null ;
    HttpServletResponse httpResponse = null ;
    
    public Response(Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        this.request = request ;
        this.httpRequest = httpRequest ;
        this.httpResponse = httpResponse ;
//        try { output = resp.getOutputStream() ;}
//        catch (IOException ex)
//        { LogFactory.getLog(Response.class).fatal("Can't get ServletOutputStream", ex) ; }
    }
    
    public void startResponse() { committed = true ;}
    public void finishResponse() {}
    
    
    public void serialize(Model m)
    {
        HttpResultSerializer ser = new HttpResultSerializer() ;
        try {
            ser.sendResponse(m, null, httpRequest, httpResponse) ;
        } catch (Exception ex)
        {
            try {
                log.warn("Internal server error", ex) ;
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR) ;
                httpResponse.flushBuffer() ;
                httpResponse.getOutputStream().close() ;
            } catch (Exception e) {}
        }        
    }

    public void serialize(ExecutionException ex) throws ExecutionException 
    {
        HttpResultSerializer ser = new HttpResultSerializer() ;
        try {
            ser.sendError(ex, httpResponse) ;
        } catch (Exception ex2)
        {
            try {
                log.warn("Internal server error", ex2) ;
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR) ;
                httpResponse.flushBuffer() ;
                httpResponse.getOutputStream().close() ;
            } catch (Exception e) {}
        }    
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
        if ( committed )
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
 
