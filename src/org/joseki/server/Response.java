/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;
import java.io.* ;

/** Abstaction of an operation response
 * @author      Andy Seaborne
 * @version     $Id: Response.java,v 1.2 2004-11-09 11:33:27 andy_seaborne Exp $
 */
public class Response
{
    String mimeType = null ;
    OutputStream output ;
    int responseCode = ExecutionError.rcOK ;
    String responseMessage = null ;
    
    
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
     * @return Returns the output.
     */
    public OutputStream getOutput()
    {
        return output;
    }
    /**
     * @param output The output to set.
     */
    public void setOutput(OutputStream output)
    {
        this.output = output;
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
 
