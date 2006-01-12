/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.JenaModelSpec;


public class DatasetDesc
{
    static Log log = LogFactory.getLog(DatasetDesc.class) ;
    Model confModel ;
    // Resource will keep the config model around as well. 
    Resource defaultGraph = null ;
    Map namedGraphs = new HashMap() ;
    Dataset dataset = null ;
    
    public DatasetDesc(Model conf) { confModel = conf ; }
    
    /** @return Returns the resources for the default graph. */
    public Resource getDefaultGraph() { return defaultGraph ;  }
    
    /** @param dftGraph Set the resource to use to make the default graph. */
    public void setDefaultGraph(Resource dftGraph) { this.defaultGraph = dftGraph ; }

    /** @return Returns the namedGraphs. */
    public Map getNamedGraphs()
    {
        return namedGraphs ;
    }

    public void addNamedGraph(String uri, Resource r)
    {
        namedGraphs.put(uri, r) ;
    }
    
    /** Drop any dataset to free system resources */ 
    public void freeDataset() { dataset = null ; }
    
    public Dataset getDataset()
    {
        if ( dataset == null )
        {
            DataSource ds = DatasetFactory.create() ;
            if ( getDefaultGraph() != null )
            {
                Model m = buildModel(getDefaultGraph()) ;
                ds.setDefaultModel(m) ;
            }
            
            for ( Iterator iter = namedGraphs.keySet().iterator() ; iter.hasNext() ; )
            {
                String n = (String)iter.next() ;
                Resource r = (Resource)namedGraphs.get(n) ; 
                ds.addNamedModel(n, buildModel(r)) ;
            }
            dataset = ds ;
        }
        return dataset ;
    }
    
    public void clearDataset() { dataset = null ; }
    
    private Model buildModel(Resource r)
    {
        log.info("Attempt to build dataset: "+Utils.nodeLabel(r)) ;
//      {
//      StmtIterator sIter = r.listProperties() ;
//      while ( sIter.hasNext() )
//          log.info("  "+sIter.nextStatement()) ;
//  }

        if ( r.hasProperty(JenaModelSpec.loadWith) || r.hasProperty(JenaModelSpec.maker) )
        {
            log.warn("Build model using JenaModelSpec (deprecated)") ;
            return buildModelOld(r) ;
        }

        try {
            return Assembler.general.openModel( r, Mode.REUSE ) ;
        } catch (Exception ex) 
        { throw new JosekiServerException("Failed to assemble model", ex) ; }
    }
    
    private Model buildModelOld(Resource r)
    {
        ModelSpec mSpec = ModelFactory.createSpec(r, confModel) ;
        
        if ( r.hasProperty(JenaModelSpec.maker) )
        {
            String modelName = r.getProperty(JenaModelSpec.modelName).getString() ;
            log.info("Building named model: "+Utils.nodeLabel(r)+" / "+modelName) ;
        }
        else
            log.info("Building model: "+Utils.nodeLabel(r)) ;
        
        // TODO Use this code when Jena Assemblers available 
        //        Model m = mSpec.openModel() ;
        //        return m ;  
        
        //return mSpec.createDefaultModel() ;
        //BEST -- ?? -- return mSpec.createModel() ;
        
        if ( r.hasProperty(JenaModelSpec.loadWith) )
        {
            // Assume it is a in-memory model
            log.info("Creating a memory model") ;
            Model m = ModelFactory.createDefaultModel() ;
            String data = r.getProperty(JenaModelSpec.loadWith).getResource().getURI() ;
            FileManager.get().readModel(m, data) ;
            return m ;
        }
        
        if ( r.hasProperty(JenaModelSpec.maker) )
        {
            Resource r2 = r.getProperty(JenaModelSpec.maker).getResource() ;
            if ( r2.hasProperty(JenaModelSpec.hasConnection) )
            {
                // Database
                Model m = mSpec.openModel() ;
                //                log.warn("Only accessing asserted statements in database model") ;
                //                ModelRDB mdb = (ModelRDB)m ;
                //                mdb.setQueryOnlyAsserted(true) ;
                //                mdb.setDoFastpath(true) ;
                
                return m ;        
            }
        }
        throw new JosekiServerException("Unrecognized model description: "+Utils.nodeLabel(r)) ;
    }
    
    public String toString()
    {
        StringBuffer sbuff = new StringBuffer() ;
        sbuff.append("{") ;
        boolean first = true ;  
        if ( defaultGraph != null )
        {
            sbuff.append(Utils.nodeLabel(defaultGraph)) ;
            first = false ;
        }
        for ( Iterator iter = namedGraphs.keySet().iterator() ; iter.hasNext() ; )
        {
            if ( first )
                sbuff.append(" ") ;
            first = false ;
            String n = (String)iter.next() ;
            Resource r = (Resource)namedGraphs.get(n) ;
            sbuff.append("(") ;
            sbuff.append(n) ;
            sbuff.append(", ") ;
            sbuff.append(Utils.nodeLabel(r)) ;
            sbuff.append(")") ;
        }
        sbuff.append("}") ;
        return sbuff.toString() ;
    }
}
/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
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
