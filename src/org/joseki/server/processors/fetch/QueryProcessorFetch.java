/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors.fetch;

import org.joseki.server.*;
import org.apache.commons.logging.* ;
import java.util.* ;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.* ;
import com.hp.hpl.jena.vocabulary.* ;

import org.joseki.server.processors.QueryProcessorModelCom;
//import org.joseki.server.processors.fetch.* ;
import org.joseki.vocabulary.*;
import org.joseki.server.module.*;
import org.joseki.util.PrintUtils;

/** Query processor that returns the data object referenced.
 *  This subsystem finds a node in the graph (by directly being given its URI)
 *  and uses some server side rules that calculate the RDF model to return.
 *  For example, the rule may be based on the type of the object and/or on a bNode
 *  closure over the properties with this node as subject.
 * 
 * @author      Andy Seaborne
 * @version     $Id: QueryProcessorFetch.java,v 1.1 2004-11-17 14:47:36 andy_seaborne Exp $
 */
public class QueryProcessorFetch extends QueryProcessorModelCom
{
    Log log = LogFactory.getLog(QueryProcessorFetch.class.getName() ) ; 
    
    public QueryProcessorFetch()
    {   
        super() ;
    }

    // This is the type of object this 
    Resource rdfType = null ;
    Set fetchHandlers = new HashSet() ;

    public void init(Resource processor, Resource implmentation)
    {
        try {
            // Pull in further modules per-attached model fetches possible.
            StmtIterator sIter = processor.listProperties(JosekiModule.module) ;
            for ( ; sIter.hasNext() ; )
            {
                Statement stmt = sIter.nextStatement() ;
                Resource fetchBinding = stmt.getResource() ;
                Loader loader = new Loader() ;
                FetchHandler handler = (FetchHandler)loader.loadAndInstantiate(fetchBinding, FetchHandler.class) ;
                log.info("    Fetch: "+handler.getClass().getName()) ;
                
                // Sanity check for fetch handlers of the same name.
                String n1 = handler.getClass().getName() ;
                for ( Iterator iter = fetchHandlers.iterator() ; iter.hasNext() ; )
                {
                    FetchHandler handler2 = (FetchHandler)iter.next() ;
                    String n2 = handler2.getClass().getName() ;
                    if ( n1.equals(n2) )
                        log.warn("      Two handlersa of teh same class name: "+n1) ;
                }
                // End sanity.
                fetchHandlers.add(handler) ;
            }
        
        } catch (JenaException ex)
        {
            log.warn("Exception loading fetch handlers: "+ex) ;
            return ;
        }
        
        if ( implmentation.hasProperty(RDFS.label))
            log.info("rdfs:label(binding): "+implmentation.getProperty(RDFS.label).getString()) ;
        if ( processor.hasProperty(RDFS.label))
            log.info("rdfs:label(processor): "+processor.getProperty(RDFS.label).getString()) ;
    }

    public String getInterfaceURI() { return JosekiVocab.queryOperationFetch ; }

    public Model execQuery(SourceModel src, String queryString, Request request)
        throws RDFException, QueryExecutionException
    {
        if (!(src instanceof SourceModelJena))
            throw new QueryExecutionException(
                ExecutionError.rcOperationNotSupported,
                "Wrong implementation - this Fetch processor works with Jena models");         
        Model model = ((SourceModelJena)src).getModel() ;
        
        Model resultModel = ModelFactory.createDefaultModel() ;
        resultModel.setNsPrefixes(model) ;
        Map m = src.getPrefixes() ;
        if ( m!= null )
            resultModel.setNsPrefixes(src.getPrefixes()) ;
        
        String uri = request.getParam("r") ;

        if (uri != null )
        {    
            Resource r = model.createResource(uri) ;
            log.debug("Fetch: "+uri) ;
            doOneResource(r, resultModel) ;
            return resultModel ;
        }

        //Not the "r=" form
        
        // Is this the predicat/value form?
        String pURI = request.getParam("p") ;
        if ( pURI == null )
            throw new QueryExecutionException(
                    ExecutionError.rcArgumentError,
                    "Fetch must have either resource URI parameter or identifying predicate");
        Property p = model.createProperty(pURI) ;
        RDFNode o = null ;
        String objURI = request.getParam("o") ;
        if ( objURI == null )
        {
            String objVal = request.getParam("v") ;
            o = model.createLiteral(objVal) ;    
            log.debug("Fetch: p="+pURI+"  v="+o) ;
        }
        else
        {    
            log.debug("Fetch: p="+pURI+"  o="+objURI) ;
            o = model.createResource(objURI) ;
        }
        
        StmtIterator sIter = model.listStatements(null, p, o) ;
        if ( ! sIter.hasNext() )
            return resultModel;
        
        for ( ; sIter.hasNext() ; )
        {
            Resource r = sIter.nextStatement().getSubject() ;
            doOneResource(r, resultModel) ;
        }
        sIter.close() ;
        return resultModel ;
    }


    private void doOneResource(Resource resource, Model resultModel)
    {
        log.debug("Fetch: "+PrintUtils.fmt(resource)) ;
        // Types are so significant, we get them all once and
        // pass them to the fetch handlers.
        Set types = new HashSet() ;
        StmtIterator sIter = resource.listProperties(RDF.type) ;
        for ( ; sIter.hasNext() ;)
        {
            // Find handler.
            Resource type = sIter.nextStatement().getResource() ;
            types.add(type) ;
        }
        
        for ( Iterator iter = fetchHandlers.iterator() ; iter.hasNext() ; )
        {
            FetchHandler fetch = (FetchHandler)iter.next() ;
            if ( fetch.handles(resource, types) )
                fetch.fetch(resource, types, resultModel) ; 
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
