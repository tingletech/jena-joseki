/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;

import java.util.* ;
import org.apache.commons.logging.* ;

import org.joseki.vocabulary.*;
import org.joseki.server.module.*;

import com.hp.hpl.jena.rdf.model.* ;
import com.hp.hpl.jena.vocabulary.* ;

/** The main operation engine class.  The dispatcher route requests to
 *  operation processors and keeps the mapping from URI to model.
 * 
 * @author      Andy Seaborne
 * @version     $Id: Dispatcher.java,v 1.1 2004-11-03 10:15:01 andy_seaborne Exp $
 */
public class Dispatcher
{
    private static Log logger = LogFactory.getLog(Dispatcher.class.getName()) ;
    
    ModelSet modelSet = new ModelSet() ;

    Dispatcher parent = null ;
    Loader loader = new Loader() ;
    
    /** Create a Dispatcher */
    public Dispatcher() { }
    
    /** Create a Dispatcher that will call another 
     *  if it can not handle the request
     * @param queryDispatcher Parent dispatcher
     */
    
    public Dispatcher(Dispatcher queryDispatcher) { parent = queryDispatcher ; }

    public ModelSet getModelSet() { return modelSet ; }
    
    public Loader getLoader() { return loader ; }  

    /** Find target model, choose processor and make an operation.
     * The operation will need connection-specific completion for argument transport.
     */
    
    public Request createOperation(String uri, String url, String opName) throws ExecutionException
    {
        return createRequest(uri, url, opName, true) ;
    }
    
    public synchronized Request createRequest(String uri, String url, String opName, boolean modelMustExist) throws ExecutionException
    {
        SourceModel aModel = findModel(uri);
        Processor proc = null ;
        if ( aModel == null )
        {
            if (modelMustExist)
                throw new ExecutionException(ExecutionError.rcNoSuchURI, "Not found: " + uri);
        }
        else
        {   
            proc = findProcessor(aModel, opName) ;
            if ( proc == null )
                  throw new ExecutionException(ExecutionError.rcOperationNotSupported, "Request not found: " + opName);
        }
        Request req = new RequestImpl(uri, url, opName, this, aModel, proc) ;
        return req ;
    }
    

    public Request createQueryRequest(String uri, String url, String langName) throws ExecutionException
    {
        QueryProcessor qProc = null;
        SourceModel aModel = null;
        synchronized (this)
        {
            aModel = findModel(uri);
            if (aModel == null)
                throw new ExecutionException(ExecutionError.rcNoSuchURI, "Not found: " + uri);
            qProc = findQueryProcessor(aModel, langName) ;
        }        

        if ( qProc == null )
        {
            if ( langName == null )
                throw new ExecutionException(ExecutionError.rcQueryExecutionFailure, "Null query language name") ;
            throw new ExecutionException(ExecutionError.rcNoSuchQueryLanguage, "No such query language: "+langName) ;
        }
        Request req = new RequestImpl(uri, url, "query", this, aModel, qProc) ;
        req.setParam("lang", langName) ;
        return req ;
    }

    /** Actually do something!
     * @param request                    Request to perform
     * @throws ExecutionException
     */
    
    public Model exec(Request request) throws ExecutionException
    {
        return request.getProcessor().exec(request) ;
    }
    
    private Processor findProcessor(SourceModel aModel, String opName)
    {
        Processor proc = aModel.getProcessorRegistry().findProcessor(opName);
        return proc ;
    }

    private QueryProcessor findQueryProcessor(SourceModel aModel, String langName)
    {
        QueryProcessor qProc = aModel.getProcessorRegistry().findQueryProcessor(langName);
        return qProc ;
    }

    public SourceModel findModel(String uri)
    {
        SourceModel aModel = modelSet.findModel(uri);
        return aModel ;
    }
    
    // -------- Meta data about this dispatcher
    
    Model optionsModel = null ;
    public Model getOptionsModel(String baseName) throws ExecutionException
    {
        if ( optionsModel == null )
            optionsModel = calcOptionsModel(baseName) ;
        return optionsModel ;
    }
    
    private synchronized Model calcOptionsModel(String baseName) throws ExecutionException
    {
        try {
            Model optModel = ModelFactory.createDefaultModel() ;
            // Just put in all models.
            for ( Iterator iter = modelSet.sourceURIs() ; iter.hasNext() ; )
            {
                String uri = (String)iter.next() ;
                uri = baseName+uri ;
                logger.debug("Server options request: URI = "+uri) ;
                Resource r = optModel.createResource(uri) ;
                optModel.add(r, RDF.type, JosekiVocab.AttachedModel) ;
            }
            return optModel ;
        } catch (RDFException rdfEx)
        {
            logger.warn("RDFException", rdfEx) ;
            throw new ExecutionException(ExecutionError.rcInternalError, "Failed to create options model") ;
        }
        //return null ;
    }
    
