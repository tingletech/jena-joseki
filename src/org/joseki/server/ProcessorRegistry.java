/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;

import java.util.* ;

import org.joseki.vocabulary.*;

import com.hp.hpl.jena.rdf.model.* ;

/** Registry for operation processors.
 * 
 * @author      Andy Seaborne
 * @version     $Id: ProcessorRegistry.java,v 1.4 2004-11-15 15:27:51 andy_seaborne Exp $
 */

public class ProcessorRegistry
{
    QueryOperationRegistry queryRegistry = new QueryOperationRegistry() ;
    OperationRegistry operationRegistry = new OperationRegistry() ;
    
    Set processorURIs = new HashSet() ;
    Set queryLanguageURIs = new HashSet() ;
    
    public boolean hasProcessor(String opName)
    {
        return findProcessor( opName) != null ;
    }
    
    public boolean hasQueryProcessor(String queryLanguageName)
    {
        return findQueryProcessor(queryLanguageName) != null ;
    }
    
    
    public ProcessorModel findProcessor(String opName)
    {
        return operationRegistry.findProcessor(opName) ;
    }


    public QueryProcessorModel findQueryProcessor(String queryLanguageName)
    {
        return queryRegistry.findProcessor(queryLanguageName) ;
    }


    public void registerProcessor(String shortName, ProcessorModel processor)
    {
        operationRegistry.register(shortName, processor) ;
        processorURIs.add(processor.getInterfaceURI()) ;
    }
    
    public void registerQueryProcessor(String langName, QueryProcessorModel queryProcessor)
    {
        queryRegistry.register(langName, queryProcessor) ;
        queryLanguageURIs.add(queryProcessor.getInterfaceURI()) ;
    }
    
    public void remove(ProcessorModel processor)
    {
        operationRegistry.remove(processor) ;
    }

    public void remove(QueryProcessorModel queryProcessor)
    {
        queryRegistry.remove(queryProcessor) ;
    }
    
//    public Set getProcessorURIs() { return processorURIs ; }
//    public Set getQueryLanguageURIs() { return queryLanguageURIs ; }

    // Encode in RDF for OPTIONS
    

    public void toRDF(Model optModel, Resource optionRes) throws RDFException
    {
        Map processors = operationRegistry.procRegistry ;
        for ( Iterator iter = processors.keySet().iterator() ; iter.hasNext() ; )
        {
            String name = (String)iter.next() ;
            ProcessorModel proc = (ProcessorModel)processors.get(name) ;
            Resource r = optModel.createResource() ; 
            r.addProperty(JosekiVocab.operationName, name) ;
            Resource opRes = optModel.createResource(proc.getInterfaceURI()) ;
            r.addProperty(JosekiVocab.operation, opRes) ;
            optModel.add(optionRes, JosekiVocab.hasOperation, r) ;
        }

        // Add query languages
        Map queryLanguages = queryRegistry.procRegistry ;
        for ( Iterator iter = queryLanguages.keySet().iterator() ; iter.hasNext() ; )
        {
            String qlName = (String)iter.next() ;
            QueryProcessorModel qproc = (QueryProcessorModel)queryLanguages.get(qlName) ;
            Resource r = optModel.createResource() ;
            r.addProperty(JosekiVocab.queryOperationName, qlName) ;
            Resource qRes = optModel.createResource(qproc.getInterfaceURI()) ;
            r.addProperty(JosekiVocab.queryOperation, qRes) ;
            optModel.add(optionRes, JosekiVocab.hasQueryOperation, r) ;
        }

    }

    // ----------------------------------------------------
    // The registry storage classes
    // Wouldn't paramterized types be nice ?

    static class OperationRegistry
    {
        // Map from short name to processor.
        Map procRegistry = new HashMap() ;

        void register(String shortName, ProcessorModel processor)
        {
            procRegistry.put(shortName, processor) ;
        }
    
        synchronized ProcessorModel findProcessor(String opName)
        {
            ProcessorModel proc = (ProcessorModel)procRegistry.get(opName) ;
            return proc ;
        }


    
        void remove(ProcessorModel processor)
        {
            for ( Iterator iter = procRegistry.entrySet().iterator() ; iter.hasNext() ; )
            {
                Map.Entry entry = (Map.Entry)iter.next() ;
                ProcessorModel proc = (ProcessorModel)entry.getValue() ;
    
                if ( processor.equals(proc) )
                    iter.remove() ;
            }
        } 
    }

    static class QueryOperationRegistry
    {
        // Map from short name to processor.
        Map procRegistry = new HashMap() ;

        void register(String languageName, QueryProcessorModel queryPprocessor)
        {
            procRegistry.put(languageName, queryPprocessor) ;
        }
    
        synchronized QueryProcessorModel findProcessor(String languageName)
        {
            QueryProcessorModel proc = (QueryProcessorModel)procRegistry.get(languageName) ;
            return proc ;
        }


    
        void remove(QueryProcessorModel queryProcessor)
        {
            for ( Iterator iter = procRegistry.entrySet().iterator() ; iter.hasNext() ; )
            {
                Map.Entry entry = (Map.Entry)iter.next() ;
                QueryProcessorModel qProc = (QueryProcessorModel)entry.getValue() ;
    
                if ( queryProcessor.equals(qProc) )
                    iter.remove() ;
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
