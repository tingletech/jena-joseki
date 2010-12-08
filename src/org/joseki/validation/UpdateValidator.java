/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * (c) Copyright 2010 Epimorphics Ltd. 
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.validation;

import java.io.IOException ;

import javax.servlet.ServletConfig ;
import javax.servlet.ServletException ;
import javax.servlet.ServletOutputStream ;
import javax.servlet.http.HttpServlet ;
import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpServletResponse ;

import org.openjena.atlas.io.IndentedLineBuffer ;
import org.openjena.atlas.io.IndentedWriter ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.query.Syntax ;
import com.hp.hpl.jena.sparql.ARQException ;
import com.hp.hpl.jena.update.UpdateFactory ;
import com.hp.hpl.jena.update.UpdateRequest ;

public class UpdateValidator extends HttpServlet 
{
    protected static Logger log = LoggerFactory.getLogger("Update Validator") ;
    
    public UpdateValidator() 
    {
        log.info("-------- Update Validator") ;
    }

    @Override
    public void init() throws ServletException
    { super.init() ; }

    @Override
    public void init(ServletConfig config) throws ServletException
    { super.init(config) ; }
        
 
    @Override
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
    
    @Override
    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    { validationRequest(httpRequest, httpResponse) ; }

    @Override
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    { validationRequest(httpRequest, httpResponse) ; }
    
    static final String paramLineNumbers      = "linenumbers" ;
    static final String paramFormat           = "outputFormat" ;
    static final String paramUpdate            = "update" ;
    static final String paramSyntax           = "languageSyntax" ;
    //static final String paramSyntaxExtended   = "extendedSyntax" ;
    static final String respService           = "X-Service" ;
    
    private void validationRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        try {
            if ( log.isDebugEnabled() )
                log.debug("validation request") ;
            
            String[] args = httpRequest.getParameterValues(paramUpdate) ;
            
            if ( args == null || args.length == 0 )
            {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "No update parameter to validator") ;
                return ;
            }
            
            if ( args.length > 1 )
            {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Too many update parameters") ;
                return ;
            }

            final String updateString = httpRequest.getParameter(paramUpdate).replaceAll("(\r|\n| )*$", "") ;

            // TEMP Default to ARQ update syntax, not strict SPARQL 1.1
            Syntax language = Syntax.syntaxARQ ;

            String updateSyntax = httpRequest.getParameter(paramSyntax) ;
            if ( updateSyntax != null && ! updateSyntax.equals("") )
            {
                updateSyntax = "SPARQL" ;
                Syntax language2 = Syntax.lookup(updateSyntax) ; 
                if ( language2 == null )
                {
                    httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown syntax: "+updateSyntax) ;
                    return ;
                }
                language = language2 ;
            }
            
            String lineNumbersArg = httpRequest.getParameter(paramLineNumbers) ; 
            String a[] = httpRequest.getParameterValues(paramFormat) ;
            
            // Currentl default.
            boolean outputSPARQL = true ;
            boolean outputPrefix = false ;
            boolean outputAlgebra = false ;
            boolean outputQuads = false ;
            
            if ( a != null )
            {
                outputSPARQL = false ;
                for ( int i = 0 ; i < a.length ; i++ )
                {
                    if ( a[i].equals("sparql") ) 
                        outputSPARQL = true ;
                    if ( a[i].equals("prefix") ) 
                        outputPrefix = true ;
                    if ( a[i].equals("algebra") ) 
                        outputAlgebra = true ;
                    if ( a[i].equals("quads") ) 
                        outputQuads = true ;
                }
            }
            
//            if ( ! outputSPARQL && ! outputPrefix )
//                outputSPARQL = true ;
            
            boolean lineNumbers = true ;
            
            if ( lineNumbersArg != null )
                lineNumbers = lineNumbersArg.equalsIgnoreCase("true") || lineNumbersArg.equalsIgnoreCase("yes") ;
            
            // Headers
            httpResponse.setCharacterEncoding("UTF-8") ;
            httpResponse.setContentType("text/html") ;
            httpResponse.setHeader(respService, "Joseki/ARQ SPARQL Update Validator: http://openjena.org/ARQ") ;
            
            ServletOutputStream outStream = httpResponse.getOutputStream() ;

            outStream.println("<html>") ;
            
            printHead(outStream) ;
            
            outStream.println("<body>") ;
            outStream.println("<h1>SPARQL Update Validator</h1>") ;
            
            // Print query as received
            {
                outStream.println("<p>Input:</p>") ;
                // Not Java's finest hour.
                Content c = new Content(){
                    public void print(IndentedWriter out)
                    { out.print(updateString) ; }
                } ;
                output(outStream, c, lineNumbers) ;
            }
            
            // Attempt to parse it.
            UpdateRequest request= null ;
            try {
                request = UpdateFactory.create(updateString, "http://example/base/", language) ;
            } catch (ARQException ex)
            {
                // Over generous exception (should be QueryException)
                // but this makes the code robust.
                outStream.println("<p>Syntax error:</p>") ;
                startFixed(outStream) ;
                outStream.println(ex.getMessage()) ;
                finishFixed(outStream) ;
            }
            catch (RuntimeException ex)
            { 
                outStream.println("<p>Internal error:</p>") ;
                startFixed(outStream) ;
                outStream.println(ex.getMessage()) ;
                finishFixed(outStream) ;
            }
            
            // Because we pass into anon inner classes
            final UpdateRequest updateRequest = request ;
            
            // OK?  Pretty print
            if ( updateRequest != null && outputSPARQL )
            {
                outStream.println("<p>Formatted, parsed update request:</p>") ;
                Content c = new Content(){
                    public void print(IndentedWriter out)
                    {
                        updateRequest.output(out) ;
                    }
                        
                } ;
                output(outStream, c, lineNumbers) ;
            }
            
            outStream.println("</html>") ;
            
        } catch (Exception ex)
        {
            log.warn("Exception in doGet",ex) ;
        }
    }
    
    private String htmlQuote(String str)
    {
        StringBuffer sBuff = new StringBuffer() ;
        for ( int i = 0 ; i < str.length() ; i++ )
        {
            char ch = str.charAt(i) ;
            switch (ch)
            {
                case '<': sBuff.append("&lt;") ; break ;
                case '>': sBuff.append("&gt;") ; break ;
                case '&': sBuff.append("&amp;") ; break ;
                default: 
                    // Work around Eclipe bug with StringBuffer.append(char)
                    //try { sBuff.append(ch) ; } catch (Exception ex) {}
                    sBuff.append(ch) ;
                    break ;  
            }
        }
        return sBuff.toString() ; 
    }

    private void printHead(ServletOutputStream outStream) throws IOException
    {
        outStream.println("<head>") ;
        outStream.println(" <title>SPARQL Validator Report</title>") ;
        outStream.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"StyleSheets/joseki.css\" />") ;
        //outStream.println() ;
        outStream.println("</head>") ;
    }

    interface Content { void print(IndentedWriter out) ; }
    
    private void output(ServletOutputStream outStream, Content content, boolean lineNumbers) throws IOException
    {
        startFixed(outStream) ;
        IndentedLineBuffer out = new IndentedLineBuffer(lineNumbers) ; 
        content.print(out) ;
        out.flush() ;  
        String x = htmlQuote(out.asString()) ;
        byte b[] = x.getBytes("UTF-8") ;
        outStream.write(b) ;
        finishFixed(outStream) ;
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
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * (c) Copyright 2010 Epimorphics Ltd. 
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