/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package joseki3.server.http;

import java.util.*;
import org.apache.commons.logging.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.FileManager;

import joseki3.server.*;
import org.joseki.HttpParams;


/** The servlet class.
 * @author  Andy Seaborne
 * @version $Id$
 */

public class Servlet extends HttpServlet implements Connector
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
    
    ServiceRegistry serviceRegistry = new ServiceRegistry() ;
    
    protected HttpRequestParser    httpRequestParser = new HttpRequestParser() ;
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
        log.debug("Created") ;
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
            if ( serviceRegistry == null )
                serviceRegistry = new ServiceRegistry() ;
            return ;
        }
        initAttempted = true ; 
        
        servletConfig = config;
        
        if (config != null)
        {
            servletContext = config.getServletContext();
            // Modifiy the (Jena) global filemanager to include loading by servlet context  
            FileManager.get().addLocator(new LocatorServletContext(servletContext)) ;
        }

        printName = config.getServletName();
        
        servletEnv() ;
        
        // Get the service registry is not already supplied.
        if ( serviceRegistry == null )
            serviceRegistry = (ServiceRegistry)Registry.find(RDFServer.ServiceRegistryName) ;
        
        // No global one.
        if ( serviceRegistry == null )
        {
            log.info("Creating service registry") ;
            ServiceRegistry tmp = new ServiceRegistry() ;
            // Initialize it.
            try {
                initServiceRegistry(tmp);
            } catch (ConfigurationErrorException confEx)
            {
                throw new ServletException("Joseki configuration error", confEx) ;
            }
            Registry.add(RDFServer.ServiceRegistryName, tmp) ;
            serviceRegistry = (ServiceRegistry)Registry.find(RDFServer.ServiceRegistryName) ;
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
            // Internally, its the "request URI" that names the model
            
            String requestURL = httpRequest.getRequestURL().toString() ;
            String uri = httpRequest.getRequestURI() ;

            if ( uri.length() > urlLimit )
            {
                httpResponse.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG) ;
                return ;
            }

            uri = chooseDispatchURI(uri, httpRequest) ;
            if ( uri.startsWith("/") )
                uri = uri.substring(1) ;
            
            
            
            // lookup uri (not a 
            
            //msg(Level.FINE, "Get: URL= "+uri) ;
            //msg(Level.FINE, "  QueryString = "+request.getQueryString()) ;
            
            String queryLang = httpRequest.getParameter(HttpParams.pQueryLang) ;

            if ( httpRequest.getParameterMap().size() == 0 )
                // It's a plain GET
                queryLang = "GET" ;
        
            // Do request
            //try {}
        } catch (Exception ex)
        {
            try {
                log.warn("Internal server error", ex) ;
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR) ;
                httpResponse.flushBuffer() ;
                httpResponse.getWriter().close() ;
            } catch (Exception e) {}
        }        
    }


    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        try {
            if ( log.isDebugEnabled() )
                log.debug(HttpUtils.fmtRequest(httpRequest)) ;
            
            String requestURL = httpRequest.getRequestURL().toString() ;
            String uri = httpRequest.getRequestURI() ;
            String httpQueryString = httpRequest.getQueryString() ;
            
            uri = chooseDispatchURI(uri, httpRequest) ;
            
            if ( httpQueryString == null )
            {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The HTTP query string specified in POST is empty: it must be ?op=<opName>") ;
                return ;
            }

            //try {} catch () {}{
        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err) ;
            // Problems.
            doPanic(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR) ;
            return ;
        }        
    }

    // ------------------------------------------
    // This should return a list of possibilities to allow for
    // future changes.

    private String chooseDispatchURI(String uri, HttpServletRequest httpRequest)
    {
        String dispatchURI = uri ;
        String contextPath = httpRequest.getContextPath() ;
        
        if ( contextPath != null && contextPath.length() > 0 )
            dispatchURI = dispatchURI.substring(contextPath.length()) ;
        
        String servletPath = httpRequest.getServletPath() ;

        // Suggested by Frank Hartman: helps make conf files more portable
        // between /joseki/myModel and /myModel but if the servlet is 
        // explicitly named in web.xml, it strips that off
//        if ( servletPath != null && servletPath.length() > 0 )
//            dispatchURI = dispatchURI.substring(servletPath.length()) ;

        if ( log.isDebugEnabled() )
        {
            if ( servletPath == null )
                servletPath = "" ;
            if ( contextPath == null )
                contextPath = "" ;
            log.debug("DispatchURI: "+uri+" => "+dispatchURI+" (ContextPath = "+contextPath+", ServletPath = "+servletPath+")") ;
        }
        return dispatchURI ;
    }
    
    
