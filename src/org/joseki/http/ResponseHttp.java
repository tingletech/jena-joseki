/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
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
//import com.hp.hpl.jena.query.resultset.XMLOutput;
//import com.hp.hpl.jena.query.resultset.XMLOutputASK;
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
    
    interface OutputContent { void output(ServletOutputStream out) ; }

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
    static final String paramAccept     = "accept" ;
    static final String paramOutput     = "output" ;        // See Yahoo! developer: http://developer.yahoo.net/common/json.html 
    static final String paramCallback   = "callback" ;
    static final String headerAccept    = "Accept" ;
    
    ResponseHttp(Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) 
    { 
        // TODO Move content negotiation to constructor so early failures.
        super(request) ;
        this.httpResponse = httpResponse ;
        this.httpRequest = httpRequest ; 
    }
    
    protected void doResponseModel(Model model) throws QueryExecutionException
    {
        String mimeType = null ;
        String writerMimeType = null ;
        String charset = null ;
        
        // TODO : should this be text/plain?
        String f = httpRequest.getHeader(headerAccept) ;
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
    
    protected void doResponseResultSet(final ResultSet resultSet) throws QueryExecutionException
    {
        boolean wantsStyle = request.containsParam(paramStyleSheet) ;
        String contentType = Joseki.contentTypeResultsXML ;         // Default choice
        
        String acceptField = paramAcceptField() ;
        String outputField = paramOutput() ;
        
        boolean wantsAppXML1 = HttpUtils.accept(acceptField, Joseki.contentTypeXML) ; 
        boolean wantsAppXML2 = HttpUtils.accept(acceptField, Joseki.contentTypeResultsXML) ;
        
        // Test exact match
        boolean wantsAppJSON = 
            acceptField.equalsIgnoreCase(Joseki.contentTypeResultsJSON) ;
        if ( outputField != null && outputField.equals(Joseki.contentOutputJSON) )
            wantsAppJSON = true ;
        
        if ( wantsAppJSON )
        {
            // As JSON
            try {
                contentType = Joseki.contentTypeResultsJSON ;

                if ( outputField != null && outputField.equals(Joseki.contentOutputJSON) )
                    contentType = Joseki.contentTypeTextPlain ;

                jsonOutput(contentType, new OutputContent(){
                    public void output(ServletOutputStream out)
                    {
                        ResultSetFormatter.outputAsJSON(out, resultSet) ;
                    }
                }) ;
            }
            catch (QueryException qEx)
            {
                log.info("Query execution error (SELECT/JSON): "+qEx) ;
                throw new QueryExecutionException(ReturnCodes.rcQueryExecutionFailure, qEx.getMessage()) ;
            }
            catch (IOException ioEx) { log.warn("IOException(ignored) "+ioEx, ioEx) ; }
            return ;
        }
        
        // Not JSON.  If not XML, then send as a model.
        if ( ! ( wantsAppXML1 || wantsAppXML2 ) )
        {
            // As model
            Model m = ResultSetFormatter.toModel(resultSet) ;
            doResponseModel(m) ;
            return ;
        }
        
        // XML result set format.
        
        final String stylesheetURL = paramStylesheet() ;

        // Firefox will prompt if application/sparlq-result+xml 
        if ( stylesheetURL != null )
            contentType = Joseki.contentTypeXML ;
        try {
            output(contentType, null, new OutputContent()
                {
                    public void output(ServletOutputStream out)
                    {
                        ResultSetFormatter.outputAsXML(out, resultSet, stylesheetURL) ;
                    }
                }) ;
        }
        catch (QueryException qEx)
        {
            log.info("Query execution error (SELECT/XML): "+qEx) ;
            throw new QueryExecutionException(ReturnCodes.rcQueryExecutionFailure, qEx.getMessage()) ;
        }
        catch (IOException ioEx) { log.warn("IOException(ignored) "+ioEx, ioEx) ; }

    }
    
    
    protected void doResponseBoolean(final Boolean result) throws QueryExecutionException
    {
        String acceptField = paramAcceptField() ;

        // Test exact match
        boolean wantsAppJSON = acceptField.equalsIgnoreCase(Joseki.contentTypeResultsJSON) ;
        
        try {
            final String stylesheetURL = paramStylesheet() ;
            String contentType = Joseki.contentTypeResultsXML ;
            String charset = null ;
            OutputContent proc = null ;
            
            if ( stylesheetURL != null )
                contentType = Joseki.contentTypeXML ;
            
            if ( wantsAppJSON )
            {
                contentType = Joseki.contentTypeResultsJSON ;
                charset = Joseki.charsetUTF8 ;
                proc = new OutputContent(){
                    public void output(ServletOutputStream out)
                    {
                        ResultSetFormatter.outputAsJSON(out, result.booleanValue()) ;
                    }
                };
            }
            else
            {
                proc = new OutputContent(){
                    public void output(ServletOutputStream out)
                    {
                        ResultSetFormatter.outputAsXML(out, result.booleanValue(), stylesheetURL) ;
                    }
                };
            }   
             
            output(contentType, charset, proc) ;  
          } 
          catch (QueryException qEx)
          {
              log.info("Query execution error (ASK): "+qEx) ;
              throw new QueryExecutionException(ReturnCodes.rcQueryExecutionFailure, null) ;
          }
          catch (IOException ioEx) { log.warn("IOException(ignored) "+ioEx, ioEx) ; }
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
    
    private void output(String contentType, String charset, OutputContent proc)  throws IOException
    {
        ser.setHttpResponse(httpRequest, httpResponse, contentType, charset);  
        httpResponse.setStatus(HttpServletResponse.SC_OK) ;
        httpResponse.setHeader(Joseki.httpHeaderField, Joseki.httpHeaderValue);
        ServletOutputStream out = httpResponse.getOutputStream() ;
        proc.output(out) ;
        out.flush() ;
        httpResponse.flushBuffer();
    }

    private void jsonOutput(String contentType, OutputContent proc) throws IOException
    {
        String callback = paramCallback() ;
        String outputField = paramOutput() ;
        String jsonRef = null ;
        ServletOutputStream out = httpResponse.getOutputStream() ;
        
        if ( callback != null )
        {
            int i = callback.indexOf(".obj") ;
            
            if ( i > 0 )
            {
                jsonRef = callback.substring(i+".obj".length()) ;
                callback = callback.substring(0,i) ;
            }
            out.print(callback) ;
            out.println("(") ;
        }
        
        output(contentType, Joseki.charsetUTF8, proc) ;
        
        if ( callback != null )
        {
            out.print(")") ;
            if ( jsonRef != null )
                out.print(jsonRef) ;
            out.println() ;
        }
        out.flush() ;
        httpResponse.flushBuffer();
    }

    private String paramStylesheet() { return fetchParam(paramStyleSheet) ; }
    
    private String paramOutput() { return fetchParam(paramOutput) ; }
    
    private String paramAcceptField()
    {
        String acceptField = httpRequest.getHeader(headerAccept) ;
        String acceptParam = fetchParam(paramAccept) ;
        
        if ( acceptParam != null )
            acceptField = acceptParam ;
        
        if (acceptField == null )
            return null ;
        
        // Catch an easy mistake to make.
        if ( acceptField.contains(" ") )
        {
            log.warn("The accept parameter value has a space in it - did you mean '+'?");
            log.warn("You need to use %2B - '+' is the encoding of a space") ;  
        }
        // Some short names.
        if ( acceptField.equalsIgnoreCase(Joseki.contentOutputJSON) ) 
            acceptField = Joseki.contentTypeResultsJSON ;

        return acceptField ; 
    }
    
    private String paramCallback() { return fetchParam(paramCallback) ; }
    
    private String fetchParam(String parameterName)
    {
        String value = null ;
        if ( request.containsParam(parameterName) )
        {
            value = request.getParam(parameterName) ;
            if ( value != null )
            {
                value = value.trim() ;
                if ( value.length() == 0 )
                    value = null ;
            }
        }
        return value ;
    }
}

/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
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