/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.validator;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.util.IndentedWriter;

public class Validator extends HttpServlet 
{
    protected static Log log = LogFactory.getLog("Validator") ;
    
    public Validator() 
    {
        log.info("-------- Validator") ;
    }

    public void init() throws ServletException
    { super.init() ; }

    public void init(ServletConfig config) throws ServletException
    { super.init(config) ; }
        
 
    public void destroy()
    {
        log.debug("destroy");
    }

    // Intercept incoming requests.
    /*
    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        super.service(req, resp);
    }
    */
    
    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    { validationRequest(httpRequest, httpResponse) ; }

    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    { validationRequest(httpRequest, httpResponse) ; }

    static final String paramQuery = "query" ;
    static final String respService = "X-Service" ;
    
    private void validationRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        try {
            if ( log.isDebugEnabled() )
                log.debug("validation request") ;
            
            String[] args = httpRequest.getParameterValues(paramQuery) ;
            
            if ( args.length == 0 )
            {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "No query parameter to validator") ;
                return ;
            }
            
            if ( args.length > 1 )
            {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Too many query parameters") ;
                return ;
            }

            String queryString = httpRequest.getParameter(paramQuery) ;
            queryString = queryString.replace("\r\n", "\n") ;
            
            
            // Headers
            httpResponse.setCharacterEncoding("UTF-8") ;
            httpResponse.setContentType("text/html") ;
            httpResponse.setHeader(respService, "Joseki/ARQ SPARQL Validator: http://jena.sourceforge.net/ARQ") ;
            
            ServletOutputStream outStream = httpResponse.getOutputStream() ;

            outStream.println("<html>") ;
            printHead(outStream) ;
            
            // ********* Need to encode < and >
            
            outStream.println("<body>") ;
            // Print query as received
            {
                String prefix = "    " ;
                startFixed(outStream) ;
                columns(prefix, outStream) ;
                IndentedWriter out = new IndentedWriter(outStream, true) ;
                out.incIndent(prefix.length()) ;
                out.print(queryString) ;
                out.decIndent(prefix.length()) ;
                out.flush() ;
                finishFixed(outStream) ;
                out = null ;
            }
            outStream.println("<br/>") ;
            Query query = null ;
            try {
                query = QueryFactory.create(queryString) ;
            } catch (QueryParseException ex)
            {
                startFixed(outStream) ;
                outStream.println(ex.getMessage()) ;
                finishFixed(outStream) ;
            }
            
            // OK?  Pretty print
            if ( query != null )
            {
                outStream.println("<hr/>") ;
                String prefix = "    " ;
                startFixed(outStream) ;
//                columns(prefix, outStream) ;
                IndentedWriter out = new IndentedWriter(outStream, true) ;
                out.incIndent(prefix.length()) ;
                query.serialize(out) ;
                out.decIndent(prefix.length()) ;
                out.flush() ;
                finishFixed(outStream) ;
                out = null ;
            }
            
            //Report deatils
            outStream.println("</html>") ;
            
        } catch (Exception ex)
        {
            log.warn("Exception in doGet",ex) ;
        }
    }
    
    private void printHead(ServletOutputStream outStream) throws IOException
    {
        outStream.println("<html>") ;
        outStream.println("<head>") ;
        outStream.println(" <title>SPARQL Validator Report</title>") ;
        //outStream.println() ;
        outStream.println("</head>") ;
    }

    private void startFixed(ServletOutputStream outStream) throws IOException
    {
        outStream.println("<pre>") ;
    }

    private void columns(String prefix, ServletOutputStream outStream) throws IOException
    {
        outStream.print(prefix) ;
        outStream.println("         1         2         3         4         5         6         7") ;
        outStream.print(prefix) ;
        outStream.println("12345678901234567890123456789012345678901234567890123456789012345678901234567890") ;
    }
    
    private void finishFixed(ServletOutputStream outStream) throws IOException
    {
        outStream.println("</pre>") ;
    }
}

/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
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