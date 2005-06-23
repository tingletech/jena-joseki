/*
 * (c) Copyright 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.source;

//import org.joseki.vocabulary.JosekiVocab ;

import com.hp.hpl.jena.rdf.model.* ;

import org.joseki.server.*;
//import org.joseki.server.module.Loadable;
import org.apache.commons.logging.* ;

/** A SourceController for permanent models (i.e. sources that are 
 *  loaded once and kept in memory).
 *
 * @author Andy Seaborne
 * @version $Id: SourceControllerPermanent.java,v 1.1 2005-06-23 09:55:58 andy_seaborne Exp $
 */ 

public class SourceControllerPermanent implements SourceController //, Loadable
{
    static Log logger = LogFactory.getLog(SourceControllerPermanent.class.getName()) ;
    
    String serverURI ;
    Model model ;

    // ----------------------------------------------------------
    // -- Own operations : 
    
    public SourceControllerPermanent(Model _model)
    {
        model = _model ;
    }
    
    // ----------------------------------------------------------
    // -- Loadable interface : this controller isn't a loadable thingy

    //public String getInterfaceURI() { return JosekiVocab.SourceController.getURI() ; } 
    //public void init(Resource binding, Resource implementation) { return ; }
    
    // ----------------------------------------------------------
    // -- SourceController interface
    
    // Called once, during configuration
    public SourceModel createSourceModel(Resource description, String _serverURI)
    {
        serverURI = _serverURI ;
        return new SourceModelPermanent(this, model, serverURI) ;
    }
    
    public String getServerURI() { return serverURI ; }
    
    // Called when used.
    public void activate() { logger.trace("activate") ; return ; }
    
    // Called when not in use any more.
    public void deactivate() { logger.trace("deactivate") ; return ; }

    // Called each time a source needs to be built.
    public Model buildSource()
    {
        logger.debug("buildSource: "+serverURI) ;
        return null ;
    }
    
    // Called when released (if releasable)
    public void releaseSource()
    {
        logger.debug("releaseSource: "+serverURI) ;
    }
}

/*
 * (c) Copyright 2004, 2005 Hewlett-Packard Development Company, LP
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
