/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.joseki;

import org.apache.commons.logging.* ;
import java.net.* ;
import java.io.* ;

import com.hp.hpl.jena.rdf.model.*;
import org.joseki.*;
import org.joseki.server.http.HttpContentType;

/** Common code for performing an HTTP operation (not query) on a remote model.
 *  
 *  @see HttpAdd
 *  @see HttpRemove
 *  @see HttpPing
 *  @see HttpOptions
 *  @see HttpQuery
 * @author      Andy Seaborne
 * @version     $Id: HttpExecute.java,v 1.3 2004-11-25 18:21:58 andy_seaborne Exp $
 */

public class HttpExecute
{
    private static Log log = LogFactory.getLog(HttpExecute.class.getName()) ;

    private URL url = null ;

    private String requestMethod = null ;
    private boolean doOutput = true ;
    private boolean hasExecuted = false ;

    static final String ENC_UTF8 = "UTF-8" ;

    /** Usual way to contruct the request
     * @param target
     * @param opName
     * @throws MalformedURLException
     */ 

    protected HttpExecute(String target, String opName) throws MalformedURLException
    { init(target, opName) ; }
    
    /** Allow 2 stage constructor because subclasses may wish to
     *  compute before setting the URL (@see setURL)
     */
    protected HttpExecute() { }
    
    protected void init(String target, String opName)
        throws MalformedURLException
    {
        url = new URL(target+"?op="+opName) ;  
    }
    
    // Special: allow the HTTP verb to be changed.
    protected void setRequestMethod(String rMethod, boolean _doOutput)
    {
        requestMethod = rMethod ;
        doOutput = _doOutput ;
    }
    
    // Special: allow the URL to be directly set.
    protected void setURL(String urlStr) throws MalformedURLException
    {
        url = new URL(urlStr) ;
    }
    
    public Model exec() throws HttpException
    {
        HttpURLConnection conn = null ;
        try
        {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", Joseki.contentTypeRDFXML+", "+
                                              Joseki.contentTypeN3) ;
            conn.setRequestProperty("Accept-Charset", ENC_UTF8) ;
            
            if ( requestMethod != null )
                conn.setRequestMethod(requestMethod) ;
            conn.setDoInput(true);
            
            if ( doOutput )
            {
                conn.setRequestProperty("Content-Type", //Joseki.clientContentType);
                                                      Joseki.clientContentType+ "; charset="+ENC_UTF8) ;
                // Maybe we should set the charset.
                // At the moment, we leave it to the onSend operation to
                // do the right thing.
                conn.setDoOutput(true);
                onSend(Joseki.clientContentType, conn.getOutputStream());
                conn.getOutputStream().flush() ;
            }
            conn.connect();
        }
        catch (java.net.ConnectException connEx)
        {
            throw new HttpException(-1, "Failed to connect to remote server");
        }

        catch (IOException ioEx)
        {
            throw new HttpException(ioEx);
        }
        catch (RDFException rdfEx)
        {
            throw new HttpException(rdfEx) ;
        }

        try {        
            int responseCode = conn.getResponseCode() ;
            String responseMessage = conn.getResponseMessage() ;
            
            // 1xx: Informational 
            // 2xx: Success 
            // 3xx: Redirection 
            // 4xx: Client Error 
            // 5xx: Server Error 
            
            if ( 300 <= responseCode && responseCode < 400 )
            {
                throw new HttpException(responseCode, responseMessage) ;
            }
            
            // Other 400 and 500 - errors 
            
            if ( responseCode >= 400 )
            {
                throw new HttpException(responseCode, responseMessage) ;
            }
  
            // Request suceeded
            // Result coming back is a model
            Model resultModel = onResult(conn.getContentType(),  conn.getInputStream()) ;
            return resultModel ;
        }
        catch (IOException ioEx)
        {
            log.info("IOException after connect: "+ioEx) ;
            throw new HttpException(ioEx) ;
        } 
        catch (RDFException rdfEx)
        {
            log.info("RDFException(result): "+rdfEx) ;
            throw new HttpException(rdfEx) ;
        }
    }

    /** Called when output to the POST is needed.
     * Content should be written with UTF-8.
     * @param mediaType
     * @param out
     */
    
    protected void onSend(String mediaType, OutputStream out) { return ; }
    
    protected Model onResult(String mediaType, InputStream in)
    {
        HttpContentType cType = new HttpContentType(mediaType) ;
        
        // Normal result is a model.
        Model resultModel = ModelFactory.createDefaultModel() ;
        String rType = Joseki.getWriterType(cType.getMediaType()) ;
        
        if ( false )
        {
            try 
            {
                log.trace("Reader type: "+rType) ;

                byte b[] = new byte[1024] ;
                ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
                while(true)
                {   
                    int len = in.read(b) ;
                    if ( len == -1 )
                        break ;
                    bout.write(b, 0, len) ;
                }
                // Crude.
                log.trace("\n"+bout.toString()) ;
                
                // reset to reread
                in = new ByteArrayInputStream(bout.toByteArray()) ;
            }        
            catch (IOException ioEx) { System.err.println("IOException: "+ioEx) ; return null ; }
        }
        resultModel.read(in, "http://somewhere/", Joseki.getWriterType(cType.getMediaType())) ;
        hasExecuted = true ;
        return resultModel ;
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

