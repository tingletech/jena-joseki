/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.processors;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joseki.*;
import org.joseki.module.Loadable;
import org.joseki.util.GraphUtils;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.*;
import com.hp.hpl.jena.util.FileManager;

public class SPARQL extends QueryCom implements Loadable
{
    // TODO Refactor into the stages of a query 
    private static Log log = LogFactory.getLog(SPARQL.class) ;
    
    static final String policyMRSW  = JosekiVocab.lockingPolicyMRSW.getURI();
    static final String policyMutex = JosekiVocab.lockingPolicyMutex.getURI();
    static final String policyNone  = JosekiVocab.lockingPolicyNone.getURI();
    
    static final Property allowDatasetDescP = JosekiVocab.allowExplicitDataset ;
    static final Property allowWebLoadingP  = JosekiVocab.allowWebLoading ;
    
    static private Model m = ModelFactory.createDefaultModel() ;
    
    static final Literal XSD_TRUE   = m.createTypedLiteral(true) ; 
    static final Literal XSD_FALSE  = m.createTypedLiteral(false) ;
    
    public static final String P_QUERY          = "query" ;
    public static final String P_QUERY_REF      = "query-uri" ;
    public static final String P_NAMED_GRAPH    = "named-graph-uri" ;
    public static final String P_DEFAULT_GRAPH  = "default-graph-uri" ;
    
    protected boolean allowDatasetDesc = false ;
    protected boolean allowWebLoading  = false ;
    
    protected int maxTriples = 10000 ;
    protected FileManager fileManager ; 
    
    
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

        if ( ! service.hasProperty(JosekiVocab.lockingPolicy) )
        {
            log.info("Locking policy not declared - default to mutex") ;
            setLock(new LockMutex()) ;
        }

        
        if ( service.hasProperty(JosekiVocab.lockingPolicy) )
        {
            RDFNode policy = service.getProperty(JosekiVocab.lockingPolicy).getObject() ;

            if ( ! policy.isURIResource() )
            {
                log.warn("Locking policy is not a URI") ;
                setLock(new LockMutex()) ;
            }
            else
            {
                String policyURI = ((Resource)policy).getURI() ;
    
                if ( policyURI.equals(policyMRSW) )
                {
                    log.info("Locking policy: multiple reader, single writer") ;
                    setLock(new LockMRSW()) ;
                }
                else if (policyURI.equals(policyMutex) )
                {
                    log.info("Locking policy: single request") ;
                    setLock(new LockMutex()) ;
                }
                else if (policyURI.equals(policyNone) )
                {
                    log.info("Locking policy: none") ;
                    setLock(new LockMutex()) ;
                }
                else
                {
                    log.warn("Unrecognized locking policy: <"+policyURI+">") ;
                    setLock(new LockMutex()) ;
                }
            }
        }
        
