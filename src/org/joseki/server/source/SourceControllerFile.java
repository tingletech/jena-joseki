/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.source;

import org.joseki.vocabulary.JosekiVocab ;

import com.hp.hpl.jena.rdf.model.* ;
import com.hp.hpl.jena.util.FileManager ;

import org.joseki.server.*;
import org.joseki.server.module.Loadable;
import org.apache.commons.logging.* ;

/** SourceController for RDF models held in files.
 *  Does not cause updates to the model to be propagated back to the disk file.
 *
 * @author Andy Seaborne
 * @version $Id: SourceControllerFile.java,v 1.2 2005-01-03 20:26:30 andy_seaborne Exp $
 */ 

public class SourceControllerFile implements SourceController, Loadable
{
    static Log logger = LogFactory.getLog(SourceControllerFile.class.getName()) ;
    
    String serverURI ;
    String filename ;
    FileManager fileManager ;
    
    // ----------------------------------------------------------
    // -- Loadable interface 

    public String getInterfaceURI() { return JosekiVocab.SourceController.getURI() ; } 
    public void init(Resource binding, Resource implementation)
    {
        fileManager = FileManager.get() ;
    } 
    
    // ----------------------------------------------------------
    // -- SourceController interface
    
    // Called once, during configuration
    public SourceModel createSourceModel(Resource description, String _serverURI)
    {
        serverURI = _serverURI ;
        Statement aModelStmt = description.getProperty(JosekiVocab.attachedModel) ;

        if ( aModelStmt == null )
        {
            logger.warn("Model: "+serverURI + " : No internal Jena model specified") ;
            return null ;
        } 
        
        filename = null ;
        try { 
            filename = aModelStmt.getResource().getURI() ;
            logger.debug("File source controller: "+serverURI + " ==> " + filename) ;
        } catch (RDFException ex)
        {
            logger.warn("No internal resource URI for "+serverURI) ;
            return null ;
        }
        
        return new SourceModelFile(this, serverURI) ;
    }
    
    public String getServerURI() { return serverURI ; }
    
    // Called when used.
    public void activate() { logger.trace("activate: "+filename) ; return ; }
    
    // Called when not in use any more.
    public void deactivate() { logger.trace("deactivate: "+filename) ; return ; }

    // Called each time a source needs to be built.
    public Model buildSource()
    {
        logger.debug("buildSource: "+serverURI+ " ("+filename+")") ;
        try {
            return fileManager.loadModel(filename) ;
        } catch (RDFException rdfEx)
        {
            if ( rdfEx.getCause() != null )
                logger.warn(filename+": "+rdfEx.getCause()) ;
            else
                logger.warn(filename+": "+rdfEx) ;
            throw rdfEx ;
        }
    }
    
    // Called when released (if releasable)
    public void releaseSource()
    {
        logger.debug("releaseSource: "+serverURI+ " ("+filename+")") ;
    }
}

/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
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
