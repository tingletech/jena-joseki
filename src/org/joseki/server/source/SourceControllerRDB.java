/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.source;

import org.joseki.vocabulary.JosekiVocab ;

import com.hp.hpl.jena.rdf.model.* ;
import com.hp.hpl.jena.db.* ;

import org.joseki.server.SourceController;
import org.joseki.server.SourceModel;
import org.joseki.server.module.Loadable;
import org.apache.commons.logging.* ;

/** SourceController for RDF models held in databases.
 *
 * @author Andy Seaborne
 * @version $Id: SourceControllerRDB.java,v 1.2 2005-01-03 20:26:30 andy_seaborne Exp $
 */ 

public class SourceControllerRDB implements SourceController, Loadable
{
    static Log logger = LogFactory.getLog(SourceControllerRDB.class.getName()) ;
    
    String serverURI ;
    
    // ----------------------------------------------------------
    // -- Loadable interface 

    public String getInterfaceURI() { return JosekiVocab.SourceController.getURI() ; } 
    public void init(Resource binding, Resource implementation) {}
    
    // ----------------------------------------------------------
    // -- SourceController interface
    
    // Called once, during configuration
    public SourceModel createSourceModel(Resource description, String _serverURI)
    {
        serverURI = _serverURI ;
        
        Resource dbURI = description.getProperty(JosekiVocab.attachedModel).getResource() ;
        String user, password, dbType, modelName, driver ;
        try 
        {
            // Get database details
            user =       description.getRequiredProperty(JosekiVocab.user).getString() ;
            password =   description.getRequiredProperty(JosekiVocab.password).getString();
            dbType =     description.getRequiredProperty(JosekiVocab.dbType).getString();
            modelName =  description.getRequiredProperty(JosekiVocab.dbModelName).getString();
            driver =     description.getRequiredProperty(JosekiVocab.dbDriver).getString();
        } catch (RDFException rdfEx)
        {
            logger.warn("Failed to gather all the database information: "+ rdfEx) ;
            return null ;
        }

        try { 
            if ( driver != null )
                Class.forName(driver).newInstance();
        } catch (Exception ex)
        {
            logger.warn("Failed to load the driver: "+ex.getMessage()) ;
        }
        
        try {
            IDBConnection conn = ModelFactory.createSimpleRDBConnection(dbURI.getURI(), user, password, dbType) ;
            ModelRDB modelRDB = ModelRDB.open(conn, modelName) ;
            SourceModel mSrc = new SourceModelPermanent(this, modelRDB, serverURI) ;
            logger.info("Connected to database: "+dbURI.getURI()) ;
            return mSrc ;
        } catch (RDFRDBException ex)
        {
            logger.warn("Failed to connect to database: "+ex.getMessage()) ;
            logger.debug("Exception", ex) ;
            if ( ex.getCause() != null )
                logger.warn("Connect failure cause : "+ex.getCause().getMessage()) ;
            return null ;
        }
    }
    
    public String getServerURI() { return serverURI ; }
    
    // Called when used.
    public void activate() { return ; }
    
    // Called when not in use any more.
    public void deactivate() { return ; }
    
    // Called each time a source needs to be built.
    public Model buildSource()
    {
        logger.warn("Attempt to build a database source") ;
        return null ;
    }
    
    // Called when released (if releasable)
    public void releaseSource()
    {
        logger.warn("Attempt to release a database source") ;
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