        log.info("Dataset description: "+allowDatasetDesc+" // Web loading: "+allowWebLoading) ;
    }
    
    public void execQuery(Request request, Response response, DatasetDesc datasetDesc) throws QueryExecutionException
    {
        execQueryProtected(request, response, datasetDesc, 0) ;
    }
    
    public void execQueryProtected(Request request, Response response, DatasetDesc datasetDesc, int attempts) throws QueryExecutionException
    {
        try {
            execQueryWorker(request, response, datasetDesc) ;
        }
        catch (QueryExecutionException qEx)
        { throw qEx; }
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
        catch (QueryStageException ex)
        {
            if ( attempts == 0 && causeLooksRetryable(ex) )
            {
                attempts ++ ;
                log.warn("Execution failure (retryable) : retry: "+attempts) ;
                datasetDesc.clearDataset() ;
                execQueryProtected(request, response, datasetDesc, attempts) ;
                return ;
            }
            
            log.warn("QueryStageException: "+ex.getMessage(), ex) ;
            QueryExecutionException qExEx = new QueryExecutionException(ReturnCodes.rcInternalError, ex.getMessage()) ;
            throw qExEx ;
        }
        catch (JenaException ex)
        {
            log.info("JenaException: "+ex.getMessage()) ;
            QueryExecutionException qExEx = new QueryExecutionException(ReturnCodes.rcJenaException, ex.getMessage()) ;
            throw qExEx ;
        }
        catch (Throwable ex)
        {   // Attempt to catch anything
            log.info("Throwable: "+ex.getMessage(), ex) ;
            //log.info("Throwable: "+ex.getMessage()) ;
            QueryExecutionException qExEx = new QueryExecutionException(ReturnCodes.rcInternalError, ex.getMessage()) ;
            throw qExEx ;
        }
    }
    
    private boolean causeLooksRetryable(Throwable ex)
    {
        while ( ex != null )
        {
            String name = ex.getClass().getName() ;
            if ( name.equals("com.mysql.jdbc.CommunicationsException") ||
                 name.equals("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException") )
                return true ;
            ex = ex.getCause() ;
        }
        return false ;
    }
    
    
    private void execQueryWorker(Request request, Response response, DatasetDesc datasetDesc) throws QueryExecutionException
    {
        //log.info("Request: "+request.paramsAsString()) ;
        String queryString = null ;
        
        if ( request.containsParam(P_QUERY) )
        {
            queryString = request.getParam(P_QUERY) ;
            if  (queryString == null )
            {
                log.debug("No query argument (but query parameter exists)") ;
                throw new JosekiServerException("Query string is null") ;
            }
        }
        
        if ( request.containsParam(P_QUERY_REF) )
        {
            String queryURI = request.getParam(P_QUERY_REF) ;
            if ( queryURI == null )
            {
                log.debug("No query reference argument (but query parameter exists)") ;
                throw new JosekiServerException("Query reference is null") ;
            }
            queryString = getRemoteString(queryURI) ;
        }
        
        if ( queryString == null )
        {
            log.debug("No query argument") ;
            throw new QueryExecutionException(ReturnCodes.rcBadRequest,
            "No query string");    
        }
        
        
        if ( queryString.equals("") )
        {
            log.debug("Empty query string") ;
            throw new QueryExecutionException(ReturnCodes.rcBadRequest,
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
            String tmp = queryString +"\n\r" + ex.getMessage() ;
            throw new QueryExecutionException(ReturnCodes.rcQueryParseFailure, "Parse error: \n"+tmp) ;
        } catch (Throwable thrown)
        {
            log.info("Query unknown error during parsing: "+queryStringLog, thrown) ;
            throw new QueryExecutionException(ReturnCodes.rcQueryParseFailure, "Unknown Parse error") ;
        }
        
        // Check arguments
        Dataset dataset = null ;
        boolean useQueryDesc = false ;
        if ( ! allowDatasetDesc )
        {
            // Restrict to service dataset only. 
            if ( datasetInProtocol(request) )
                throw new QueryExecutionException(ReturnCodes.rcArgumentError, "This service does not allow the dataset to be specified in the protocol request") ;
            if ( query.hasDatasetDescription() )
                throw new QueryExecutionException(ReturnCodes.rcArgumentError, "This service does not allow the dataset to be specified in the query") ;
        }
        else
        {
            // Protocol
            dataset = datasetFromProtocol(request) ;
            // In query itself.
            if ( dataset == null )
            {
                // No dataset in protocol
                if ( query.hasDatasetDescription() )
                    useQueryDesc = true ;
                // If in query, then the query engine will do the loading.
            }
        }
        // Use the service dataset description if
        // not in query and not in protocol. 
        if ( !useQueryDesc && dataset == null )
        {
            if ( datasetDesc != null )
                dataset = getDataset(datasetDesc) ;
        }

        // How to test for a fixed dataset ... wait until SPARQL blows up!
//        if ( !useQueryDesc && dataset == null )
//            throw new QueryExecutionException(ReturnCodes.rcBadRequest, "No dataset given") ;
        
        if ( useQueryDesc )
            // If using query description, ignore dataset
            dataset = null ;
        
        final QueryExecution qexec = getQueryExecution(query, dataset) ;
        ResponseCallback cb = new ResponseCallback(){

            public void callback()
            { 
                log.debug("ResponseCallback: close execution") ;
                qexec.close(); 
            }} ;
        
        response.addCallback(cb) ;
        executeQuery(query, queryStringLog, qexec, response) ;
        
    }
    
    protected QueryExecution getQueryExecution(Query query, Dataset dataset)
    {
        return QueryExecutionFactory.create(query, dataset) ;
    }
    
    protected Dataset getDataset(DatasetDesc datasetDesc)
    {
        return datasetDesc.getDataset() ;
    }
    
    private void executeQuery(Query query, String queryStringLog, QueryExecution qexec, Response response)
        throws QueryExecutionException
    {
        if ( query.isSelectType() )
        {
            // Force some query execute now.
            // To cope with MySQL comms timeouts.  Mutter, mutter.
            ResultSet rs = qexec.execSelect() ;
            
            // Do this to force the query to do something that should touch any underlying database,
            // and hence ensure the communications layer is working.  MySQL can time out after  
            // 8 hours of an idle connection
            rs.hasNext() ;
            
            // Old way - heavyweight
            //rs = ResultSetFactory.copyResults(rs) ;
            response.setResultSet(rs) ;
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
    
    protected Dataset datasetFromProtocol(Request request) throws QueryExecutionException
    {
        try {
            
            List graphURLs = request.getParams(P_DEFAULT_GRAPH) ;
            List namedGraphs = request.getParams(P_NAMED_GRAPH) ;
            
            graphURLs = removeEmptyValues(graphURLs) ;
            namedGraphs = removeEmptyValues(namedGraphs) ;
            
            if ( graphURLs.size() == 0 && namedGraphs.size() == 0 )
                return null ;
            
            DataSource dataset = null ;
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
            
        } 
        catch (QueryExecutionException ex) { throw ex ; }
        catch (Exception ex)
        {
            log.info("SPARQL parameter error",ex) ;
            throw new QueryExecutionException(
                                              ReturnCodes.rcArgumentError, "Parameter error");
        }
        
    }

    private List removeEmptyValues(List strList)
    {
        for ( Iterator iter = strList.iterator() ; iter.hasNext(); )
        {
            String v = (String)iter.next();
            if ( v.equals("") ) 
                iter.remove() ;
        }
        return strList ; 
    }
    
    /**
     * @param queryURI
     * @return
     */
    private String getRemoteString(String queryURI)
    {
        return FileManager.get().readWholeFileAsUTF8(queryURI) ;
    }
}

/*
 * (c) Copyright 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
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
