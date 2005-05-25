/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors.sparql;

import java.io.PrintWriter ;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.*;

import com.hp.hpl.jena.rdf.model.*;

import org.joseki.Joseki ;
import org.joseki.server.*;
//import org.joseki.server.http.HttpResultSerializer;
import org.joseki.vocabulary.JosekiVocab;
import com.hp.hpl.jena.util.FileUtils ;
import com.hp.hpl.jena.util.FileManager;


import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.query.util.RelURI;

/** SPARQL operations
 * 
 * @author  Andy Seaborne
 * @version $Id: SPARQL.java,v 1.26 2005-05-25 08:40:11 andy_seaborne Exp $
 */

public class SPARQL extends QueryProcessorCom
{
    static private Log log = LogFactory.getLog(SPARQL.class) ;
    
    static final String P_QUERY          = "query" ;
    static final String P_NAMED_GRAPH    = "named-graph-uri" ;
    static final String P_DEFAULT_GRAPH  = "default-graph-uri" ;
    
    // Old names
    static final String P_DEFAULT_GRAPH_ALT1  = "graph-id" ;
    
    FileManager fileManager ; 
    
    public SPARQL()
    {
        super("SPARQL") ;
        fileManager = new FileManager() ;
        // Only know how to handle http URLs 
        fileManager.addLocatorURL() ;
    }

//    /** Processor.exec operation - provided by QueryProcessorCom*/
//    public void exec(Request request, Response response) throws ExecutionException
//    {
//        throw new QueryExecutionException(Response.rcNotImplemented, "SPARQL.execQuery") ;
//    }

