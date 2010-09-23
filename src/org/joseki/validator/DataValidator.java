/*
 * (c) Copyright 2010 Epimorphics Ltd.
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.validator;

import static org.openjena.riot.SysRIOT.fmtMessage ;

import java.io.IOException ;
import java.io.PrintStream ;
import java.io.Reader ;
import java.io.StringReader ;

import javax.servlet.ServletConfig ;
import javax.servlet.ServletException ;
import javax.servlet.ServletOutputStream ;
import javax.servlet.http.HttpServlet ;
import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpServletResponse ;

import org.openjena.atlas.io.IO ;
import org.openjena.atlas.lib.Sink ;
import org.openjena.atlas.lib.SinkWrapper ;
import org.openjena.riot.ErrorHandler ;
import org.openjena.riot.Lang ;
import org.openjena.riot.RiotException ;
import org.openjena.riot.RiotReader ;
import org.openjena.riot.lang.LangRIOT ;
import org.openjena.riot.tokens.Tokenizer ;
import org.openjena.riot.tokens.TokenizerFactory ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.sparql.core.Quad ;
import com.hp.hpl.jena.sparql.util.FmtUtils ;

public class DataValidator extends HttpServlet 
{
    protected static Logger log = LoggerFactory.getLogger("DataValidator") ;
    
    public DataValidator() 
    {
        log.info("-------- DataValidator") ;
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
    
    @Override
    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    { validationRequest(httpRequest, httpResponse) ; }

    @Override
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    { validationRequest(httpRequest, httpResponse) ; }
    
    static final String paramLineNumbers      = "linenumbers" ;
    static final String paramFormat           = "outputFormat" ;
    static final String paramIndirection      = "url" ;
    static final String paramData             = "data" ;
    static final String paramSyntax           = "languageSyntax" ;
    //static final String paramSyntaxExtended   = "extendedSyntax" ;
    static final String respService           = "X-Service" ;
    
    private void validationRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        try {
            if ( log.isDebugEnabled() )
                log.debug("data validation request") ;
            
            Tokenizer tokenizer = createTokenizer(httpRequest, httpResponse) ;
            if ( tokenizer == null )
                return ;

            ServletOutputStream outStream = httpResponse.getOutputStream() ;
            ErrorHandlerMsg errorHandler = new ErrorHandlerMsg(outStream) ;
            
            PrintStream stdout = System.out ;
            PrintStream stderr = System.err ;
            System.setOut(new PrintStream(outStream)) ;
            System.setErr(new PrintStream(outStream)) ;
            
            LangRIOT parser = setupParser(tokenizer, errorHandler, outStream) ;
            
            outStream.println("<html>") ;
            printHead(outStream) ;
            outStream.println("</html>") ;
            outStream.println("<body>") ;
            
            outStream.println("<h1>RIOT Parser Report</h1>") ;
            outStream.println("<p>Line and column numbers refer to original input</p>") ;
            outStream.println("<p>&nbsp;</p>") ;
            try {
                startFixed(outStream) ;
                RiotException exception = null ;
                try {
                    parser.parse() ;
                    System.out.flush() ;
                    System.err.flush() ;
                } catch (RiotException ex) { exception = ex ; } 
                
                // Exception shoudl have caused an error/fatal message.  
//                if ( exception != null )
//                {
//                    finishFixed(outStream) ;
//                    startFixed(outStream) ;
//                    outStream.println(exception.getMessage()) ;
//                    finishFixed(outStream) ;
//                }
            } finally 
            {
                finishFixed(outStream) ;
                System.out.flush() ;
                System.err.flush() ;
                System.setOut(stdout) ;
                System.setErr(stdout) ;
            }
            
            outStream.println("</body>") ;
        } catch (Exception ex)
        {
            log.warn("Exception in validationRequest",ex) ;
        }
    }
    
    static final long LIMIT = 50000 ;
    
    private LangRIOT setupParser(Tokenizer tokenizer, ErrorHandler errorHandler, final ServletOutputStream outStream)
    {
        Sink<Quad> sink = new Sink<Quad>()
        {
            public void send(Quad quad)
            {
                String $ = FmtUtils.stringForQuad(quad) ;
                $ = htmlQuote($) ;
                try { 
                    outStream.print($) ;
                    outStream.println(" .") ;
                } catch (IOException ex) { IO.exception(ex) ; }
            }
            public void close() {}
            public void flush() {}
        } ;
        
        Sink<Quad> sink2 = new SinkWrapper<Quad>(sink){
            long count = 0 ;
            public void close() {}
            public void flush() {}
            public void send(Quad quad)
            { 
                super.send(quad) ;
                count++ ;
                if ( count > LIMIT )
                    throw new RiotException("Limit exceeded") ;
            }
        } ;
        // Language?
        LangRIOT parser = RiotReader.createParserQuads(tokenizer, Lang.TURTLE, null, sink) ;
        parser.getProfile().setHandler(errorHandler) ;
        parser.getProfile().setHandler(errorHandler) ;
        return parser ;
    }

    // Error handler that records messages
    private static class ErrorHandlerMsg implements ErrorHandler
    {
        private ServletOutputStream out ;

        ErrorHandlerMsg(ServletOutputStream out) { this.out = out ; }
        
        public void warning(String message, long line, long col)
        { output(message, line, col, "warning") ; }
    
        // Attempt to continue.
        public void error(String message, long line, long col)
        { output(message, line, col, "error") ; }
    
        public void fatal(String message, long line, long col)
        { output(message, line, col, "error") ; throw new RiotException(fmtMessage(message, line, col)) ; }
        
        private void output(String message, long line, long col, String className)
        {
            try {
                String str = fmtMessage(message, line, col) ;
                str = htmlQuote(str) ;
                out.print("<div class=\""+className+"\">") ;
                out.print(str) ;
                out.print("</div>") ;
            } catch (IOException ex) { IO.exception(ex) ; }
        }
       
        
    }
    
    private Tokenizer createTokenizer(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception
    {
        Reader reader = null ;  
        String[] args = httpRequest.getParameterValues(paramIndirection) ;
        
        if ( args == null || args.length == 0 )
        {
            reader = httpRequest.getReader() ;
        } 
        else if ( args.length > 1 )
        {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Too many parameters for '"+paramIndirection+"='") ;
            return null ;
        }
//        else
//        {
//            reader = // get from afar.
//        }
        
        args = httpRequest.getParameterValues(paramData) ;
        if ( args == null || args.length == 0 )
        {}
        else if ( args.length > 1 )
        {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Too many parameters for '"+paramData+"='") ;
            return null ;
        }
        else
        {
            reader = new StringReader(args[0]) ;
        }
        
        if ( reader == null )
        {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Can't find data to validate") ;
            return null ;
        }
        
        return TokenizerFactory.makeTokenizer(reader) ;
    }

    private static String htmlQuote(String str)
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

    private static void startFixed(ServletOutputStream outStream) throws IOException
    {
        outStream.println("<pre class=\"box\">") ;
    }

    private static void columns(String prefix, ServletOutputStream outStream) throws IOException
    {
        outStream.print(prefix) ;
        outStream.println("         1         2         3         4         5         6         7") ;
        outStream.print(prefix) ;
        outStream.println("12345678901234567890123456789012345678901234567890123456789012345678901234567890") ;
    }
    
    private static void finishFixed(ServletOutputStream outStream) throws IOException
    {
        outStream.println("</pre>") ;
    }
    
    private static void printHead(ServletOutputStream outStream) throws IOException
    {
        outStream.println("<head>") ;
        outStream.println(" <title>Jena Data Validator Report</title>") ;
        outStream.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"StyleSheets/joseki.css\" />") ;
        //outStream.println() ;
        outStream.println("</head>") ;
    }

}

/*
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