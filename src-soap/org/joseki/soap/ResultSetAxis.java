/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.soap;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.LogFactory;
import org.w3.www._2005.sparql_results.*;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.util.LabelToNodeMap;
import com.hp.hpl.jena.query.util.NodeUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class ResultSetAxis implements ResultSet
{
    List rows = new ArrayList();
    List varNames = null ;
    boolean ordered = false ;
    boolean distinct = false ;
    Result[] results ;
    int index ; 
    LabelToNodeMap bNodeLabels = new LabelToNodeMap(false) ;
    Model model = ModelFactory.createDefaultModel() ;
    
    
    public ResultSetAxis(Sparql rs)
    {
        setVarNames(rs) ;
        Results r = rs.getResults() ;
        if ( r == null )
            throw new JosekiAxisException("Result set is not a result set!") ;

        ordered = r.isOrdered() ;
        distinct = r.isDistinct() ;
        results = r.getResult() ;
        index = 0 ;
    }
    
    
    public boolean hasNext()
    {
        if ( index < results.length)
            return true ;
        return false ;
    }

    public Object next()
    {
        return nextSolution() ;
    }

    public QuerySolution nextSolution()
    {
        Result xmlRow = results[index] ;
        index++ ;
        QuerySolution oneRow = makeResultBinding(xmlRow) ;
        return oneRow ;
    }

    public int getRowNumber()
    {
        return index ;
    }

    public List getResultVars()
    { return varNames ; }

    public boolean isOrdered()
    { return ordered ; }

    public boolean isDistinct()
    { return distinct ; }

    public void remove()
    {
        throw new UnsupportedOperationException(ResultSetAxis.class.getName()+".remove()") ;
    }

    private void setVarNames(Sparql rs)
    {
        varNames = new ArrayList() ;
        Variable[] vars = rs.getHead().getVariable() ;
        for ( int i = 0 ; i < vars.length ; i++ )
        {
            Variable v = vars[i] ;
            String vn = v.getName().toString() ;
            varNames.add(vn) ;
        }
    }


    private QuerySolution makeResultBinding(Result xmlRow)
    {
        QuerySolutionMap qs = new QuerySolutionMap() ;
        Binding[] bindings = xmlRow.getBinding() ;
        for ( int i = 0 ; i < bindings.length ; i++ )
        {
            Binding b = bindings[i] ;
            String varName = b.getName().toString() ;
    
            if ( b.getBnode() != null )
            {
                String label = b.getBnode() ;
                Node n = bNodeLabels.asNode(label) ;
                RDFNode rdfNode = NodeUtils.convertGraphNodeToRDFNode(n, model) ;
                qs.add(varName, rdfNode) ;
                continue ;
            }
    
            if ( b.getLiteral() != null )
            {
                Literal l = b.getLiteral() ;
                MessageElement[] elts = l.get_any() ;
                MessageElement e = elts[0] ;
                String lex = e.getNodeValue() ;
                if ( lex == null )
                {
                    LogFactory.getLog(ResultSetAxis.class).warn("Null literal text") ;
                    continue ;
                }
                
                if ( l.getDatatype() != null )
                {
                    String dtURI = l.getDatatype().toString() ;
                    RDFDatatype dt = TypeMapper.getInstance().getSafeTypeByName(dtURI);
                    qs.add(varName, model.createTypedLiteral(lex, dt)) ;
                    continue ;
                }
                
                // XML lang?
                qs.add(varName, model.createLiteral(lex)) ;
                continue ;
            }
            
            if ( b.getUnbound() != null ) continue ;
    
            if ( b.getUri() != null )
            {
                String uri = b.getUri() ;
                qs.add(varName, model.createResource(uri)) ;
                continue ;
            }
        }
        return qs ;
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