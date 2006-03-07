/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.processors;

import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.FileManager;

import org.apache.commons.logging.*;

import org.joseki.*;
import org.joseki.module.Loadable;
import org.joseki.util.GraphUtils;

public class SPARQL extends QueryCom implements Loadable
{
    // TODO Refactor into the stages of a query 
    static Log log = LogFactory.getLog(SPARQL.class) ;
    static final Property allowDatasetDescP = ResourceFactory.createProperty(JosekiVocab.getURI(), "allowExplicitDataset") ;
    static final Property allowWebLoadingP = ResourceFactory.createProperty(JosekiVocab.getURI(), "allowWebLoading") ;
    
    static Model m = ModelFactory.createDefaultModel() ;
    
    static final Literal XSD_TRUE   = m.createTypedLiteral(true) ; 
    static final Literal XSD_FALSE  = m.createTypedLiteral(false) ;
    
    public static final String P_QUERY          = "query" ;
    public static final String P_NAMED_GRAPH    = "named-graph-uri" ;
    public static final String P_DEFAULT_GRAPH  = "default-graph-uri" ;
    
    boolean allowDatasetDesc = false ;
    boolean allowWebLoading  = false ;
    
    int maxTriples = 10000 ;
    
    FileManager fileManager ; 
    
    
    public SPARQL()
    {
    }
    
    public void init(Resource service, Resource implementation)
    {
        log.info("SPARQL processor") ;
        
        fileManager = new FileManager() ;
        
        //fileManager = FileManager.get() ; // Needed for DAWG tests - but why?

        // Only know how to handle http URLs 
        fileManager.addLocatorURL() ;
        
        if ( service.hasProperty(allowDatasetDescP, XSD_TRUE) )
            allowDatasetDesc = true ;
        if ( service.hasProperty(allowWebLoadingP, XSD_TRUE) )
            allowWebLoading = true ;
        
        log.info("Dataset description: "+allowDatasetDesc+" // Web loading: "+allowWebLoading) ;
    }
    
    static Object globalLock = new Object() ; 
    
    public void execQuery(Request request, Response response, DatasetDesc datasetDesc) throws QueryExecutionException
    {
        execQueryWorker(request, response, datasetDesc) ;
    }
    
    private void execQueryWorker(Request request, Response response, DatasetDesc datasetDesc) throws QueryExecutionException
    {
        try {
            //log.info("Request: "+request.paramsAsString()) ;
            String queryString = request.getParam(P_QUERY) ;
            if  (queryString == null )
            {
                log.debug("No query argument") ;
                throw new QueryExecutionException(ReturnCodes.rcQueryExecutionFailure,
                    "No query string");    
            }
            if ( queryString.equals("") )
            {
                log.debug("Empty query string") ;
                throw new QueryExecutionException(ReturnCodes.rcQueryExecutionFailure,
                    "Empty query string");    
            }
            
            // ---- Query
            
            String queryStringLog = formatForLog(queryString) ;
            log.info("Query: "+queryStringLog) ;
            
            Query query = null ;
            try {
                // NB syntax is ARQ (a superset of SPARQL)
                query = QueryFactory.create(queryString, Syntax.syntaxARQ) ;
            } catch (QueryException ex)
            {
                // TODO Enable errors to be sent as body
                //    response.setError(ExecutionError.rcQueryParseFailure)
                //    OutputString out = response.getOutputStream() ;
                //    out.write(something meaning full)
                String tmp = queryString +"\n\r" + ex.getMessage() ;
                throw new QueryExecutionException(ReturnCodes.rcQueryParseFailure, "Parse error: \n"+tmp) ;
            } catch (Throwable thrown)
            {
                log.info("Query unknown error during parsing: "+queryStringLog, thrown) ;
                throw new QueryExecutionException(ReturnCodes.rcQueryParseFailure, "Unknown Parse error") ;
            }
            
            // Check arguments
            
            if ( ! allowDatasetDesc )
            {
                // Restrict to service dataset only. 
               if ( datasetInProtocol(request) )
                   throw new QueryExecutionException(ReturnCodes.rcArgumentError, "This service does not allow the dataset to be specified in the protocol request") ;
               if ( query.hasDatasetDescription() )
                   throw new QueryExecutionException(ReturnCodes.rcArgumentError, "This service does not allow the dataset to be specified in the query") ;
            }
            
            // ---- Dataset
            
            Dataset dataset = datasetFromProtocol(request) ;

            boolean useQueryDesc = false ;
            
            if ( dataset == null )
            {
                // No dataset in protocol
                if ( query.hasDatasetDescription() )
                    useQueryDesc = true ;
                // If in query, then the query engine will do the loading.
            }
            
            // Use the service dataset description if
            // not in query and not in protocol. 
            if ( !useQueryDesc && dataset == null )
            {
                if ( datasetDesc != null )
                    dataset = datasetDesc.getDataset() ;
            }
            
            if ( !useQueryDesc && dataset == null )
                throw new QueryExecutionException(ReturnCodes.rcQueryExecutionFailure, "No datatset given") ;
            
            QueryExecution qexec = null ;
            
            if ( useQueryDesc )
                qexec = QueryExecutionFactory.create(query) ;
            else
                qexec = QueryExecutionFactory.create(query, dataset) ;
            
            if ( query.hasDatasetDescription() )
                qexec = QueryExecutionFactory.create(query) ;
            else
                qexec = QueryExecutionFactory.create(query, dataset) ;
            
            response.setCallback(new QueryExecutionClose(qexec)) ;
            
            if ( query.isSelectType() )
            {
                response.setResultSet(qexec.execSelect()) ;
                log.info("OK/select: "+queryStringLog) ;
                return ;
            }
            
            if ( query.isConstructType() )
            {
                Model model = qexec.execConstruct() ;
                response.setModel(model) ;
                log.info("OK/construct: "+queryStringLog) ;
                return ;
            }

            if ( query.isDescribeType() )
            {
                Model model = qexec.execDescribe() ;
                response.setModel(model) ;
                log.info("OK/describe: "+queryStringLog) ;
                return ;
            }

            if ( query.isAskType() )
            {
                boolean b = qexec.execAsk() ;
                response.setBoolean(b) ;
                log.info("OK/ask: "+queryStringLog) ;
                return ;
            }

            log.warn("Unknown query type - "+queryStringLog) ;
        }
        catch (QueryException qEx)
        {
            log.info("Query execution error: "+qEx) ;
            QueryExecutionException qExEx = new QueryExecutionException(ReturnCodes.rcQueryExecutionFailure, qEx.getMessage()) ;
            throw qExEx ;
        }
        catch (NotFoundException ex)
        {
            // Trouble loading data
            log.info(ex.getMessage()) ;
            QueryExecutionException qExEx = new QueryExecutionException(ReturnCodes.rcResourceNotFound, ex.getMessage()) ;
            throw qExEx ;
        }
        catch (JenaException ex)
        {   // Parse exceptions
            log.info("JenaException: "+ex.getMessage()) ;
            QueryExecutionException qExEx = new QueryExecutionException(ReturnCodes.rcArgumentUnreadable, ex.getMessage()) ;
            throw qExEx ;
        }
        catch (RuntimeException ex)
        {   // Parse exceptions
            log.info("Exception: "+ex.getMessage()) ;
            QueryExecutionException qExEx = new QueryExecutionException(ReturnCodes.rcInternalError, ex.getMessage()) ;
            throw qExEx ;
        }
    }
    
