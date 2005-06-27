/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.processors;

import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.FileManager;

import org.apache.commons.logging.*;

import org.joseki.*;
import org.joseki.module.Loadable;


public class SPARQL extends QueryCom implements Loadable
{
    static Log log = LogFactory.getLog(SPARQL.class) ;
    static final Property allowDatasetDescP = ResourceFactory.createProperty(JosekiVocab.getURI(), "allowExplicitDataset") ;
    static final Property allowWebLoadingP = ResourceFactory.createProperty(JosekiVocab.getURI(), "allowWebLoading") ;
    
    static Model m = ModelFactory.createDefaultModel() ;
    
    static final Literal XSD_TRUE   = m.createTypedLiteral(true) ; 
    static final Literal XSD_FALSE  = m.createTypedLiteral(false) ;
    
    static final String P_QUERY          = "query" ;
    static final String P_NAMED_GRAPH    = "named-graph-uri" ;
    static final String P_DEFAULT_GRAPH  = "default-graph-uri" ;
    
    boolean allowDatasetDesc = false ;
    boolean allowWebLoading  = false ;
    
    FileManager fileManager ; 
    
    
    public SPARQL()
    {
    }
    
    public void init(Resource service, Resource implementation)
    {
        log.info("SPARQL processor") ;
        
        fileManager = new FileManager() ;
        // Only know how to handle http URLs 
        fileManager.addLocatorURL() ;
        
        if ( service.hasProperty(allowDatasetDescP, XSD_TRUE) )
            allowDatasetDesc = true ;
        if ( service.hasProperty(allowWebLoadingP, XSD_TRUE) )
            allowWebLoading = true ;
        
        log.info("Dataset description: "+allowDatasetDesc+" // Web loading: "+allowWebLoading) ;
    }
    
    public void execQuery(Request request, Response response, DatasetDesc datasetDesc) throws QueryExecutionException
    {
        try {
            //log.info("Request: "+request.paramsAsString()) ;
            String queryString = request.getParam(P_QUERY) ;
            if  (queryString == null )
            {
                log.debug("No query argument") ;
                throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure,
                    "No query string");    
            }
            if ( queryString.equals("") )
            {
                log.debug("Empty query string") ;
                throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure,
                    "Empty query string");    
            }
            
            // ---- Query
            
            String queryStringLog = formatForLog(queryString) ;
            log.info("Query: "+queryStringLog) ;
            
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
            
            // Check arguments
            
            if ( ! allowDatasetDesc )
            {
                // Restrict to service dataset only. 
               if ( datasetInProtocol(request) )
                   throw new QueryExecutionException(ExecutionError.rcArgumentError, "This service does not allow the dataset to be specified in the protocol request") ;
               if ( query.hasDatasetDescription() )
                   throw new QueryExecutionException(ExecutionError.rcArgumentError, "This service does not allow the dataset to be specified in the query") ;
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
                throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, "No datatset given") ;
            
            QueryExecution qexec = null ;
            
            if ( useQueryDesc )
                qexec = QueryExecutionFactory.create(query) ;
            else
                qexec = QueryExecutionFactory.create(query, dataset) ;
            
            if ( query.hasDatasetDescription() )
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
                // Includes text
                execQuerySelectXML(query, qexec, request, response) ;
                log.info("OK: "+queryStringLog) ;
                return ;
            }
            
            if ( query.isAskType() )
                execQueryAsk(query, qexec, request, response) ;
            else
            {
                // SELECT / RDF results, CONSTRUCT or DESCRIBE
                Model results = execQueryModel(query, qexec) ;
                response.doResponse(results) ;
            }
            log.info("OK - "+queryStringLog) ;
        }
        catch (QueryException qEx)
        {
            log.info("Query execution error: "+qEx) ;
            QueryExecutionException qExEx = new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, qEx.getMessage()) ;
            throw qExEx ;
            //response.doException(qExEx) ;
        }
    }

    private Model execQueryModel(Query query, QueryExecution qexec) throws QueryExecutionException
    {
        try {
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
            
            log.warn("Unknown query type") ;
            throw new QueryExecutionException(ExecutionError.rcOperationNotSupported, "Unknown query type") ;
        }
        catch (QueryException qEx)
        {
            log.info("Query execution error (Graph results): "+qEx) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, null) ;
        }
    }
  
    static final String paramStyleSheet = "stylesheet" ;
    
    private void execQuerySelectXML(Query query, QueryExecution qexec, Request request, Response response)
    throws QueryExecutionException
    {
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
            log.info("Query execution error (SELECT/XML): "+qEx) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, qEx.getMessage()) ;
        }
        catch (NotFoundException ex)
        {
            log.info("Query execution error (SELECT/XML): "+ex) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, ex.getMessage()) ;
        }
    }

    
    private void execQueryAsk(Query query, QueryExecution qexec, Request request, Response response)
    throws QueryExecutionException
    {
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
            boolean result = qexec.execAsk() ;
            
            response.setMimeType(Joseki.contentTypeXML) ;
            response.setResponseCode(Response.rcOK) ;
            response.startResponse() ;
            ResultSetFormatter.outputAsXML(response.getOutputStream(), result) ;
            response.finishResponse() ;
        }
        //throw new QueryExecutionException(Response.rcNotImplemented, "SPARQL.execQueryXML") ;
        catch (QueryException qEx)
        {
            log.info("Query execution error (ASK): "+qEx) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, null) ;
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
        
        List n = (List)request.getParams(P_NAMED_GRAPH) ;
        if ( n != null && n.size() > 0 )
            return true ;
        return false ;
    }
    
    private Dataset datasetFromProtocol(Request request) throws QueryExecutionException
    {
        try {
            
            String graphURL = request.getParam(P_DEFAULT_GRAPH) ;
            List namedGraphs = request.getParams(P_NAMED_GRAPH) ;
            if ( ( graphURL == null || graphURL.equals("") )  
                    && namedGraphs.size() == 0 )
                return null ;
            DataSource dataset = null ;
            
//          if ( graphURL != null && request.getBaseURI() != null )
//          graphURL = RelURI.resolve(graphURL, request.getBaseURI()) ;
            
            // Look in cache for loaded graphs!!
            
            if ( graphURL != null && ! graphURL.equals(""))
            {
                if ( dataset == null )
                    dataset = DatasetFactory.create() ;
                
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
                    dataset = DatasetFactory.create() ;
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
            
            return dataset ;
            
        } catch (Exception ex)
        {
            log.info("SPARQL parameter error",ex) ;
            throw new QueryExecutionException(
                                              ExecutionError.rcArgumentError, "Parameter error");
        }
        
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
