/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.soap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.resultset.ResultSetApply;
import com.hp.hpl.jena.query.resultset.ResultSetProcessor;
import com.hp.hpl.jena.query.util.NodeToLabelMap;
import com.hp.hpl.jena.rdf.model.RDFNode;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.Text;
import org.apache.axis.types.NMToken;
import org.apache.commons.logging.LogFactory;
import org.w3.www._2005.sparql_results.*;


public class AxisUtils
{
    static public Sparql resultSetToProtocol(ResultSet resultSet)
    {
        Sparql sparqlResults = new Sparql() ;
        Results xmlResults = new Results() ;
        xmlResults.setOrdered(resultSet.isOrdered()) ;
        xmlResults.setDistinct(resultSet.isDistinct()) ;
        
        Head head = new Head() ;
        sparqlResults.setHead(head) ;
        sparqlResults.setResults(xmlResults) ;
        
        ResultSetApply apply = new ResultSetApply(resultSet, new RS(head, xmlResults)) ;
        apply.apply() ;
        return sparqlResults ;
    }
    
    static class RS implements ResultSetProcessor
    {
        private Results xmlResults ;
        private List x = new ArrayList() ;      // Rows
        private Head head ;
        private List current ;                  // Current row
        private NodeToLabelMap bNodes = new NodeToLabelMap("b", false) ;
        
        public RS(Head head, Results xmlResults)
        {
            this.xmlResults = xmlResults ;
            this.head = head ;
        }

        public void start(ResultSet rs)
        {
            int n = rs.getResultVars().size() ;
            Variable[] vars = new Variable[n] ;
            int i = 0 ;
            for ( Iterator iter = rs.getResultVars().iterator() ; iter.hasNext() ; )
            {
                String varName = (String)iter.next() ;
                vars[i] = new Variable() ;
                vars[i].setName(new NMToken(varName)) ;
                i++ ;
            }
            head.setVariable(vars) ;
        }

        public void finish(ResultSet rs)
        {
            Result[] r = (Result[])x.toArray(new Result[0]) ;
            xmlResults.setResult(r) ;
        }

        public void start(QuerySolution qs)
        {
            current = new ArrayList() ;
            
        }

        public void finish(QuerySolution qs)
        {
            Binding[] bindings = (Binding[])current.toArray(new Binding[0]) ;
            Result row = new Result() ;
            row.setBinding(bindings) ;
            x.add(row) ;
            current = null ;
        }

        public void binding(String varName, RDFNode value)
        {
            Binding binding = new Binding() ;
            binding.setName(new NMToken(varName)) ;
            current.add(binding) ;

            if ( value.isURIResource() )
            {
                binding.setUri("http://example.org/#") ;
                return ;
            }
            
            if ( value.isAnon() )
            {
                Node n = value.asNode() ;
                String label = bNodes.asString(n) ;
                binding.setBnode(label) ;
                return ;
            }
            
            if ( value.isLiteral() )
            {
                Node n = value.asNode() ;
                String lex = n.getLiteralLexicalForm() ;
                String lang = n.getLiteralLanguage() ;
                String dtURI = n.getLiteralDatatypeURI() ;
                if ( lang != null && ! lang.equals("") ) 
                    LogFactory.getLog(RS.class).warn("Not implemented yet: RDF literals with lang") ;
                
                if ( dtURI != null )
                    LogFactory.getLog(RS.class).warn("Not implemented yet: RDF literals with datatype") ;
                
                Literal lit = new Literal() ;
                Text txt = new Text(lex) ;
                MessageElement mElt = new MessageElement(txt) ;
                lit.set_any(new MessageElement[]{mElt}) ;
                binding.setLiteral(lit) ;
                return ;
                
            }
            
            throw new JosekiAxisException(RS.class.getName()+" : Unexpected RDFNode: "+value) ;
        }
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