    private String formatForLog(String queryString)
    {
        String tmp = queryString ;
        tmp = tmp.replace('\n', ' ') ;
        tmp = tmp.replace('\r', ' ') ;
        return tmp ;
    }
    
    private boolean datasetInProtocol(Request request)
    {
        String d = request.getParam(P_DEFAULT_GRAPH) ;
        if ( d != null && !d.equals("") )
            return true ;
        
        List n = request.getParams(P_NAMED_GRAPH) ;
        if ( n != null && n.size() > 0 )
            return true ;
        return false ;
    }
    
    private Dataset datasetFromProtocol(Request request) throws QueryExecutionException
    {
        try {
            
            List graphURLs = request.getParams(P_DEFAULT_GRAPH) ;
            List namedGraphs = request.getParams(P_NAMED_GRAPH) ;
            
            if ( graphURLs.size() == 0 && namedGraphs.size() == 0 )
                return null ;
            
            DataSource dataset = null ;
            
//          if ( graphURL != null && request.getBaseURI() != null )
//          graphURL = RelURI.resolve(graphURL, request.getBaseURI()) ;
            
            // Look in cache for loaded graphs!!
            
            if ( graphURLs != null )
            {
                if ( dataset == null )
                    dataset = DatasetFactory.create() ;
                
                Model model = ModelFactory.createDefaultModel() ;
                for ( Iterator iter = graphURLs.iterator() ; iter.hasNext() ; )
                {
                    String uri = (String)iter.next() ;
                    if ( uri == null )
                    {
                        log.warn("Null "+P_DEFAULT_GRAPH+ " (ignored)") ;
                        continue ;
                    }
                    if ( uri.equals("") )
                    {
                        log.warn("Empty "+P_DEFAULT_GRAPH+ " (ignored)") ;
                        continue ;
                    }
                
                    try {
                        GraphUtils.loadModel(model, uri, maxTriples) ;
                        log.info("Load (default) "+uri) ;
                    } catch (Exception ex)
                    {
                        log.info("Failed to load (default) "+uri+" : "+ex.getMessage()) ;
                        throw new QueryExecutionException(
                                                          ReturnCodes.rcArgumentUnreadable,
                                                          "Failed to load URL "+uri) ;
                    }
                }
                dataset.setDefaultModel(model) ;
            }
            
            if ( namedGraphs != null )
            {
                if ( dataset == null )
                    dataset = DatasetFactory.create() ;
                for ( Iterator iter = namedGraphs.iterator() ; iter.hasNext() ; )
                {
                    String uri = (String)iter.next() ;
                    if ( uri == null )
                    {
                        log.warn("Null "+P_NAMED_GRAPH+ " (ignored)") ;
                        continue ;
                    }
                    if ( uri.equals("") )
                    {
                        log.warn("Empty "+P_NAMED_GRAPH+ " (ignored)") ;
                        continue ;
                    }
                    try {
                        Model model = fileManager.loadModel(uri) ;
                        log.info("Load (named) "+uri) ;
                        dataset.addNamedModel(uri, model) ;
                    } catch (Exception ex)
                    {
                        log.info("Failer to load (named) "+uri+" : "+ex.getMessage()) ;
                        throw new QueryExecutionException(
                                                          ReturnCodes.rcArgumentUnreadable,
                                                          "Failed to load URL "+uri) ;
                    }
                }
            }
            
            return dataset ;
            
        } catch (Exception ex)
        {
            log.info("SPARQL parameter error",ex) ;
            throw new QueryExecutionException(
                                              ReturnCodes.rcArgumentError, "Parameter error");
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
