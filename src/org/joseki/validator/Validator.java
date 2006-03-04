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
import com.hp.hpl.jena.query.util.IndentedLineBuffer;
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
    
    static final String paramLineNumbers = "linenumbers" ;
    static final String paramQuery = "query" ;
    static final String respService = "X-Service" ;
    
    private void validationRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        try {
            if ( log.isDebugEnabled() )
                log.debug("validation request") ;
            
            String[] args = httpRequest.getParameterValues(paramQuery) ;
            
            if ( args == null || args.length == 0 )
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
            
            String lineNumbersArg = httpRequest.getParameter(paramLineNumbers) ; 
            
            boolean lineNumbers = true ;
            
            if ( lineNumbersArg != null )
                lineNumbers = lineNumbersArg.equalsIgnoreCase("true") || lineNumbersArg.equalsIgnoreCase("yes") ;
            
            // Headers
            httpResponse.setCharacterEncoding("UTF-8") ;
            httpResponse.setContentType("text/html") ;
            httpResponse.setHeader(respService, "Joseki/ARQ SPARQL Validator: http://jena.sourceforge.net/ARQ") ;
            
            ServletOutputStream outStream = httpResponse.getOutputStream() ;

            outStream.println("<html>") ;
            printHead(outStream) ;
            
            // ********* Need to encode < and >
            
            outStream.println("<body>") ;
            outStream.println("<h1>SPARQL Validator</h1>") ;
            // Print query as received
            {
                String prefix = "    " ;
                outStream.println("<p>Input:</p>") ;
                startFixed(outStream) ;
                if ( false )
                {
                    columns(prefix, outStream) ;
                    outStream.println() ;
                }
                IndentedLineBuffer buff = new IndentedLineBuffer(lineNumbers) ; 
                
                IndentedWriter out = buff.getIndentedWriter() ; 
                out.print(queryString) ;
                out.flush() ;
                outStream.print(htmlQuote(buff.asString())) ;
                finishFixed(outStream) ;
            }
            
            // Attempt to parse it.
            Query query = null ;
            try {
                query = QueryFactory.create(queryString) ;
            } catch (QueryParseException ex)
            {
                outStream.println("<p>Syntax error:</p>") ;
                startFixed(outStream) ;
                outStream.println(ex.getMessage()) ;
                finishFixed(outStream) ;
            }
            
            // OK?  Pretty print
            if ( query != null )
            {
                outStream.println("<p>Formatted, parsed query:</p>") ;

                String prefix = "    " ;
                startFixed(outStream) ;
//                columns(prefix, outStream) ;
                IndentedLineBuffer buff = new IndentedLineBuffer(lineNumbers) ; 
                IndentedWriter out = buff.getIndentedWriter() ;
                query.serialize(out) ;
                out.flush() ;
                outStream.print(htmlQuote(buff.asString())) ;
                finishFixed(outStream) ;
            }
            
            //Report deatils
            outStream.println("</html>") ;
            
        } catch (Exception ex)
        {
            log.warn("Exception in doGet",ex) ;
        }
    }
    
    private String htmlQuote(String str)
    {
        str = str.replace("<", "&lt;") ;
        str = str.replace(">", "&gt;") ;
        return str ;

    }

    private void printHead(ServletOutputStream outStream) throws IOException
    {
        outStream.println("<head>") ;
        outStream.println(" <title>SPARQL Validator Report</title>") ;
        outStream.println(" <style>") ;
        outStream.println(" hr { border:0 ; width:90%; heigh: 1px; }") ;
        // Sort out!
        outStream.println("pre {") ;
        outStream.println("    font-family: monospace; font-size: 10pt ;") ;
        outStream.println("    line-height: 14pt ;") ;
        outStream.println("    margin-top: 1 ; margin-bottom: 1 ;") ;
        outStream.println("    margin-left: 5ex ;") ;
        outStream.println("    }") ;
        outStream.println(".box {") ; 
        outStream.println("    margin-left : 5% ; margin-right :    5% ; ") ;
        outStream.println("    border: solid ; border-width: 1pt; ") ; 
        outStream.println("    background-color: #F0F0F0; padding: 2mm;") ;
        outStream.println("    page-break-inside: avoid ;") ;
        outStream.println("    }") ;
        
        outStream.println(" </style>") ;
        //outStream.println() ;
        outStream.println("</head>") ;
    }

    private void startFixed(ServletOutputStream outStream) throws IOException
    {
        outStream.println("<pre class=\"box\">") ;
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