    /** QueryProcessor.execQuery */
    public void execQuery(SourceModel src, String queryString,
                          Request request, Response response)
        throws QueryExecutionException
    {
        try {
            if (!(src instanceof SourceModelJena))
                throw new QueryExecutionException(
                    ExecutionError.rcOperationNotSupported,
                    "Wrong implementation - this SPARQL processor works with Jena models");
            
            // Process arguments

            // Decide target for the query.
            DataSource dataset = null ;
            
            try {
                
                String graphURL = request.getParam(P_DEFAULT_GRAPH) ;
                
                if ( graphURL == null )
                    // try again, alternative name
                    graphURL = request.getParam(P_DEFAULT_GRAPH_ALT1) ;
                
                List namedGraphs = request.getParams(P_NAMED_GRAPH) ;
                
                if ( graphURL != null && request.getBaseURI() != null )
                    graphURL = RelURI.resolve(graphURL, request.getBaseURI()) ;
                    
                // Look in cache for loaded graphs!!
                
                if ( graphURL != null && ! graphURL.equals(""))
                {
                    if ( dataset == null )
                        dataset = DataSetFactory.create() ;
                    
                    try {
                        Model model = fileManager.loadModel(graphURL) ;
                        dataset.setDefaultModel(model) ;
                        log.info("Load "+graphURL) ;
                    } catch (Exception ex)
                    {
                        log.info("Failed to load "+graphURL+" : "+ex.getMessage()) ;
                        throw new QueryExecutionException(
                                                          ExecutionError.rcArgumentUnreadable,
                                                          "Failed to load URL "+graphURL) ;
                    }
                }
                
                if ( namedGraphs != null )
                {
                    if ( dataset == null )
                        dataset = DataSetFactory.create() ;
                    for ( Iterator iter = namedGraphs.iterator() ; iter.hasNext() ; )
                    {
                        String uri = (String)iter.next() ;
                        try {
                            Model model = fileManager.loadModel(uri) ;
                            log.info("Load (named) "+uri) ;
                            dataset.addNamedModel(uri, model) ;
                        } catch (Exception ex)
                        {
                            log.info("Failer to load (named) "+uri+" : "+ex.getMessage()) ;
                            throw new QueryExecutionException(
                                          ExecutionError.rcArgumentUnreadable,
                                          "Failed to load URL "+uri) ;
                        }
                    }
                }
                
            } catch (Exception ex)
            {
                log.info("SPARQL parameter error",ex) ;
                throw new QueryExecutionException(
                     ExecutionError.rcArgumentError, "Parameter error");
            }
                
            // Sort out the query.
            
            if (queryString == null )
            {
                log.debug("Query: No query argument") ;
                throw new QueryExecutionException(
                    ExecutionError.rcQueryExecutionFailure,
                    "No query supplied");
            } 
             
            if ( queryString.equals("") )
            {
                log.debug("Query: No query argument") ;
                throw new QueryExecutionException(
                    ExecutionError.rcQueryExecutionFailure,
                    "Empty query string");
            }
            
            String queryStringLog = formatForLog(queryString) ;
            log.debug("Query: "+queryStringLog) ;
            
            // Build query
            
            Query query = null ;
            try {
                // NB synatx is ARQ (a superset of SPARQL)
                query = QueryFactory.create(queryString, Syntax.syntaxARQ) ;
            } catch (QueryException ex)
            {
                // TODO Enable errors to be sent as body
                //    response.setError(ExecutionError.rcQueryParseFailure)
                //    OutputString out = response.getOutputStream() ;
                //    out.write(something meaning full)
                String tmp = queryString +"\n\r" + ex.getMessage() ;
                throw new QueryExecutionException(ExecutionError.rcQueryParseFailure, "Parse error: \n"+tmp) ;
            } catch (Throwable thrown)
            {
                log.info("Query unknown error during parsing: "+queryStringLog, thrown) ;
                throw new QueryExecutionException(ExecutionError.rcQueryParseFailure, "Unknown Parse error") ;
            }

            // Finalize the target dataset
            if ( dataset == null )
            {
                // No dataset in protocol
                if ( ! query.hasDataSetDescription() )
                {
                    dataset = DataSetFactory.create() ;
                    // No dataset in query.
                    Model model = ((SourceModelJena)src).getModel() ;
                    dataset.setDefaultModel(model) ;
                }
            }
            
            QueryExecution qexec = null ;
            
            if ( query.hasDataSetDescription() )
                qexec = QueryExecutionFactory.create(query) ;
            else
                qexec = QueryExecutionFactory.create(query, dataset) ;
                
            // Test for SELECT in XML result set form
            boolean wantsAppXML = response.accepts("Accept", "application/xml") ;
            
            // Tests needed:
            // is app/xml prefed over app/rdf+xml?
            // test for is type X acceptable
            // response.acceptable(
            
            // Browser tests:
            // is text/* preferred over 
            
            if ( query.isSelectType() && wantsAppXML )
            {
                execQuerySelectXML(qexec, request, response) ;
                log.info("OK - URI="+request.getModelURI()+" : "+queryStringLog) ;
                return ;
            }
            
            if ( query.isAskType() )
                execQueryAsk(qexec, response) ;
            else
            {
                // SELECT / RDF results, CONSTRUCT or DESCRIBE
                Model results = execQueryModel(qexec) ;
                response.doResponse(results) ;
            }
            log.info("OK - URI="+request.getModelURI()+" : "+queryStringLog) ;
        }
        catch (QueryException qEx)
        {
            log.info("Query execution error: "+qEx) ;
            QueryExecutionException qExEx = new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, qEx.getMessage()) ;
            throw qExEx ;
            //response.doException(qExEx) ;
        }
    }

    private Model execQueryModel(QueryExecution qexec) throws QueryExecutionException
    {
        try {
            Query query = qexec.getQuery() ;
            
            if ( query.isSelectType() )
            {
                ResultSet results = qexec.execSelect() ;
                ResultSetFormatter rsFmt = new  ResultSetFormatter(results, query.getPrefixMapping()) ;
                return rsFmt.toModel() ;
            }
            
            if ( query.isConstructType() )
                return qexec.execConstruct() ;
            
            if ( query.isDescribeType() )
                return qexec.execDescribe() ; 
            
            if ( query.isAskType() )
            {
                log.warn("Not implemented: ASK queries") ;
                throw new QueryExecutionException(ExecutionError.rcOperationNotSupported, "ASK query") ;
            }
            
            log.warn("Unknown query type") ;
            throw new QueryExecutionException(ExecutionError.rcOperationNotSupported, "Unknown query type") ;
        }
        catch (QueryException qEx)
        {
            log.info("Query execution error: "+qEx) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, null) ;
        }
    }
    
    static final String paramStyleSheet = "stylesheet" ;
    
    private void execQuerySelectXML(QueryExecution qexec, Request request, Response response)
        throws QueryExecutionException
    {
        Query query = qexec.getQuery() ;
        String stylesheetURL = null ;
        if ( request.containsParam(paramStyleSheet) )
        {
            stylesheetURL = request.getParam(paramStyleSheet) ;
            if ( stylesheetURL != null )
            {
                stylesheetURL = stylesheetURL.trim() ;
                if ( stylesheetURL.length() == 0 )
                    stylesheetURL = null ;
            }
        }
        
        try {
            ResultSetFormatter fmt = new ResultSetFormatter(qexec.execSelect(), query.getPrefixMapping()) ;
            // TODO Remove any HTTPisms
            response.setMimeType(Joseki.contentTypeXML) ;
            // See doResponse as well - more header setting?  How to abstract?
            response.setResponseCode(Response.rcOK) ;
            response.startResponse() ;
            fmt.outputAsXML(response.getOutputStream(), stylesheetURL) ;
            response.finishResponse() ;
        }
        //throw new QueryExecutionException(Response.rcNotImplemented, "SPARQL.execQueryXML") ;
        catch (QueryException qEx)
        {
            log.info("Query execution error: "+qEx) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, null) ;
        }
    }

    private void execQueryAsk(QueryExecution qexec, Response response)
        throws QueryExecutionException
    {
        try {
            boolean result = qexec.execAsk() ;
            response.setMimeType(Joseki.contentTypeTextPlain) ;
            response.setResponseCode(Response.rcOK) ;
            response.startResponse() ;
            PrintWriter pw = FileUtils.asPrintWriterUTF8(response.getOutputStream()) ;
            pw.println(result?"yes":"no" ) ;
            pw.flush() ;
            response.finishResponse() ;
        }
        //throw new QueryExecutionException(Response.rcNotImplemented, "SPARQL.execQueryXML") ;
        catch (QueryException qEx)
        {
            log.info("Query execution error: "+qEx) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, null) ;
        }
        
    }

    
    
    public String getInterfaceURI() { return JosekiVocab.queryOperationSPARQL ; }

    public void init(Resource binding, Resource implementation) {}
    
    private String formatForLog(String queryString)
    {
        String tmp = queryString ;
        tmp = tmp.replace('\n', ' ') ;
        tmp = tmp.replace('\r', ' ') ;
        return tmp ;
    }
}

/*
 *  (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
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