//    protected void execute(Request request, Response response)
//    {
//       try {
//            String uri = request.getModelURI() ;
//            String s = null ; 
//            if ( request.getOpName().equals(HttpParams.pQuery) )
//                s = "URI="+ uri + "  Query="+request.getParam(HttpParams.pQueryLang) ;
//            else
//                s = "URI="+ uri + "  Request="+request.getOpName() ;
//
//            log.debug(s) ;
//            
//            dispatcher.exec(request, response) ;
//            log.info("OK - "+s) ;
//        } catch (ExecutionException execEx)
//        {
//            //String s = httpResponseCode(execEx.returnCode) ;
//            //log.info(s+" ("+execEx.returnCode+")") ;
//            response.doException(execEx) ;
//            return ;
//        }
//
//    }


    // Safe version (does not read body)
    // Don't use ServletRequest.getParameter as that reads form data.
    
    private Map getParameters(String queryString)
    {
        Map map = new HashMap() ;
        
        String[] params = queryString.split("&") ;
        for ( int i = 0 ; i < params.length ; i++ )
        {
            String p = params[i] ;
            String[] x = p.split("=",2) ;
            
            if ( x.length == 0 )
                continue ;

            String pName = x[0] ;
            String pValue = "" ;
            if ( x.length == 2 )
                pValue = x[1] ;
            map.put(pName, "") ;
        }
        // Not found
        return null ;
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
    
    private void doPanic(HttpServletResponse response, int reason)
    {
//        // Panic.
//        try {                
//            response.setHeader(Joseki.httpHeaderField, Joseki.httpHeaderValue) ;
//            response.setStatus(reason) ;
//            response.flushBuffer() ;
//            response.getWriter().close() ;
//        } catch (Exception e) {}
    }


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


    

    // This method contains the pragmatic algorithm to determine the server model map.
    //
    // In this order (i.e. specific to general) to find the filename:
    //    System property:         jena.rdfserver.modelmap
    //    Webapp init parameter:   jena.rdfserver.modelmap
    //    Servlet init parameter:  jena.rdfserver.modelmap
    // and then the file is loaded.
    // 
    // If the system property is explicit set a well known value, this is skipped. 

    private boolean initServiceRegistry(ServiceRegistry registry)
    {
      String tmp = System.getProperty(RDFServer.propertyModelSet) ;
      if ( tmp != null && tmp.equals(RDFServer.noMapValue))
          return false ;
      
      if ( tmp != null )
          if (attemptLoad(tmp, "System property: " + RDFServer.propertyModelSet, registry))
              return true;

      if (servletConfig != null
          && attemptLoad(
              servletConfig.getInitParameter(RDFServer.propertyModelSet),
              "Servlet configuration parameter: " + RDFServer.propertyModelSet,
              registry) )
          return true;

      // Try default name and location
      if (attemptLoad(RDFServer.defaultConfigFile, "Default filename", registry) )
          return true;

      return false;
    }

    

//    private boolean initDispatcher(Dispatcher disp) throws ConfigurationErrorException
//    {
//        String tmp = System.getProperty(RDFServer.propertyModelSet) ;
//        if ( tmp != null && tmp.equals(RDFServer.noMapValue))
//            return false ;
//        
//        if ( tmp != null )
//            if (attemptLoad(tmp, "System property: " + RDFServer.propertyModelSet, disp))
//                return true;
//
//        if (servletConfig != null
//            && attemptLoad(
//                servletConfig.getInitParameter(RDFServer.propertyModelSet),
//                "Servlet configuration parameter: " + RDFServer.propertyModelSet,
//                disp) )
//            return true;
//
//        // Try default name and location
//        if (attemptLoad(RDFServer.defaultConfigFile, "Default filename",disp) )
//            return true;
//
//        return false;
//    }
//
    private boolean attemptLoad(String filename, String reason, ServiceRegistry registry)
        throws ConfigurationErrorException
    {
        if (filename == null)
            return false;

        try
        {
            Configuration c = new Configuration(filename, registry) ;
            log.info("Loaded data source configuration: " + filename);
            return true;
        } catch (RDFException rdfEx)
        {
            // Trouble processing a configuration 
            throw new ConfigurationErrorException("RDF Exception: "+rdfEx.getMessage(), rdfEx) ;
            //return false ;
        } catch (NotFoundException ex)
        {
            throw new ConfigurationErrorException("Not found: "+ex.getMessage(), ex) ;
            //return false;
        }
    }

    public void setServiceRegistry(ServiceRegistry r)
    {
        serviceRegistry = r ;
    }

    public ServiceRegistry getServiceRegistry()
    {
        return serviceRegistry ;
    }


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
 *  (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
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
 