/*
 * (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.http;

import java.util.*;
import org.apache.commons.logging.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.hp.hpl.jena.util.FileManager;

import org.joseki.*;

/** The servlet class.
 * @author  Andy Seaborne
 * @version $Id: Servlet.java,v 1.25 2008-01-07 16:27:25 andy_seaborne Exp $
 */

public class Servlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;  // Serializable.
    
    // Use one logger.
    protected static Log log = null ;
    
    // This happens very early - check it.
    static {
        try { log = LogFactory.getLog(Servlet.class) ; }
        catch (Exception ex)
        {
            System.err.println("Exception creating the logger") ;
            System.err.println("Commons logging jar files in WEB-INF/lib/?") ;
            System.err.println(ex.getMessage());
            //ex.printStackTrace(System.err) ;
        }
    }
    
    static boolean initAttempted = false ;
    
    int urlLimit = 8*1024 ;
    
    protected HttpResultSerializer httpSerializer = new HttpResultSerializer() ;
    
    String printName = "HTTP SPARQL";
    // Servlet info
    ServletConfig servletConfig = null;
    // Web app info
    ServletContext servletContext = null;
    
    // The servlet is either running as a webapp (web.xml config file and all)
    // or running as a plain servlet embedded in something else.
    // The former means the configuration is in the webapp/servlet environment
    // The latter means the main application will programmatically do it.
    
    /** Creates new JosekiWebAPI */
    public Servlet()
    {
        log.info("-------- Joseki") ;
    }

    public void init() throws ServletException
    {
        super.init() ;
        return ;
    }

    /** Initializes the servlet.
    */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        // It seems that if the servlet fails to initialize the first time,
        // init can be called again (it has been observed in Tomcat log files
        // but not explained).
        
        if ( initAttempted )
        {
            log.info("Re-initialization of servlet attempted") ;
            return ;
        }

        initAttempted = true ; 
        
        servletConfig = config;
        
        // Modify the (Jena) global filemanager to include loading by servlet context  
        FileManager fileManager = FileManager.get() ;

        if (config != null)
        {
            servletContext = config.getServletContext();
            fileManager.addLocator(new LocatorServletContext(servletContext)) ;
        }
        
        printName = config.getServletName();
        String configURI = config.getInitParameter(Joseki.configurationFileProperty) ;
        servletEnv() ;
        try {
            Dispatcher.initServiceRegistry(fileManager, configURI) ;
        } catch (ConfigurationErrorException confEx)
        {
            throw new ServletException("Joseki configuration error", confEx) ;
        }
    }
        
    /** Destroys the servlet.
    */
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
    {
        try {
            if ( log.isDebugEnabled() )
                log.debug(HttpUtils.fmtRequest(httpRequest)) ;
            
            // getRequestURL is the exact string used by the caller in the request.
            // Internally, it's the "request URI" that names the service
            
            //String requestURL = httpRequest.getRequestURL().toString() ;
            String uri = httpRequest.getRequestURI() ;

            if ( uri.length() > urlLimit )
            {
                httpResponse.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG) ;
                return ;
            }

            String serviceURI = chooseServiceURI(uri, httpRequest) ;
            serviceURI = Service.canonical(serviceURI) ;
            
            String sender = httpRequest.getRemoteAddr() ; 
            log.info("["+sender+"] Service URI = <"+serviceURI+">") ;
            
            // Assemble parameters
            Request request = new Request(serviceURI) ;
            // params => request items
            for ( Enumeration en = httpRequest.getParameterNames() ; en.hasMoreElements() ; )
            {
                String k = (String)en.nextElement() ;
                String[] x = httpRequest.getParameterValues(k) ;
                for(int i = 0 ; i < x.length ; i++ )
                {
                    String s = x[i] ;
                    request.setParam(k, s) ;
                }
            }
            
            Response response = new ResponseHttp(request, httpRequest, httpResponse) ;

            Dispatcher.dispatch(serviceURI, request, response) ;
        }
        catch (Exception ex)
        {
            try {
                log.warn("Internal server error", ex) ;
//                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR) ;
//                httpResponse.flushBuffer() ;
//                httpResponse.getWriter().close() ;
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR) ;
            } catch (Exception e) {}
        }        
    }


    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        String s = httpRequest.getContentType() ;
        if ( s != null )
        {
            // A charset isn't necessary but if it is present it is ignored.

            // An accept item is actually a general header parser.
            AcceptItem aItem = new AcceptItem(s) ;
            String t1 = aItem.getType() ;
            String t2 = aItem.getSubType() ;

            if ( ! t1.equalsIgnoreCase("application") || ! t2.equalsIgnoreCase("x-www-form-urlencoded") )
            {
                try {
                    httpResponse.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Must be application/x-www-form-urlencoded") ;
                } catch (Exception ex) {}
                return ;
            }
        }
        doGet(httpRequest, httpResponse) ;
    }

    // ------------------------------------------
    // This should return a list of possibilities to allow for
    // future changes.

    public static String chooseServiceURI(String uri, HttpServletRequest httpRequest)
    {
        String serviceURI = uri ;
        String contextPath = httpRequest.getContextPath() ;
        
        if ( contextPath != null && contextPath.length() > 0 )
            serviceURI = serviceURI.substring(contextPath.length()) ;
        
        String servletPath = httpRequest.getServletPath() ;

        // Suggested by Frank Hartman: helps make conf files more portable
        // between /joseki/myModel and /myModel but if the servlet is 
        // explicitly named in web.xml, it strips that off
//        if ( servletPath != null && servletPath.length() > 0 )
//            dispatchURI = dispatchURI.substring(servletPath.length()) ;
        
        // Suggested by damien_coraboeuf
        // TODO Test and verify
//        if ( servletPath != null && servletPath.length() > 0 )
//            serviceURI = serviceURI.substring(servletPath.length()) ;
  
        // Example:
//    <servlet-mapping>
//        <servlet-name>JosekiServlet</servlet-name>
//        <url-pattern>/ws/joseki/*</url-pattern>
//    </servlet-mapping>
        

        if ( log.isDebugEnabled() )
        {
            if ( servletPath == null )
                servletPath = "" ;
            if ( contextPath == null )
                contextPath = "" ;
            log.debug("DispatchURI: "+uri+" => "+serviceURI+" (ContextPath = "+contextPath+", ServletPath = "+servletPath+")") ;
        }
        return serviceURI ;
    }
    
    
    // Reply when an exception was generated

