/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.http;

import java.io.*;
import java.util.*;
import org.apache.commons.logging.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;

import org.joseki.util.Convert;
import org.joseki.server.*;
import org.joseki.Joseki ;


/** The servlet class.
 * @author  Andy Seaborne
 * @version $Id: JosekiWebAPI.java,v 1.1 2004-11-03 10:15:02 andy_seaborne Exp $
 */

public class JosekiWebAPI extends HttpServlet implements Connector
{
    // Use one logger.
    protected static Log log = null ; //LogFactory.getLog(HTTP_Execution.class.getName()) ;
    
    // This happens very early - check it.
    static {
        try { log = LogFactory.getLog(JosekiWebAPI.class) ; }
        catch (Exception ex)
        {
            System.err.println("Exception creating the logger") ;
            System.err.println("Commons logging jar files in WEB-INF/lib/?") ;
            System.err.println(ex.getMessage());
            //ex.printStackTrace(System.err) ;
        }
    }
    
    // The long URL limit
    static int urlLimit = 4*1024 ;
    static boolean initAttempted = false ;
    
    protected Dispatcher dispatcher = null ;
    protected HttpRequestParser    httpRequestParser = new HttpRequestParser() ;
    protected HttpResultSerializer httpSerializer = new HttpResultSerializer() ;
    
    String printName = "HTTP RDF WebAPI";
    // Servlet info
    ServletConfig servletConfig = null;
    // Web app info
    ServletContext servletContext = null;
    
    // The servlet is either running as a webapp (web.xml coinfig file and all)
    // or running as a plain servlet embedded in something else.
    // The former means the configuration is in the webapp/servlet environment
    // The latter means the main application will programmatically do it.
    
    /** Creates new JosekiWebAPI */
    public JosekiWebAPI()
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
            if ( dispatcher == null )
                dispatcher = new Dispatcher() ;
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
        DispatcherRegistry dispatcherRegistry = DispatcherRegistry.getInstance() ;
        
        if ( ! dispatcherRegistry.contains(RDFServer.DispatcherName) )
        {
            log.info("Creating dispatcher") ;
            Dispatcher tmp = new Dispatcher() ;
            // Initialize it.
            try {
                initDispatcher(tmp);
            } catch (ConfigurationErrorException confEx)
            {
                throw new ServletException("Joseki configuration error", confEx) ;
            }
            dispatcherRegistry.add(RDFServer.DispatcherName, tmp) ;
            dispatcher = dispatcherRegistry.find(RDFServer.DispatcherName) ;
        }
        else
        {
            log.info("Using existing dispatcher") ;
            dispatcher = dispatcherRegistry.find(RDFServer.DispatcherName) ;
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
                log.debug(fmtRequest(httpRequest)) ;
            
            // getRequestURL is the exact string used by the caller in the request.
            // Internally, its the "request URI" that names the model
            
            String requestURL = httpRequest.getRequestURL().toString() ;
            String uri = httpRequest.getRequestURI() ;

            if ( uri.length() > urlLimit )
            {
                httpResponse.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG) ;
                return ;
            }

            uri = formDispatchURI(uri, httpRequest) ;
            
            //msg(Level.FINE, "Get: URL= "+uri) ;
            //msg(Level.FINE, "  QueryString = "+request.getQueryString()) ;
            
            String queryLang = httpRequest.getParameter("lang");

            if ( httpRequest.getParameterMap().size() == 0 )
                // It's a plain GET
                queryLang = "GET" ;
            
            try {
                Request opRequest = dispatcher.createQueryRequest(uri, requestURL, queryLang) ;

                Map m = httpRequest.getParameterMap() ;
                for ( Iterator iter = m.keySet().iterator() ; iter.hasNext() ; )
                {
                    String k = (String)iter.next() ;
                    String[] v = (String[])m.get(k) ;
                    if ( k.equals("q") )
                        k = "query" ;
                    for ( int vi = 0 ; vi < v.length ; vi++ )
                        opRequest.setParam(k,v[vi]) ;
                }
                
                execute(opRequest, httpRequest, httpResponse) ;
            } catch (ExecutionException execEx)
            {
                doResponse(execEx, uri, httpResponse) ;
                return ;
            }
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
                log.debug(fmtRequest(httpRequest)) ;
            
            String requestURL = httpRequest.getRequestURL().toString() ;
            String uri = httpRequest.getRequestURI() ;
            String httpQueryString = httpRequest.getQueryString() ;
            
            uri = formDispatchURI(uri, httpRequest) ;
            
            if ( httpQueryString == null )
            {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The HTTP query string specified in POST is empty: it must be ?op=<opName>") ;
                return ;
            }

            // TODO Make this position insensitive
            // Parse the whole line using Joseki argument parsing code here.

            // ---- Compatibility : old systems had ...&opName&....
            String reqName = httpQueryString ;
            // Split off the first word - up to the first '&'
            int i = reqName.indexOf('&') ;
            if ( i != -1 )
            {
                reqName = reqName.substring(0,i) ;
                //httpQueryString = httpQueryString.substring(i) ;
            }