    // -------- Meta data about a source
    
    public Model getOptionsModel(SourceModel aModel, String baseName)  throws ExecutionException
    {
        return calcOptionsModel(aModel, baseName) ;
    }
        
    private Model calcOptionsModel(SourceModel aModel, String baseName) throws ExecutionException
    {
         // Nested locking is currently broken.
//        try {
//            aModel.startOperation(true) ;
            try {
                Model optModel = ModelFactory.createDefaultModel() ;
                // We recreate from the internal datastructures,
                // not just read/copy from the original configuration data
                //Model model = modelSet.getConfigurationModel() ;

                // The "URI" is dependent on the request and the 
                // the webapplication mount point. 
                String configURI = ModelSet.serverRootURI+aModel.getServerURI() ;

                // Model as bNode as we do not know its true URI (URL)
                // It has several, depending on how it is addressed!
                
                Resource optionRes = optModel.createResource() ;
                optModel.add(optionRes, RDF.type, JosekiVocab.AttachedModel) ;

                aModel.getProcessorRegistry().toRDF(optModel, optionRes) ;

                // Add prefixes
                Map prefixes = aModel.getPrefixes() ;
                for ( Iterator iter = prefixes.keySet().iterator() ; iter.hasNext() ; )
                {
                    String prefix = (String)iter.next() ;
                    String nsURI = (String)prefixes.get(prefix) ;
                    Resource r = optModel.createResource() ;
                    r.addProperty(JosekiVocab.nsURI, nsURI) ;
                    r.addProperty(JosekiVocab.prefix, prefix) ;
                    optModel.add(optionRes, JosekiVocab.namespacePrefix, r) ;
                }
                
                
                return optModel ;
            } catch (RDFException rdfEx)
            {
                logger.warn("RDFException", rdfEx) ;
                throw new ExecutionException(ExecutionError.rcInternalError, "Failed to create options model for "+aModel.getServerURI()) ;
            }
//        } finally { aModel.endOperation() ; }
    }
    
    
    // -------- Operations to provide programmatic configuration of a dispatcher

    public synchronized void addSourceModel(SourceModel aModel, String uri)
    {
        if ( uri == null )
        {
            logger.warn("No URI supplied for the model source") ;
            return ;
        }
        modelSet.addModel(uri, aModel) ;
    }

    public synchronized void removeSourceModel(String uri)
    {
        if ( uri == null )
        {
            logger.warn("No URI supplied for the model source") ;
            return ;
        }
        modelSet.removeModel(uri) ;
    }

    /** Add an operation processor.
     * @param aModelURI URI for the source the processor is specific to.
     * @param shortName Short name for the operation
     * @param proc The processor
     */
    public synchronized void addProcessor(String aModelURI, String shortName, Processor proc)
    {
        if ( aModelURI == null )
        {
            logger.warn("No URI supplied for the attached model") ;
            return ;
        }

        SourceModel aModel = modelSet.findModel(aModelURI) ;
        if ( aModel != null )
            aModel.getProcessorRegistry().registerProcessor(shortName, proc) ;
    }

    /** Add a query operation processor.
     * @param aModelURI       Model the processor is specific to.
     * @param langName        Language name
     * @param queryProc The query processor
     */
    public synchronized void addQueryProcessor(String aModelURI, String langName, QueryProcessor queryProc)
    {
        if ( aModelURI == null )
        {
            logger.warn("No URI supplied for the attached model") ;
            return ;
        }

        SourceModel aModel = modelSet.findModel(aModelURI) ;
        if ( aModel != null )
            aModel.getProcessorRegistry().registerQueryProcessor(langName, queryProc) ;
    }

    /** Remove a processor.
     * @param aModelURI   The attached model URI that the processor is specific to.
     * @param processor   The processor
     */
    public synchronized void removeProcessor(String aModelURI, Processor processor)
    {
        if ( aModelURI == null )
        {
            logger.warn("No URI supplied for the attached model") ;
            return ;
        }
        
        SourceModel aModel = modelSet.findModel(aModelURI) ;
        if ( aModel != null )
            aModel.getProcessorRegistry().remove(processor) ;
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
