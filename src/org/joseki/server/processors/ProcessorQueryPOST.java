/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;

import org.apache.commons.logging.*;
import org.joseki.server.* ;
import org.joseki.vocabulary.JosekiVocab;
import org.joseki.HttpParams ;

import com.hp.hpl.jena.rdf.model.* ;

/** This class translates incoming requests into calls on the appropriate
 *  query processor.  Requests can can be specified by argument in the
 *  query string or specified by an RDF model.
 *  
 *  Large queries, or ones being forced to go to the model (e.g. no caching)
 *  are sent by HTTP POST.  This class takes that request and finds the right
 *  language processor for query.  The language is part of the argument
 *  model as property "joseki:queryOperationName".
 * 
 * @author      Andy Seaborne
 * @version     $Id: ProcessorQueryPOST.java,v 1.4 2005-01-11 10:52:01 andy_seaborne Exp $
 */

public class ProcessorQueryPOST extends ProcessorCom
{
    Log log = LogFactory.getLog(ProcessorQueryPOST.class) ;
    
    public ProcessorQueryPOST()
    {
        super("QueryProcessorPOST", LockType.ReadOperation) ;
    }
    
    /** @see org.joseki.server.module#init(Resource, Resource)
      */
    public void init(Resource processor, Resource binding) { }
     
    public void execute(Request request, Response response)
        throws ExecutionException
    {
        // Inside a lock from ProcessorCom by now.
        SourceModel aModel = request.getSourceModel() ;
        QueryProcessor qProc = null ;
        
        Model queryModel = null ;
        if ( request.getDataArgs().size() > 0 )
          queryModel = (Model)request.getDataArgs().get(0);
  
        if ( queryModel != null )
        {
            try
            {
                String queryLangName = getPropertyValue(queryModel, JosekiVocab.requestQueryLanguage) ;
                //if ( queryLangName == null )
                //    queryLangName = getPropertyValue(queryModel, JosekiVocab.queryOperationName) ;
                
                if ( queryLangName == null )
                {
                    log.warn("No query language name or URI found") ;
                    throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure,
                    "No query language name or URI found");
                }
                
                qProc = aModel.getProcessorRegistry().findQueryProcessor(queryLangName);
            } catch (RDFException ex)
            {
                log.warn("Problems processing request", ex) ;
                throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure,
                                                  "Couldn't get query language name: "+ex.getMessage()); 
            }
        }
        
        if (qProc == null )
            throw new QueryExecutionException(ExecutionError.rcOperationNotSupported,
            "No query language found") ;
        
        // Get query string.
        // If null (no string supplied) or empty, the query is in the model.
        String queryString = request.getParam(HttpParams.pQuery);
        
        if ( queryString != null && queryModel != null )
            throw new QueryExecutionException(
                                              ExecutionError.rcQueryExecutionFailure,
            "Query has string and model arguments");
        
        if ( queryString == null && queryModel != null )
        {
            queryString = getPropertyValue(queryModel, JosekiVocab.queryScript) ;
            if ( queryString.equals("") )
                queryString = null ;
        }

        qProc.execQuery(aModel, queryString, request, response) ;
    }
    

    /**
     * @see org.joseki.server.Interface#getInterfaceURI()
     */
    public String getInterfaceURI()
    {
        return JosekiVocab.opQueryPOST ;
    }

    private String getPropertyValue(Model model, Property property)
    {
        NodeIterator nIter = model.listObjectsOfProperty(property) ;
        for ( ; nIter.hasNext() ; )
        {
            RDFNode n = nIter.nextNode() ;
            if ( n instanceof Literal )
            {
                String s = ((Literal)n).getString() ;
                return s ;
            }
        }
        return null ;
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