            // Old style: has just ?opName[&k=v&k=v]
            // Documentation has ?op=opName
            // Handle both
            
            if ( reqName.startsWith("op=") )
                reqName = reqName.substring("op=".length()) ;
            
            try {
                Request opRequest =
                    dispatcher.createOperation(uri, requestURL, reqName) ;
                httpRequestParser.setParameters(opRequest, httpRequest) ;
                httpRequestParser.setArgs(opRequest, httpRequest) ;
                execute(opRequest, httpRequest, httpResponse) ;  
            } catch (ExecutionException execEx)
            {
                doResponse(execEx, uri, httpResponse) ;
                return ;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err) ;
            // Problems.
            doResponse(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR) ;
            return ;
        }        
    }


    public void doOptions(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    throws ServletException, java.io.IOException
    {
        try {
            log.debug(fmtRequest(httpRequest)) ;

            String requestURL = httpRequest.getRequestURL().toString() ;
            String uri = httpRequest.getRequestURI() ;
            
            uri = formDispatchURI(uri, httpRequest) ;

            
            //msg(Level.FINE, "Context path="+request.getContextPath()+" :: PathInfo="+request.getPathInfo()+" :: PathTranslated="+request.getPathTranslated()) ;
            log.debug("Context path="+httpRequest.getContextPath()+" :: PathInfo="+httpRequest.getPathInfo()+" :: PathTranslated="+httpRequest.getPathTranslated()) ;
            
            if ( uri.length() > urlLimit )
            {
                httpResponse.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG) ;
                return ;
            }
            
            // Base for this request - this servelet may be on many access points.
            // Note - code else where assumes this does NOT end in a slash
            String baseURL = "http://"+httpRequest.getServerName() ;
            int port = httpRequest.getServerPort() ;
            if ( port != 80 )
                baseURL= baseURL+":"+port ;

            try {
                //The URLConnection class seems to have a problem with a uri of * (server)
                if (uri.equals("*") || uri.equals("/") || uri.equals("/*"))
                {
                    // There isn't an SourceModel for the server itself - do the work here.
                    try {
                        Request opHostRequest = dispatcher.createRequest(uri, requestURL, "options", false) ;
                        opHostRequest.setParam("baseURL", baseURL) ;
                        //msg("Options: URI="+ req.getModelURI() + "  Request="+req.getName()) ;
                        log.info("Options: URI="+ opHostRequest.getModelURI() + "  Request="+opHostRequest.getName()) ;
                        Model resultModel = dispatcher.getOptionsModel(baseURL);
                        doResponse(resultModel, opHostRequest, httpRequest, httpResponse) ;
                    }
                    catch (ExecutionException execEx) { doResponse(execEx, uri, httpResponse); }
                    return ;
                }
                
                Request opRequest = dispatcher.createOperation(uri, requestURL, "options") ;
                opRequest.setParam("baseURL", baseURL) ;
                execute(opRequest, httpRequest, httpResponse);
            } catch (ExecutionException execEx)
            {
                doResponse(execEx, uri, httpResponse) ;
                return ;
            }
        }
        catch (Exception ex)
        {
            try
            {
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                httpResponse.flushBuffer();
                httpResponse.getWriter().close();
            } catch (Exception e) { }
        }  
    }

    // ------------------------------------------
    // This should return a list of possibilities to allow for
    // future changes.
    // e.g. Under Tomcat: http://server/joseki/books
    // where as http://server/books us probably better.
    // Don't want to break current deployed configurations. 
 

    private String formDispatchURI(String uri, HttpServletRequest httpRequest)
    {
        String dispatchURI = uri ;
        String contextPath = httpRequest.getContextPath() ;
        
        if ( contextPath != null && contextPath.length() > 0 )
            dispatchURI = dispatchURI.substring(contextPath.length()) ;
        
        // Suggested by Frank Hartman:
        String servletPath = httpRequest.getServletPath() ;
        if ( servletPath != null && servletPath.length() > 0 )
            dispatchURI = dispatchURI.substring(servletPath.length()) ;

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
    
    
    protected void execute(Request req, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ExecutionException
    {
        Model resultModel = null ;
        
        if ( req.getName().equals("query") )
            log.debug("URI="+ req.getModelURI() + "  Query="+req.getParam("lang")) ;
        else
            log.debug("URI="+ req.getModelURI() + "  Request="+req.getName()) ;

        resultModel = dispatcher.exec(req) ;
        doResponse(resultModel, req, httpRequest, httpResponse);
        
        //TODO Replace with a real result object
        // At the moment, relies on the fact that its always a memory model and 
        // nothing much happens on a close(). 
        //resultModel.close() ;
    }


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

    // Normal reply result is a model

    protected void doResponse(Model resultModel, Request opRequest,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse)
    {
        try
        {
            log.debug("Successful operation: URI = " + opRequest.getModelURI()+" : Request = "+opRequest.getName() ) ;
            try
            {
                if ( httpSerializer.sendResponse(resultModel, opRequest,
                        httpRequest, httpResponse) )
                    log.info("OK - "+fmtRequest(httpRequest)) ;
            }
            catch (RDFException rdfEx)
            {
                //msg(Level.WARNING, "RDFException", rdfEx);
                log.warn("RDFException", rdfEx);
                //printStackTrace(printName + "(execute)", rdfEx);
                httpResponse.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "RDFException: " + rdfEx);
                return;
            }
            catch (JenaException jEx)
            {
                //msg(Level.WARNING, "Exception", ex);
                log.warn("JenaException: "+jEx.getMessage());
                if ( jEx.getCause() != null )
                    jEx.getCause().printStackTrace(httpResponse.getWriter());
                else
                    jEx.printStackTrace(httpResponse.getWriter());
                httpResponse.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "JenaException: " + jEx);
                return;
            }
            catch (Exception ex)
            {
                //msg(Level.WARNING, "Exception", ex);
                log.warn("Exception", ex);

                httpResponse.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Exception: " + ex);
                return;
            }
        }
        catch (IOException ioEx)
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
    
    // Reply when an exception was generated

    protected void doResponse(ExecutionException execEx, String uri, HttpServletResponse response)
    {
        
        try {
            String httpMsg = ExecutionError.errorString(execEx.returnCode);
            //msg("Error in operation: URI = " + uri + " : " + httpMsg);
            log.info("Error: URI = " + uri + " : " + httpMsg);
            httpSerializer.sendError(execEx, response) ;
            return;
        } catch (IOException ioEx)
        {
            log.warn("IOException in exception response") ;
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.flushBuffer();
                response.getWriter().close();
            } catch (Exception e) { }
        }
    }


    // Desparate way to reply
    
    protected void doResponse(HttpServletResponse response, int reason)
    {
        // Panic.
        try {                
            response.setHeader(Joseki.httpHeaderField, Joseki.httpHeaderValue) ;
            response.setStatus(reason) ;
            response.flushBuffer() ;
            response.getWriter().close() ;
        } catch (Exception e) {}
    }


    /** Returns a short description of the servlet.
    */
    public String getServletInfo()
    {
        //return this.getClass().getName() ;
        return printName;
    }

    static String fmtRequest(HttpServletRequest request)
    {
        StringBuffer sbuff = new StringBuffer() ;
        sbuff.append(request.getMethod()) ;
        sbuff.append(" ") ;
        sbuff.append(Convert.decWWWForm(request.getRequestURL()));
        
        String qs = request.getQueryString();
        if (qs != null)
        {
        	String tmp = request.getQueryString() ;
            tmp = Convert.decWWWForm(tmp) ;
            tmp = tmp.replace('\n', ' ') ;
            tmp = tmp.replace('\r', ' ') ;
            sbuff.append("?").append(tmp);
        }
        return sbuff.toString() ;
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
    

    private boolean initDispatcher(Dispatcher disp) throws ConfigurationErrorException
    {
        String tmp = System.getProperty(RDFServer.propertyModelSet) ;
        if ( tmp != null && tmp.equals(RDFServer.noMapValue))
            return false ;
        
        if ( tmp != null )
            if (attemptLoad(tmp, "System property: " + RDFServer.propertyModelSet, disp))
                return true;

        if (servletConfig != null
            && attemptLoad(
                servletConfig.getInitParameter(RDFServer.propertyModelSet),
                "Servlet configuration parameter: " + RDFServer.propertyModelSet,
                disp) )
            return true;

        // Try default name and location
        if (attemptLoad(RDFServer.defaultConfigFile, "Default filename",disp) )
            return true;

        return false;
    }

    private boolean attemptLoad(String filename, String reason, Dispatcher disp)
        throws ConfigurationErrorException
    {
        if (filename == null)
            return false;

        try
        {
            disp.getModelSet().load(filename) ;
            log.info("Loaded data source configuration: " + filename);
            //msg("Loaded data source configuration: " + filename);
            return true;
        } catch (RDFException rdfEx)
        {
            // Trouble processing a configuration 
            throw new ConfigurationErrorException("RDF Exception: "+rdfEx.getMessage(), rdfEx) ;
            //return false ;
        } catch (IOException ioEx)
        {
            throw new ConfigurationErrorException("IO Exception: "+ioEx.getMessage(), ioEx) ;
            //return false;
        }
    }

    public void setDispatcher(Dispatcher d)
    {
        dispatcher = d ;
    }

    public Dispatcher getDispatcher()
    {
        return dispatcher ;
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
            Enumeration enum = servletConfig.getInitParameterNames();
            
            for (; enum.hasMoreElements();)
            {
                String s = (String) enum.nextElement();
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

            Enumeration enum = servletContext.getInitParameterNames();
            for (;enum.hasMoreElements();)
            {
                String s = (String) enum.nextElement();
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
 