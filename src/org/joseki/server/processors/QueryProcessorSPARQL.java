/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;


import org.joseki.util.* ;
import org.joseki.vocabulary.*;
import org.joseki.server.* ;

import com.hp.hpl.jena.rdf.model.* ;
import com.hp.hpl.jena.query.* ;


import org.apache.commons.logging.* ;
import java.util.* ;

/** Query processor that executes a SPARQL query on a model
 * 
 * @author  Andy Seaborne
 * @version $Id: QueryProcessorSPARQL.java,v 1.1 2004-11-03 10:15:03 andy_seaborne Exp $
 */

public class QueryProcessorSPARQL extends QueryProcessorCom implements QueryProcessor
{
    static Log logger = LogFactory.getLog(QueryProcessorSPARQL.class.getName()) ;
    
    public QueryProcessorSPARQL()
    {
        super() ;
    }

    public String getInterfaceURI() { return JosekiVocab.queryOperationSPARQL ; }
        
    public Model execQuery(SourceModel src, String queryString, Request request)
        throws RDFException, QueryExecutionException
    {
        if (!(src instanceof SourceModelJena))
            throw new QueryExecutionException(
                ExecutionError.rcOperationNotSupported,
                "Wrong implementation - this RDQL processor works with Jena models");         
        Model model = ((SourceModelJena)src).getModel() ;
        QueryResults results = null ;
        
        try {
            if ( queryString == null || queryString.equals("") )
            {
                logger.debug("Query: No query string provided") ;
            }
            else
            {
                String tmp = queryString ;
                tmp = tmp.replace('\n', ' ') ;
                tmp = tmp.replace('\r', ' ') ;
                logger.debug("Query: "+tmp) ;
            }
            
            if (queryString == null)
                throw new QueryExecutionException(
                    ExecutionError.rcQueryExecutionFailure,
                    "No query supplied");
            
            Query query = null ;
            try {
                query = Query.create(queryString, Query.SyntaxDAWG) ;
            } catch (Throwable thrown)
            {
                logger.info("Query parse error: request failed") ;
                throw new QueryExecutionException(ExecutionError.rcQueryParseFailure, "Parse error") ;
            }
            
            query.setSource(model) ;

            // Should choose a better execution engine that specialises in building a single subgraph
            QueryExecution qe = QueryFactory.createQueryExecution(query) ;
            
            results = qe.execSelect() ;
            
            
            String format = request.getParam("format");
            // EXPERIMENTAL/undocumented
            String closure$ = request.getParam("closure");
            boolean closure = (closure$ != null && closure$.equalsIgnoreCase("true"));

            Model resultModel = ModelFactory.createDefaultModel();

            // EXPERIMENTAL/undocumented : closure over the result
            // variables
            if (closure)
            {
                doClosure(query, results, resultModel);
                return resultModel;
            }

            if (format == null)
            {
                for (; results.hasNext();)
                {
                    ResultBinding rb = (ResultBinding)results.next();
                    rb.mergeTriples(resultModel, query);
                }
                resultModel.setNsPrefixes(model);
                Map m = src.getPrefixes();
                if (m != null)
                    resultModel.setNsPrefixes(src.getPrefixes());
                
                return resultModel;
            }

            if (format.equalsIgnoreCase("BV") || format.equals("http://jena.hpl.hp.com/2003/07/query/BV"))
            {
                ResultSetFormatter fmt = new ResultSetFormatter(results) ;
                fmt.asRDF(resultModel);
                fmt.close() ;
                resultModel.setNsPrefix("rs", "http://jena.hpl.hp.com/2003/03/result-set#");
                return resultModel;
            }

            throw new QueryExecutionException(
                ExecutionError.rcOperationNotSupported,
                "Unknown results format requested: " + format);
        }
        catch (QueryException qEx)
        {
            logger.info("Query execution error: "+qEx) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, null) ;
        }
        finally 
        {
            if ( results != null )
                results.close() ;
        }
    }
    
    public Model execQuery(SourceModel aModel, Model queryModel, Request request)
        throws RDFException, QueryExecutionException
    {
        Model resultModel = null ;
        NodeIterator nIter = queryModel.listObjectsOfProperty(JosekiVocab.queryScript) ;
        for ( ; nIter.hasNext() ; )
        {
            RDFNode n = nIter.nextNode() ;
            if ( n instanceof Literal )
            {
                String queryString = ((Literal)n).getString() ;
                Model m = execQuery(aModel, queryString, request) ;
                if ( resultModel == null )
                    resultModel = m ;
                else
                    resultModel.add(m) ;
            }
        }
        return resultModel ;
    }
    
    private void doClosure(Query query, QueryResults results, Model resultModel)
    {
        for (; results.hasNext() ; )
        {
            ResultBinding rb = (ResultBinding)results.next() ;
            for ( Iterator iter = query.getResultVars().iterator() ; iter.hasNext() ; )
            {
                String var = (String)iter.next() ;
                Object x = rb.get(var) ;
                if ( ! ( x instanceof Resource ) )
                {
                    logger.warn("Non-resource in query closure: "+x) ;
                    continue ;
                }
                Closure.closure((Resource)x, false, resultModel) ;
            }
        }
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
