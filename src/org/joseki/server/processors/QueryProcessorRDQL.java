/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;


import org.joseki.util.* ;
import org.joseki.vocabulary.*;
import org.joseki.server.* ;

import com.hp.hpl.jena.rdf.model.* ;

import com.hp.hpl.jena.rdql.* ;

import org.apache.commons.logging.* ;
import java.util.* ;

/** Query processor that executes an RDQL query on a model
 *  and returns the smallest subgraph that gives the same query results.
 *  The client may then recreate the variable bindings if it so wishes.
 * 
 *  @see com.hp.hpl.jena.joseki.HttpQuery
 *  @see com.hp.hpl.jena.joseki.QueryHTTP
 * 
 * @author  Andy Seaborne
 * @version $Id: QueryProcessorRDQL.java,v 1.5 2004-11-15 17:34:36 andy_seaborne Exp $
 */

public class QueryProcessorRDQL extends QueryProcessorModelCom
    implements QueryProcessor
{
    static Log logger = LogFactory.getLog(QueryProcessorRDQL.class.getName()) ;
    
    public QueryProcessorRDQL()
    {
        super() ;
    }

    public String getInterfaceURI() { return JosekiVocab.queryOperationRDQL ; }
        
    public Model execQuery(SourceModel src, String queryString, Request request)
        throws QueryExecutionException
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
                query = new Query(queryString) ;
            } catch (Throwable thrown)
            {
                logger.info("Query parse error: request failed") ;
                throw new QueryExecutionException(ExecutionError.rcQueryParseFailure, "Parse error") ;
            }
            
            query.setSource(model) ;

            // Should choose a better execution engine that specialises in building a single subgraph
            QueryExecution qe = new QueryEngine(query) ;
            
            results = qe.exec();
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
                    rb.mergeTriples(resultModel);
                }
                resultModel.setNsPrefixes(model);
                Map m = src.getPrefixes();
                if (m != null)
                    resultModel.setNsPrefixes(src.getPrefixes());
                
                return resultModel;
            }

            if (format.equalsIgnoreCase("BV") || format.equals("http://jena.hpl.hp.com/2003/07/query/BV"))
            {
                QueryResultsFormatter qfmt = new QueryResultsFormatter(results);
                qfmt.asRDF(resultModel);
                qfmt.close() ;
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
        catch (RDFException rdfEx)
        {
            logger.info("RDFException in query execution: "+rdfEx) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, "RDF Exception: "+rdfEx.getMessage()) ;
        }
        finally 
        {
            if ( results != null )
                results.close() ;
        }
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
