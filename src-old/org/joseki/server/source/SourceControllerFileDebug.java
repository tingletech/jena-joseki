/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.source;

import com.hp.hpl.jena.rdf.model.* ;

import org.joseki.server.module.*;
import org.joseki.server.*;
import org.apache.commons.logging.* ;

/** A simple SourceController which provides some monitoring facilities,
 *  based on the standard SourceControllerFile. 
 *  This is an example that does some logging on access to a file-backed model.
 *  It is a loadable Joseki module.
 *
 * @author Andy Seaborne
 * @version $Id: SourceControllerFileDebug.java,v 1.1 2005-06-23 09:55:58 andy_seaborne Exp $
 */ 

public class SourceControllerFileDebug
    extends SourceControllerFile
    implements SourceController, Loadable
{
    private static Log log = LogFactory.getLog(SourceControllerFileDebug.class.getName()) ;
    
    //static { System.err.println("SourceControllerDebug") ; }
    
//    String serverURI ;
//    String filename ;

    // ----------------------------------------------------------
    // -- Loadable interface 
    
    /** The URI for this loadable class.
     *  Must agree with the URI specified on loading.
     */
    public String getInterfaceURI() { return null ; }
    

    /** Allow an implementation to initialise, based on configuration file */
    public void init(Resource binding, Resource implementation)
    {
        if ( binding.isAnon() )
            log.info("Binding: []") ;
        else 
            log.info("Binding: "+binding.getURI()) ;
            
        if ( implementation.isAnon() )
            log.info("Implementation: []") ;
        else 
            log.info("Implementation: "+implementation.getURI()) ;

        // Parameters
        StmtIterator sIter = binding.listProperties() ;
        for ( ; sIter.hasNext() ;)
        {
            Statement s = sIter.nextStatement() ;
            log.info("  "+s) ;
        }
    }
    
    // ----------------------------------------------------------
    // -- SourceController interface
    
    // Called once, during configuration
    public SourceModel createSourceModel(Resource description, String _serverURI)
    {
        return super.createSourceModel(description, _serverURI) ;
    }
    
    public String getServerURI() { return super.getServerURI() ; }
    
    // Called when used.
    public void activate()
    {
        log.info("activate: " + getServerURI());
        super.activate();
    }

    // Called when not in use any more.
    public void deactivate()
    {
        log.info("deactivate: " + getServerURI());
        super.deactivate();
    }
    // Called each time a source needs to be built.
    public Model buildSource()
    {
        log.info("buildSource: "+getServerURI()) ;
        return super.buildSource() ;
    }
    
    public void releaseSource()
    {
        log.info("releaseSource: "+getServerURI()) ;
        super.releaseSource() ;
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
