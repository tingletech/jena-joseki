/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package joseki3.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelSpec;
import com.hp.hpl.jena.rdf.model.Resource;


class DatasetDesc
{
    Model confModel ;
    // Resource will keep the config model around as well. 
    Resource defaultGraph = null ;
    Map namedGraphs = new HashMap() ;
    
    DatasetDesc(Model conf) { confModel = conf ; }
    
    /** @return Returns the dftGraph. */
    public Resource getDefaultGraph() { return defaultGraph ;  }
    /**  * @param dftGraph The dftGraph to set. */
    public void setDefaultGraph(Resource dftGraph) { this.defaultGraph = dftGraph ; }

    /** @return Returns the namedGraphs. */
    public Map getNamedGraphs()
    {
        return namedGraphs ;
    }
    /**  * @param namedGraphs The namedGraphs to set. */
    public void addNamedGraph(String uri, Resource r)
    {
        namedGraphs.put(uri, r) ;
    }
    
    
    private Model buildModel(Resource r)
    {
        ModelSpec mSpec = ModelFactory.createSpec(r, confModel) ; 
        Model m = ModelFactory.createModel(mSpec) ;
        Configuration.log.info("Building model: "+Utils.nodeLabel(r)) ;
        m.write(System.out, "N3") ;
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