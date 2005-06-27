/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelSpec;
import com.hp.hpl.jena.rdf.model.Resource;


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
    
    /** @param Set the resource to use to make the default graph. */
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
    
    
    private Model buildModel(Resource r)
    {
        ModelSpec mSpec = ModelFactory.createSpec(r, confModel) ; 
        Model m = ModelFactory.createModel(mSpec) ;
        log.info("Building model: "+Utils.nodeLabel(r)) ;
        return m ;
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
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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