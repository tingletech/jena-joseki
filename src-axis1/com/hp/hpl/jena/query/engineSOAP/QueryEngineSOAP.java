/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.query.engineSOAP;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joseki.soap.GraphDeserializerFactory;
import org.joseki.soap.ResultSetAxis;
import org.joseki.ws1.JosekiQueryServiceLocator;
import org.joseki.ws1.SparqlQuery;
import org.w3.www._2005._09.sparql_protocol_types.* ;

// Clash with the types class of the same name -- import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;

public class QueryEngineSOAP implements QueryExecution
{
    private static Log log = LogFactory.getLog(QueryEngineSOAP.class) ; 
    String endpoint = null ;
    Query query = null ;
    String queryString = null ;
    SparqlQuery soapQuery = null ;
    
    /** Create a execution of a query at a given endpoint
     * @param query
     * @param endpoint
     */
    
    public QueryEngineSOAP(String endpoint, Query query)
    {
        this(query.serialize(), endpoint) ;
    }
    
    /** Create a execution of a query string at a given endpoint.
     *  Does not check locally for syntax errors - better to use 
     *  QueryEngineSOAP(Query, String)
     * 
     * @param queryString
     * @param endpoint
     */
    
    public QueryEngineSOAP(String endpoint, String queryString)
    {
        this.endpoint = endpoint ;
        this.query = null ;
        this.queryString = queryString ;
        init() ;
    }
    
    private void init()
    {
        JosekiQueryServiceLocator  service = new JosekiQueryServiceLocator();
        service.setSparqlQueryEndpointAddress(endpoint) ;
        
        TypeMappingRegistry reg = service.getEngine().getTypeMappingRegistry() ;
        TypeMapping tm = (TypeMapping)reg.getTypeMapping("") ;
        tm.register(Model.class,
                    new QName(RDF.getURI(), "RDF") ,
                    null, 
                    new GraphDeserializerFactory()) ;
        
        
        
        try {
            soapQuery = service.getSparqlQuery() ;
            // Change XML printing mode.
            
        } catch (javax.xml.rpc.ServiceException ex)
        { throw new QueryExceptionSOAP(ex.getMessage()) ; }
    }
    
    private QueryResult exec()
    {
        QueryRequest q = new QueryRequest() ;
        q.setQuery(queryString) ;
        
        try {
            return soapQuery.query(q) ;
        } catch (MalformedQuery ex)
        {
            log.debug("MalformedQuery: "+ex.getFaultDetails1()) ;
            throw new QueryExceptionSOAP(ex.getFaultDetails1(), ex) ;
        } catch (QueryRequestRefused ex)
        {
            log.debug("QueryRequestRefused: "+ex.getFaultDetails1()) ;
            throw new QueryExceptionSOAP(ex.getFaultDetails1(), ex) ;
        } catch (AxisFault axisFault)
        {
            log.debug("Axis Fault: "+axisFault.getFaultCode()+ ""+ axisFault.getFaultString()) ;
            throw new QueryExceptionSOAP("Axis Fault: "+axisFault.getFaultString(), axisFault); 
        } catch (RemoteException e)
        {
            log.debug("Remote Exception: "+e.getMessage(), e) ;
            throw new QueryExceptionSOAP("Remote Exception: "+e.getMessage(), e) ; 
        }
    }

    public void setFileManager(FileManager fm)
    {
        throw new UnsupportedOperationException("QueryEngineSOAP.setFileManager") ;
    }

    public void setInitialBinding(QuerySolution binding)
    {
        throw new UnsupportedOperationException("QueryEngineSOAP.setInitialBinding") ;
    }

    public ResultSet execSelect()
    {
        QueryResult result = exec() ;
        if ( result.getSparql() == null )
            throw new QueryExceptionSOAP("Not a SELECT query: "+queryString) ;
        
        return new ResultSetAxis(result.getSparql()) ;
    }

    public Model execConstruct(Model model)
    { 
        return model.add(execConstruct()) ;
    }
    
    public Model execConstruct()
    {
        QueryResult result = exec() ;
        if ( result.getRDF() == null )
            throw new QueryExceptionSOAP("Not a CONSTRUCT query: "+queryString) ;
            
        return (Model)result.getRDF() ;
    }

    public Model execDescribe(Model model)
    {
        return model.add(execDescribe()) ; 
    }

    public Model execDescribe()
    {
        QueryResult result = exec() ;
        if ( result.getRDF() == null )
            throw new QueryExceptionSOAP("Not a DESCRIBE query: "+queryString) ;
            
        return (Model)result.getRDF() ;
    }

    public boolean execAsk()
    {
        QueryResult result = exec() ;
        if ( result.getSparql() == null )
            throw new QueryExceptionSOAP("Not an ASK query: "+queryString) ;
        return result.getSparql().get_boolean().booleanValue() ;
    }

    //@Override
    public Context getContext() { return null ; }

    public void abort()
    { }

    public void close()
    { }

    public Dataset getDataset()
    {
        return null ;
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