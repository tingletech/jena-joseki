/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;


import org.joseki.vocabulary.*;
import org.joseki.server.* ;

import com.hp.hpl.jena.rdf.model.* ;
import com.hp.hpl.jena.query.* ;

import org.apache.commons.logging.* ;

/** Query processor that executes a SPARQL query on a model
 * 
 * @author  Andy Seaborne
 * @version $Id: QueryProcessorSPARQL.java,v 1.7 2004-11-15 17:34:36 andy_seaborne Exp $
 */

public class QueryProcessorSPARQL extends QueryProcessorModelCom implements QueryProcessor
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
        
        return queryToModel(model, queryString) ;
        
    }
    
    private Model queryToModel(Model model, String queryString) throws QueryExecutionException
    {
        try {
            Query query = null ;
            try {
                query = Query.create(queryString, Query.SyntaxSPARQL) ;
            } catch (QueryException ex)
            {
                // TODO Enable errors to be sent as body
                //    response.setError(ExecutionError.rcQueryParseFailure)
                //    OutputString out = response.getOutputStream() ;
                //    out.write(something meaning full)
                
                // Fake it.
                String tmp = queryString ;
                tmp = tmp.replace('\n', ' ') ;
                tmp = tmp.replace('\r', ' ') ;
                logger.info("Query parse exception: "+tmp) ;
                
                tmp = "Query:\n\r"+queryString +"\n\r" + ex.getMessage() ;
                throw new QueryExecutionException(ExecutionError.rcQueryParseFailure, "Parse error: \n"+tmp) ;
            } catch (Throwable thrown)
            {
                String tmp = queryString ;
                tmp = tmp.replace('\n', ' ') ;
                tmp = tmp.replace('\r', ' ') ;
                logger.info("Query unknown error during parsing: "+tmp+" "+thrown) ;
                throw new QueryExecutionException(ExecutionError.rcQueryParseFailure, "Unknown Parse error") ;
            }
            
            query.setSource(model) ;
            
            QueryExecution qe = QueryFactory.createQueryExecution(query) ;
            
            if ( query.isSelectType() )
            {
                QueryResults results = qe.execSelect() ;
                ResultSetFormatter rsFmt = new  ResultSetFormatter(results) ;
                return rsFmt.toModel() ;
            }
            
            if ( query.isConstructType() )
                return qe.execConstruct() ;
            
            if ( query.isDescribeType() )
                return qe.execDescribe() ; 
            
            if ( query.isAskType() )
            {
                logger.warn("Not implemented: ASK queries") ;
                throw new QueryExecutionException(ExecutionError.rcOperationNotSupported, "ASK query") ;
            }
            
            logger.warn("Unknown query type") ;
            throw new QueryExecutionException(ExecutionError.rcOperationNotSupported, "Unknown query type") ;
        }
        catch (QueryException qEx)
        {
            logger.info("Query execution error: "+qEx) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, null) ;
        }
    }
    
    public void execQuery(SourceModel src, String queryString, Request request, Response response)
    {
        try {
            Model m = execQuery(src, queryString, request) ;
            response.doResponse(m) ;
        }
        catch (QueryExecutionException qEx) { response.doException(qEx) ; }
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
