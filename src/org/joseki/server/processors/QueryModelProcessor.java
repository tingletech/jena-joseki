/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;

import org.joseki.server.* ;
import org.joseki.server.module.* ;
import org.joseki.vocabulary.JosekiVocab;

import com.hp.hpl.jena.rdf.model.* ;

/** This class is the indirection for queries that are specified by an RDF model.
 *  Large queries, or ones being forced to go to the model (no caching)
 *  are sent by HTTP POST.  This class takes that request and finds the right
 *  language processor for query.  The language is part of the argument
 *  model as property "joseki:queryOperationName".
 * 
 * @author      Andy Seaborne
 * @version     $Id: QueryModelProcessor.java,v 1.3 2004-11-04 15:44:52 andy_seaborne Exp $
 */
public class QueryModelProcessor implements ProcessorModel, Loadable
{
    public QueryModelProcessor()
    {
    }
    
    /** @see org.joseki.server.module#init(Resource, Resource)
      */
     public void init(Resource processor, Resource binding) { }
     
    /**
     * @see org.joseki.server.ProcessorModel#exec(Request)
     */
    public Model exec(Request request) throws ExecutionException
    {
        SourceModel aModel = request.getSourceModel();
        QueryProcessor qProc = null ;
        try
        {
            aModel.startOperation(true);
            Model queryModel = (Model) request.getDataArgs().get(0);

            try
            {
                // Bug fix: old (up to 2.0) wrongly uses joseki:queryOperationName 
                NodeIterator nIter =
                    queryModel.listObjectsOfProperty(JosekiVocab.requestQueryLanguage) ;
                
                // Not found : try again with old name.
                if ( nIter == null || ! nIter.hasNext() ) 
                        nIter = queryModel.listObjectsOfProperty(JosekiVocab.queryOperationName) ;

                for (; nIter.hasNext();)
                {
                    RDFNode n = nIter.nextNode();
                    if (n instanceof Literal)
                    {
                        String queryLangName = ((Literal) n).getString();
                        qProc = aModel.getProcessorRegistry().findQueryProcessor(queryLangName);
                    }
                }
            }
            catch (RDFException ex)
            {
                aModel.abortOperation();
                throw new ExecutionException(ExecutionError.rcArgumentUnreadable,
                                             "Failed to extract query language name") ;
            }
            // Re-execute 
            if ( qProc == null )
                throw new ExecutionException(ExecutionError.rcOperationNotSupported,
                                             "No query language found") ;
            return qProc.exec(request) ;
        } finally { aModel.endOperation() ; }
        
        //return null;
    }

    /**
     * @see org.joseki.server.ProcessorModel#getInterfaceURI()
     */
    public String getInterfaceURI()
    {
        return JosekiVocab.opQueryModel ;
    }

    /** A model is required.
     * @see org.joseki.server.ProcessorModel#argsNeeded()
     */
    public int argsNeeded()
    {
        return ProcessorModel.ARGS_ONE ;
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
