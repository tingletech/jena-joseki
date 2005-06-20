/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package joseki3.server.module;

import org.apache.commons.logging.* ;

import org.joseki.vocabulary.JosekiModule ;
import joseki3.server.module.Loadable;
import org.joseki.util.PrintUtils ;

import com.hp.hpl.jena.rdf.model.* ;
import com.hp.hpl.jena.shared.* ;

/**
 * Load classes and instantiate new objects based on loadable classes.
 * Understands the RDF properties for naming and initializing a new instance. 
 * 
 * @author  Andy Seaborne
 * @version $Id$
 */

public class Loader
{
    private static Log log = LogFactory.getLog(Loader.class.getName());

    protected static ClassLoader classLoader = chooseClassLoader() ;

    public Loader()
    {
    }
 
    public Loadable loadAndInstantiate(Resource bindingResource, Class expectedType)  
    {
        log.debug("Attempt to load: "+PrintUtils.fmt(bindingResource)) ;
        
        Resource implementation = null ;
        try
        {
            // Alternative: pass in a top level resource and do ...
            // There can be many bindings
            //bindingResource = thing.getProperty(JosekiModule.binding).getResource() ;
            
            // Should be only one. 
            implementation = bindingResource
                                .getProperty(JosekiModule.implementation)
                                .getResource() ;
            log.trace("Implementation: "+PrintUtils.fmt(implementation)) ;

        } catch (JenaException ex)
        {
            log.warn("Binding/Implementation structure incorrect") ;
            return null ;
        }
        catch (NullPointerException nullEx)
        {
            log.warn("No definition for "+PrintUtils.fmt(bindingResource)) ;
            return null ;
        }

        String className = "<<unset>>" ;
        try {
            RDFNode n = implementation.getRequiredProperty(JosekiModule.className).getObject() ;
            className = classNameFromNode(n);
            if ( className == null )
            {
                // This should not happen as we used "getRequiredProperty"
                log.warn("Class name not found" ) ;
                return null ;
            }
            log.trace("Class name: "+className) ;
        } catch (PropertyNotFoundException noPropEx)
        {
            log.warn("No property 'className'") ;
            return null ;
        }
        
        try {            
            log.trace("Load module: " + className);
            Class classObj = null ;
            try {
                classObj = classLoader.loadClass(className);
            } catch (ClassNotFoundException ex)
            {
                log.warn("Class not found: "+className);
                return null ;
            }
            
            if ( classObj == null )
            {
                log.warn("Null return from classloader");
                return null ;
            }
            log.debug("Loaded: "+className) ;
            
            if ( ! Loadable.class.isAssignableFrom(classObj) )
            {
                log.warn(className + " does not support interface Loadable" ) ;
                return null;
            }

            Loadable module = (Loadable)classObj.newInstance();
            log.trace("New Instance created") ;
            
//            Statement s = bindingResource.getProperty(JosekiModule.interface_) ;
//            if ( s == null || s.getResource() == null)
//            {
//                log.warn("No 'joseki:interface' property or value not a resource for "+PrintUtils.fmt(bindingResource)) ;
//                return null ;
//            }
//            
//            String uriInterface = s.getResource().getURI() ;
//            
//            if ( uriInterface == null || ! module.getInterfaceURI().equals(uriInterface) )
//            {
//                if ( uriInterface == null )
//                {
//                    log.warn("No declared interface URI : expected "+module.getInterfaceURI()) ;
//                    return null ;
//                }
//                log.warn("Mismatch between expected and actual operation URIs: "+
//                        "Expected: "+uriInterface+" : Actual: "+module.getInterfaceURI() ) ;
//                return null ;
//            }

            if ( expectedType != null && ! expectedType.isInstance(module) )
            {
                log.warn("  " + className + " is not of class "+expectedType.getName()) ;
                return null;
            }

            // Looks good - now initialize it.
            module.init(bindingResource, implementation) ;

            //logger.debug("  Class: " + className);
            //log.debug("Module: " + uriInterface) ; 
            log.debug("Implementation: "+className);
            return module;
        }
        catch (Exception ex)
        {
            log.warn("Unexpected exception loading class " + className, ex);
            return null;
        }
    }
    
    static private ClassLoader chooseClassLoader()
    {
        ClassLoader classLoader = null ;
    
        if ( classLoader == null )
        {
            // Find our classloader - one that uses the /WEB-INF/lib and classes directory.
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader != null )
                log.trace("Using thread classloader") ;
        }
        
//        if (classLoader == null)
//        {
//            classLoader = this.getClass().getClassLoader();
//            if ( classLoader != null )
//                logger.trace("Using 'this' classloader") ;
//        }
        
        if ( classLoader == null )
        {
            classLoader = ClassLoader.getSystemClassLoader() ;
            if ( classLoader != null )
                log.trace("Using system classloader") ;
        }
        
        if ( classLoader == null )
            log.warn("Failed to find a classloader") ;
        return classLoader ;
    }
    
    public static String classNameFromNode(RDFNode n)
    {
        String className = null ;
        
        if ( n instanceof Literal )
        {
            Literal lit = (Literal)n ;
            className = lit.getLexicalForm() ;
            if ( className.startsWith("java:") ) 
                className.substring("java:".length()) ; 
            return className ;
        }

        Resource r = (Resource)n ;
        if ( r.isAnon() )
            return null ;
        
        if ( ! r.getURI().startsWith("java:") )
        {
            log.warn("** Class name is a URI but not from the java: scheme") ;
            return null ;
        }
        className = r.getURI().substring("java:".length()) ;
        return className ; 
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
