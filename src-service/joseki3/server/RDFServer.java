/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package joseki3.server;

import java.util.* ;
import org.mortbay.jetty.* ;
import org.mortbay.util.MultiException;

import org.mortbay.jetty.servlet.* ;

//import org.joseki.Joseki ;
import org.apache.commons.logging.* ;

/** Standalone server.
 * 
 * @version $Id$
 * @author  Andy Seaborne
 */


public class RDFServer
{
    static final Log log = LogFactory.getLog(RDFServer.class.getName()) ;

    public static final String ServiceRegistryName = "Service Registry" ;
    static int count = 0 ;

    Server server = null ;
    WebApplicationContext webAppContextJoseki = null ;
    boolean earlyInitialize = true ;
    
    int port = -1 ;

    /** System property for the port number. */    
    public static final String propertyPort       = "org.joseki.rdfserver.port" ;

    /** Override the web.xml init-param for the configuration file.
     */  
    public static final String propertyModelSet   = "org.joseki.rdfserver.port" ;
    
    /** Default location for the Joseki server */
    public static final String defaultServerBaseURI = "/" ;
    
    /** Default configuration file */
    public static final String defaultConfigFile = "etc/joseki.n3" ;

    /** Value for the config file meaning "no configuration"
     */  
    public static final String noMapValue         = "<<null>>" ;
    
    /** Create a new RDFServer on the default port or as specifed by the system property jena.rdfserver.port */
    public RDFServer() { this(defaultConfigFile) ; }

    /** Create a new RDFServer using the named configuration file
     * @param configFile
     */
    public RDFServer(String configFile)
    {
        String tmp = System.getProperty(propertyPort, Joseki.defaultPort+"") ;
        int p = Integer.parseInt(tmp) ;
        init(configFile, Joseki.defaultPort, defaultServerBaseURI) ;
    }

    /** Create a new RDFServer
     * @param configFile
     * @param port
     */
    public RDFServer(String configFile, int port) { init(configFile, port, defaultServerBaseURI) ; }

    /** Creates new RDFServer using the named configuration file
     * @param configFile
     * @param port
     * @param serverBaseURI
     */
    public RDFServer(String configFile, int port, String serverBaseURI) 
    {
        init(configFile, port, serverBaseURI) ;
    }

    private void init(String configFile, int port, String serverBaseURI) 
    {
        boolean earlyInitialize = true ;
        
        if (earlyInitialize)
            createDispatcher(configFile) ;
        else
            // Set it so the servlet finds it later
            // probably during servlet creation on first request.
            System.setProperty(propertyModelSet, configFile) ;

        // Build the web application and server
        try {
            server = new Server() ;
            server.addListener(":"+port) ;
            
            webAppContextJoseki = 
                    server.addWebApplication(serverBaseURI, "./webapps/joseki3/") ;
            
            if ( webAppContextJoseki == null )
                throw new JosekiServerException("Failed to create the web application (null returned)") ;
            log.info("Created Joseki server: port="+port+"  URI="+serverBaseURI) ;
        } catch (Exception ex)
        {
            log.warn("RDFServer: Failed to create web application server: "+ex) ;
        }
    }

    
    // This code creates the dispatcher and the configuration 
    // very early, before the web application exists and before the servlet exists
    // let alone intitialised.
    // This means that files read now must be found without the servlet context
    // (just the current directory and the classpath).
    // However, it does mean that it happens now and not during the first request
    // received, which greatly helps simple use (and Joseki development).
    
    private void createDispatcher(String configFile)
    {
        if (System.getProperty(propertyModelSet) == null)
            // This tells the servlet to load nothing later - not even from the
            // default location
            // Rather than know the servlet class, we set a system property.
            // (Not necessary now as the servlet will find the dispatcher via
            // the registry)
            System.setProperty(propertyModelSet, noMapValue);

        // Build the dispatcher
        try
        {
            // Create a dispatcher and register it
            // The servlet finds it from the registry.
            ServiceRegistry services = new ServiceRegistry();
            Registry.add(ServiceRegistryName, services);

            if (configFile == null)
            {
                log.info("No initial configuration");
                return ;
            }
            Configuration conf = new Configuration(configFile, services) ;
        }
        catch (Exception ex)
        {
            if (ex instanceof ConfigurationErrorException)
                throw (ConfigurationErrorException)ex;
            throw new ConfigurationErrorException(ex);
        }
    }
    
    /** Start the server */
    
    public void start()
    {
        try {
            if ( ! server.isStarted() )
                server.start() ;
            //org.mortbay.util.Log.instance().disableLog();
            
        } catch (MultiException ex)
        {
            List exs = ex.getExceptions() ;
            java.net.BindException bindException = null ;
            for ( Iterator iter = exs.iterator() ; iter.hasNext() ; )
            {
                Exception ex2 = (Exception)iter.next() ;
                if ( ex2 instanceof java.net.BindException )
                {
                    bindException = (java.net.BindException)ex2 ;
                    continue ;
                }
                log.warn("MultiException: "+ex2) ;
            }
            
            if ( bindException != null )
            {
                log.error("Bind exception: "+bindException.getMessage()) ;
                //System.err.println("Bind exception: "+bindException.getMessage()) ;
                System.exit(99) ;
            }
            throw new JosekiServerException("Failed to start web application server") ;
        }
        catch (Exception exMisc)
        {
            log.warn("Exception (server startup): "+exMisc) ;
            System.exit(98) ;
        }
        
        if ( ! earlyInitialize )
            return ;
        
        // Check that the dispatcher registry seen by the webapp is the
        // same as the one created during initialization.  That is, the
        // webapp does not have its own class for this.
        
        try {
            ClassLoader cl = webAppContextJoseki.getClassLoader() ;
            
            if ( cl == null )
            {    
                log.warn("No classloader for webapp!") ;
                return ;
            }

            Class cls = cl.loadClass(ServiceRegistry.class.getName());

            if ( ! cls.isAssignableFrom(ServiceRegistry.class))
            {    
                log.warn("Found another dispatcher configuration subsystem in the web apllication");
                log.warn("Suspect a second copy of joseki.jar in WEB-INF/lib") ;
                throw new ConfigurationErrorException("ServiceRegistry clash") ;
            }
        } catch (ClassNotFoundException ex)
        {
            log.info("Class not found");
        }
    }

    /** Stop the server.  On exit from this method, it is <strong>not</strong>
     *  guaranteed that all server threads have ended. 
     */
    
    public void stop()
    {
        try
        {
            server.stop() ;
        } catch (InterruptedException e)
        {
            log.warn("Problems stopping server: ",e) ;
        }
        
    }

    public int getPort() { return port ; }
   
    public Server getServer() { return server ; }
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
