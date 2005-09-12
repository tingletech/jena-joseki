/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.http;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.resultset.XMLOutput;
import com.hp.hpl.jena.query.resultset.XMLOutputASK;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.JenaException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joseki.*;


public class ResponseHttp extends Response
{
    // TODO ResponseCallback 
    // Pass in as anon call with its own ref to the query execution to free
    
    private static Log log = LogFactory.getLog(ResponseHttp.class) ;
    HttpResultSerializer ser = new HttpResultSerializer() ;
    
    static AcceptItem defaultCharset      = new AcceptItem(Joseki.charsetUTF8) ;
    static AcceptList prefCharset         = new AcceptList("utf-8") ;
    
    // These EXCLUDE application/xml
    static AcceptItem defaultContentType  = new AcceptItem(Joseki.contentTypeRDFXML) ;
    
    static String[] x = { //Joseki.contentTypeXML ,
                          Joseki.contentTypeRDFXML ,
                          Joseki.contentTypeTurtle ,
                          Joseki.contentTypeAppN3 ,
                          Joseki.contentTypeTextN3 ,
                          Joseki.contentTypeNTriples } ;

    static AcceptList prefContentType     = new AcceptList(x) ;

    private HttpServletResponse httpResponse ;
    private HttpServletRequest httpRequest ; 
    
    static final String paramStyleSheet = "stylesheet" ;

    ResponseHttp(Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) 
    { 
        super(request) ;
        this.httpResponse = httpResponse ;
        this.httpRequest = httpRequest ; 
    }
    
    protected void doResponseModel(Model model) throws QueryExecutionException
    {
        // TODO Move content negotiation to constructor so early failures.
        
        String mimeType = null ;
        String writerMimeType = null ;
        String charset = null ;
        
        // TODO : should this be text/plain?
        String f = httpRequest.getHeader("Accept") ;
        String textContentType =  HttpUtils.match(f, "text/*") ; 

        if ( textContentType != null )
        {
            // Send to a browser.  Send as whatever the default type is for the 
            writerMimeType = Joseki.contentTypeForText ;
            mimeType = Joseki.contentTypeForText ;
            log.debug("MIME type (text-like): "+writerMimeType) ;
        }
        
        if ( mimeType == null )
        {
            AcceptItem i = HttpUtils.chooseContentType(httpRequest, prefContentType, defaultContentType) ; 
            mimeType = i.getAcceptType() ;
        }
        
        if ( charset == null )
        {
            AcceptItem i = HttpUtils.chooseCharset(httpRequest,  prefCharset, defaultCharset) ;
            charset = i.getAcceptType() ;
        }
        
        if ( writerMimeType == null )
            writerMimeType = mimeType ;
        
        ser.setHttpResponse(httpRequest, httpResponse, mimeType, charset);   
        
        try {
            try {
                ser.writeModel(model, request, httpRequest, httpResponse, writerMimeType) ;
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
    }
    
    protected void doResponseResultSet(ResultSet resultSet) throws QueryExecutionException
    {
        String f = httpRequest.getHeader("Accept") ;
        boolean wantsAppXML = HttpUtils.accept(f, "application/xml") ; 
        
        if ( ! wantsAppXML )
        {
            // As model
            Model m = ResultSetFormatter.toModel(resultSet) ;
            doResponseModel(m) ;
            return ;
        }
        
        String stylesheetURL = null ;
        if ( request.containsParam(paramStyleSheet) )
        {
            stylesheetURL = request.getParam(paramStyleSheet) ;
            if ( stylesheetURL != null )
            {
                stylesheetURL = stylesheetURL.trim() ;
                if ( stylesheetURL.length() == 0 )
                    stylesheetURL = null ;
            }
        }
        
        try {
            ser.setHttpResponse(httpRequest, httpResponse, Joseki.contentTypeXML, null);  
            httpResponse.setStatus(HttpServletResponse.SC_OK) ;
            httpResponse.setHeader(Joseki.httpHeaderField, Joseki.httpHeaderValue);
            ServletOutputStream out = httpResponse.getOutputStream() ;
            XMLOutput xOut = new XMLOutput() ;
            xOut.setStylesheetURL(stylesheetURL) ;
            xOut.setIncludeXMLinst(true) ;
            xOut.format(out, resultSet) ;
            out.flush() ;
            httpResponse.flushBuffer();
        }
        catch (QueryException qEx)
        {
            log.info("Query execution error (SELECT/XML): "+qEx) ;
            throw new QueryExecutionException(ReturnCodes.rcQueryExecutionFailure, qEx.getMessage()) ;
        }
        catch (IOException ioEx)
        {
            log.warn("IOExceptionecution "+ioEx) ;
        }
//        catch (NotFoundException ex)
//        {
//            log.info("Query execution error (SELECT/XML): "+ex) ;
//            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, ex.getMessage()) ;
//        }
    }
    
    
    protected void doResponseBoolean(Boolean result) throws QueryExecutionException
    {
        try {
            ser.setHttpResponse(httpRequest, httpResponse, Joseki.contentTypeXML, null) ;
            httpResponse.setStatus(HttpServletResponse.SC_OK) ;
            httpResponse.setHeader(Joseki.httpHeaderField, Joseki.httpHeaderValue);
            
            ServletOutputStream outStream = httpResponse.getOutputStream() ;
            XMLOutputASK fmt = new XMLOutputASK(outStream, null) ;
            fmt.exec(result.booleanValue()) ;
            outStream.flush() ;
          } 
          catch (QueryException qEx)
          {
              log.info("Query execution error (ASK): "+qEx) ;
              throw new QueryExecutionException(ReturnCodes.rcQueryExecutionFailure, null) ;
          }
          catch (IOException ioEx)
          {
              log.warn("IOExceptionecution "+ioEx) ;
          }
      }

    protected void doException(ExecutionException execEx)
    {
        HttpResultSerializer httpSerializer = new HttpResultSerializer() ;
        httpSerializer.setHttpResponse(httpRequest, httpResponse, null, null) ;
        
        String httpMsg = execEx.shortMessage ;
        if (execEx.shortMessage == null)
            httpMsg = ReturnCodes.errorString(execEx.returnCode);;

            //msg("Error in operation: URI = " + uri + " : " + httpMsg);
        log.info("Error: URI = " + request.getServiceURI() + " : " + httpMsg) ;
        httpSerializer.sendError(execEx, httpResponse) ;
    }
}

/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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