//    private void doExeception(ExecutionException execEx, String uri, HttpServletResponse response)
//    {
//        String httpMsg = ExecutionError.errorString(execEx.returnCode);
//        //msg("Error in operation: URI = " + uri + " : " + httpMsg);
//        log.info("Error: URI = " + uri + " : " + httpMsg);
//        httpSerializer.sendError(execEx, response) ;
//        return;
//    }

    // Desparate way to reply
    
//    private void doPanic(HttpServletResponse response, int reason)
//    {
//        // Panic.
//        try {                
//            response.setHeader(Joseki.httpHeaderField, Joseki.httpHeaderValue) ;
//            response.setStatus(reason) ;
//            response.flushBuffer() ;
//            response.getWriter().close() ;
//        } catch (Exception e) {}
//    }


    /** Returns a short description of the servlet.
    */
    public String getServletInfo()
    {
        //return this.getClass().getName() ;
        return printName;
    }

    // Need a way to pair requests and responses.
    // Via a Request object? 
    
    //private void logResponse(HttpServletRequest request, HttpServletResponse response)
    //{
    //}

    private void servletEnv()
    {
        if ( ! log.isDebugEnabled() )
            return ;
        
        try {
            java.net.URL url = servletContext.getResource("/") ;
            log.trace("Joseki base directory: "+url) ;
        } catch (Exception ex) {}
        
        if (servletConfig != null)
        {
            String tmp = servletConfig.getServletName() ;
            log.trace("Servlet = " + (tmp != null ? tmp : "<null>"));
            Enumeration en = servletConfig.getInitParameterNames();
            
            for (; en.hasMoreElements();)
            {
                String s = (String) en.nextElement();
                log.trace("Servlet parameter: " + s + " = " + servletConfig.getInitParameter(s));
            }
        }
        if (servletContext != null)
        {
            // Name of webapp
            String tmp = servletContext.getServletContextName();
            //msg(Level.FINE, "Webapp = " + (tmp != null ? tmp : "<null>"));
            log.debug("Webapp = " + (tmp != null ? tmp : "<null>"));

            // NB This servlet may not have been loaded as part of a web app

            Enumeration en = servletContext.getInitParameterNames();
            for (;en.hasMoreElements();)
            {
                String s = (String) en.nextElement();
                log.debug("Webapp parameter: " + s + " = " + servletContext.getInitParameter(s));
            }
        }
        /*
        for ( Enumeration enum = servletContext.getAttributeNames() ;  enum.hasMoreElements() ; )
        {
            String s = (String)enum.nextElement() ;
            logger.log(LEVEL, "Webapp attribute: "+s+" = "+context.getAttribute(s)) ;
        }
         */
    }
}

/*
 *  (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
 