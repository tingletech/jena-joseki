/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;

import java.util.* ;
import org.apache.commons.logging.* ;
import java.io.* ;

import org.joseki.server.source.* ;
import com.hp.hpl.jena.rdf.model.* ;

/**
 * A ModelSet is a collection of RDF models to be made available, together with the
 * specific configuration for that set.  Models themselves can have specific, custom
 * functionalty.
 * 
 * @author  Andy Seaborne
 * @version $Id: ModelSet.java,v 1.1 2004-11-03 10:15:01 andy_seaborne Exp $
 */


public class ModelSet {
    private static Log logger = LogFactory.getLog("org.joseki.server.ModelSet");
    
    // Current models, indexed by external name
    HashMap modelMap = new HashMap() ;

    /** Merge a configuration file into a model set.
     */

    public ModelSet load(String configFile) throws FileNotFoundException, RDFException
    {
        Configuration config = new Configuration() ;
        config.load(this, configFile) ;
        return this ;
    }

    /** Create an empty ModelSet */
    public ModelSet()
    {
    }
    
    public SourceModel findModel(String sourceURI)
    {
        return (SourceModel)modelMap.get(sourceURI) ;
    }
    
    
    /** Add an SourceModel to the ModelSet.
     * 
     * @param sourceURI      The external URI that the model will appear under
     * @param aModel         The SourceModel.
     * @see SourceModel
     */
    
    public void addModel(String sourceURI, SourceModel aModel)
    { 
        modelMap.put(sourceURI, aModel) ;
    }
    
    /** Add a Jena model to the ModelSet.
     *  Preferred mechanism is to added an AttachedModel.
     * 
     * @param sourceURI      The external URI that the model will appear under
     * @param model          The RDF model.
     * @see SourceModel
     */
    
    public void addModel(String sourceURI, Model model)
    { 
        // Not preferred
        modelMap.put(sourceURI, new SourceModelPermanent(null, model, sourceURI)) ;
    }

    /** Remove a model from the ModelSet.
     * 
     * @param sourceURI      The external URI that the model currently appreas under.
     */
    
    public void removeModel(String sourceURI)
    {
        SourceModel m = findModel(sourceURI) ;
        if ( m != null )
        {
            modelMap.remove(sourceURI) ;
            m.release() ;
        }
    }
    
    public int size() { return modelMap.size() ; }
    public Iterator sourceURIs() { return modelMap.keySet().iterator() ; }
    
    final static String serverRootURI = "http://server/" ;

    // Turn a config file URI into an entry for the attached models table.
    static String resource2serverURI(Resource r)
    {
        String serverURI = r.getURI();
        if ( ! serverURI.startsWith(serverRootURI) )
            logger.warn("URI '"+serverURI+"' for attached model does not start '"+serverRootURI+"'") ;
        
        // Take off the initial serverRootURI declaration
        // URIs of servlet requests do not have the host name attached.
        // ie. they are relative URI - relative to the web server.
        serverURI = "/" + serverURI.substring(serverRootURI.length());
        return serverURI ;
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
