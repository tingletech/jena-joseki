/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;

import java.util.* ;
import org.apache.commons.logging.* ;

import org.joseki.* ;
import org.joseki.util.* ;
import org.joseki.server.source.* ;
import org.joseki.vocabulary.*;
import org.joseki.server.module.*;

import com.hp.hpl.jena.rdf.model.* ;

import com.hp.hpl.jena.ontology.* ;
import com.hp.hpl.jena.shared.* ;
import com.hp.hpl.jena.vocabulary.* ;
import com.hp.hpl.jena.reasoner.* ;
import com.hp.hpl.jena.util.* ;
//import com.hp.hpl.jena.reasoner.rulesys.*;


/**
 * Parse and process a Joseki configuration file
 * 
 * @author  Andy Seaborne
 * @version $Id: Configuration.java,v 1.7 2005-01-03 20:26:34 andy_seaborne Exp $
 */


public class Configuration
{
    private static Log log = LogFactory.getLog(Configuration.class.getName());
    
    // List of old namespace URIs  
    public static final String oldNS[] = {"http://joseki.org/2002/06/configuration#"} ;
    
    Model configurationModel = ModelFactory.createDefaultModel() ;
    ClassLoader classLoader = null ;
    Loader moduleLoader = new Loader() ;
    FileManager fileManager = FileManager.get() ; 
    
    /** Create an empty ModelSet */
    public Configuration()
    {
        classLoader = chooseClassLoader() ;
    }
 
    public Model getConfigurationModel() { return configurationModel ; }
    
    /** Load a file
     */
    public void load(ModelSet modelSet, String configFile) 
    {
        log.info("Load configuration: "+configFile) ;          
        
        Set filesRead = new HashSet() ;
        
        readConfFile(configurationModel, configFile, filesRead) ;
        
        processConfigurationModel(modelSet) ;
    }

    private void readConfFile(Model confModel, String fileName, Set filesDone)
    {
        try
        {
            // Read into this empty model to find all the "alsoIncludes"
            // Not necessary (could do in the main model) but convenient
            // as it bounds any read errors.
            
            Model m = ModelFactory.createDefaultModel() ;
            m = fileManager.readModel(m, fileName) ;
            if ( m == null )
            {    
                log.warn("Configuration error: not found or not valid:: "+fileName) ;
                throw new ConfigurationErrorException("File not found or not valid: "+fileName) ;
                //return false ;
            }
            
            log.info("Read: "+fileName) ;
            filesDone.add(fileName) ;
        
            Set filesToDo = new HashSet() ;
            
            ResIterator rIter = m.listSubjectsWithProperty(RDF.type, JosekiVocab.JosekiServer) ;
            for ( ; rIter.hasNext() ; )
            {
                Resource r = rIter.nextResource() ; 
                StmtIterator sIter = r.listProperties(JosekiVocab.alsoInclude) ;
                for ( ; sIter.hasNext() ; )
                {
                    Resource fileName2 = sIter.nextStatement().getResource() ;
                    if ( fileName2.isAnon() )
                        continue ;
                    String fullname = fileName2.getURI() ;
                    if ( ! filesDone.contains(fullname) )
                        filesToDo.add(fullname) ;
                }
                sIter.close() ;
            }
            rIter.close() ;
            confModel.add(m) ;
            for ( Iterator iter = filesToDo.iterator() ; iter.hasNext() ; )
            {
                readConfFile(confModel, (String)iter.next(), filesDone) ;
            }
            
        } catch (RDFException rdfEx)
        {
            log.warn(fileName+": RDF Exception: "+rdfEx.getMessage()) ;
            throw new ConfigurationErrorException("RDF Exception: "+rdfEx.getMessage(), rdfEx) ;
            //return false ;
        }
    }

    // ------------------------------------------------
    // Main function to process the configuration model
        
    private void processConfigurationModel(ModelSet modelSet)
    {
        // -------- Checks
        
        NsIterator nsIter = configurationModel.listNameSpaces() ;
        for ( ; nsIter.hasNext() ;)
        {
            String ns = nsIter.nextNs() ;
            for ( int i = 0 ; i < oldNS.length ; i++)
            {
                if ( oldNS[i].equals(ns) )
                {
                    log.error("Found out of date configuration information found") ;
                    log.error("  Configuration URI for this codebase is "+JosekiVocab.NS) ;
                    throw new ConfigurationErrorException("Out of date configuration information") ;
                }
            }
        }    
        // -------- Do server information       
        processServerConfiguration() ;

        processSources(modelSet) ;
    }


    private void processServerConfiguration()
    {
        try
        {
            log.trace("Start server configuration") ;
            // Find the server: merge multiple nodes.
            ResIterator rIter = configurationModel.listSubjectsWithProperty(RDF.type, JosekiVocab.JosekiServer) ;
            for ( ; rIter.hasNext() ; )
            {
                Resource r = rIter.nextResource() ;
                
                // Various controls
                
                if ( r.hasProperty(JosekiVocab.serverHttpExplicitNoCache) )
                {
                    Joseki.serverHttpExplicitNoCache = 
                        r.getRequiredProperty(JosekiVocab.serverHttpExplicitNoCache).getString().equals("true") ;
                }
                
                if ( r.hasProperty(JosekiVocab.useContentType) )
                {
                    String contentType = r.getRequiredProperty(JosekiVocab.useContentType).getString() ;

                    if ( contentType.equals(Joseki.contentTypeAppN3) ||
                         contentType.equals(Joseki.contentTypeTextN3) ||
                         contentType.equals(Joseki.contentTypeRDFXML) ||
                         contentType.equals(Joseki.contentTypeNTriples) )
                    {
                        log.debug("Content type: "+contentType) ;
                        Joseki.serverContentType = contentType ;
                    }
                    else
                    {
                        log.warn("Unknown content-type: "+contentType) ;
                    }
                }

                Property oldFeature = r.getModel().createProperty(JosekiVocab.NS, "loadJavaClass") ; 
                if ( r.hasProperty(oldFeature) )
                    log.warn("Old style 'joseki:javaLoadClass' found : use joseki:dbDriver on attached model") ;
            }
            rIter.close();
            
        } catch (RDFException rdfEx)
        {
            log.warn("Error in configuration file (server section)", rdfEx) ;
            throw new ConfigurationErrorException("Error in configuration file (server section)", rdfEx) ;
        }
        
        if ( Joseki.serverHttpExplicitNoCache )
            log.debug("Server explicit 'no cache' set") ;              

        log.trace("Finish server configuration") ;
    }


    private void processSources(ModelSet modelSet)
    {
        String loggingIndicator = "<<unset>>" ; 
        try
        {
            // Find all models.  Find all things with an SourceModel property.
            ResIterator rIter =
                configurationModel.listSubjectsWithProperty(JosekiVocab.attachedModel);
            if ( ! rIter.hasNext() )
                log.warn("No models to attach") ;

            for (; rIter.hasNext();)
            {
                Resource modelSource = rIter.nextResource();
                if ( modelSource.isAnon() )
                {    
                    log.warn("Attached model is a bNode - need a URI") ;
                    continue ;
                }
                String serverURI = ModelSet.resource2serverURI(modelSource) ;

                loggingIndicator = serverURI ;
                SourceModel srcModel = null ;
                try {
                    srcModel = processModel(modelSource, serverURI) ;
                } catch (JenaException ex)
                {
                    log.warn("Error in configuration file.  Model:"+loggingIndicator) ;
                    ex.printStackTrace(System.out) ;
                    continue ;
                }
                if ( srcModel == null )
                    continue ;
                modelSet.addModel(serverURI, srcModel) ;
            }
            rIter.close() ;
        }
        catch (RDFException rdfEx)
        {
            log.warn("Error in configuration file (model section): "+loggingIndicator) ;
            throw new ConfigurationErrorException("Error in configuration file (model section): "+loggingIndicator) ;
        }
    }
    
    
    private SourceModel processModel(Resource modelSource, String serverURI)
    {
        if ( modelSource.isAnon() )
        {
            log.warn("Anonymous node for external resource!") ;
            return null ;
        }       
        
        Statement sourceStmt = modelSource.getProperty(JosekiVocab.attachedModel) ;
        if ( sourceStmt == null )
        {
            log.warn("Model: "+serverURI + " : No internal Jena model specified") ;
            return null ;
        } 
        
        String internalURI = null ;
        try { 
            internalURI = sourceStmt.getResource().getURI() ;
            log.info("Model: "+serverURI + " ==> " + internalURI) ;
        } catch (RDFException ex)
        {
            log.warn("No internal resource URI for "+serverURI) ;
            return null ;
        }
        
        SourceModel src = buildSourceModel(modelSource, internalURI, serverURI) ;

        if ( src == null )
        {
            log.warn("Failed to attach "+internalURI) ;
            return null ;
        }
        
        log.trace("Base model built") ;
        
        if ( modelSource.hasProperty(JosekiVocab.modelSpec) )
        {
            log.warn("Description by ModelSpec: Not implemented (yet)") ;
            return null ;
        }
        else
        {
            if ( modelSource.hasProperty(JosekiVocab.vocabulary) ||
                 modelSource.hasProperty(JosekiVocab.ontology) )
            {
                src = buildOntSourceModel(modelSource, serverURI, src) ;
                if ( src == null )
                    // Error in OWL/RDFS model set up.
                    return null ;
            }
        }
        
        if ( modelSource.hasProperty(JosekiVocab.isImmutable) )
        {
            String tmp = modelSource.getProperty(JosekiVocab.isImmutable).getString() ;
            if ( tmp.equalsIgnoreCase("true") )
            {
                log.debug("  Immutable model") ;
                src.setIsImmutable(true) ;
            }
        }
        
        // Add functionalities
        buildQueryProcs(modelSource, src) ;
        buildOtherProcs(modelSource, src) ;
        
        assignNamespaces(modelSource, src) ;
        return src ;
    }

    
    
    // Process any RDFS or OWL ontologies associated with the model. 
    private SourceModel buildOntSourceModel(Resource modelSource, String serverURI, SourceModel src)
    {
        final boolean hasRDFS = modelSource.hasProperty(JosekiVocab.vocabulary) ;
        final boolean hasOWL = modelSource.hasProperty(JosekiVocab.ontology) ;
        
        if ( ! hasRDFS && ! hasOWL )
            return src ;

        if ( hasRDFS && hasOWL )
        {
            log.warn("Can't have both an OWL ontology and an RDFS vocabulary") ;
            return null ;
        }
        
        if ( ! (src instanceof SourceModelJena) )
        {
            log.warn("Jena inference subsystem only works with Jena models") ;
            return null ;
        }
        
        Model aBox = ((SourceModelJena)src).getModel() ;
        Model newModel = null ;

        if ( hasRDFS )
        {
            Resource vocabulary = modelSource
                                    .getProperty(JosekiVocab.vocabulary)
                                    .getResource() ;
            log.info("  Applying RDFS: "+vocabulary) ;
            
            Model vocabularyModel = fileManager.loadModel(vocabulary.getURI()) ;
            newModel = ModelFactory.createRDFSModel(vocabularyModel, aBox) ;
        }

        if ( hasOWL )
        {
            Resource ontology = modelSource
                                    .getProperty(JosekiVocab.ontology)
                                    .getResource() ;
            Model tBox = fileManager.loadModel(ontology.getURI()) ;
            log.info("  Applying OWL: "+ontology) ;
            
            Model ontDocMgr = null ;
            if ( modelSource.hasProperty(JosekiVocab.ontDocumentManager))
            {
                Resource r = modelSource.getProperty(JosekiVocab.ontDocumentManager).getResource() ;
                String ontDocMgrURI = r.getURI() ;
                log.info("  Ontology document manager: "+ontDocMgrURI) ;
                ontDocMgr = fileManager.loadModel(ontDocMgrURI) ;
            }

            Reasoner reasoner = ReasonerRegistry.getOWLReasoner().bindSchema(tBox) ;
            OntModelSpec s = new OntModelSpec( OntModelSpec.OWL_MEM);
            if ( ontDocMgr != null )
                s.setDocumentManager( new OntDocumentManager(ontDocMgr)) ;
            s.setReasoner(reasoner) ;
            newModel = ModelFactory.createOntologyModel( s, aBox );
        }
        
        newModel.setNsPrefixes(aBox) ;
        // Create a permanent SourceModel.  No (system) resource control.
        SourceController sCtl = new SourceControllerPermanent(newModel) ;
        src = sCtl.createSourceModel(null, serverURI) ;
        return src ;
    }

    private SourceModel buildSourceModel(Resource sourceDesc, String modelURI, String serverURI)
    {
        Resource module = null ;
        
        if ( sourceDesc.hasProperty(JosekiVocab.sourceController) )
        {    
            module = sourceDesc.getProperty(JosekiVocab.sourceController).getResource() ;
            log.debug("  Custom source controller: "+PrintUtils.fmt(module)) ;
        }
        else
        {
            // Not given : try for a built-in
             
            if ( modelURI.toLowerCase().startsWith("file:") )
            {
                log.trace("  File source") ;
                module = sourceDesc.getModel().getResource(JosekiVocab.sourceControllerFile) ;
            }
                
            if ( modelURI.toLowerCase().startsWith("jdbc:") )
            {    
                log.trace("  JDBC source") ;
                module = sourceDesc.getModel().getResource(JosekiVocab.sourceControllerRDB) ;
            }
        }
                
        if ( module == null )
        {
            log.warn("Can't find module for source controller for "+serverURI+" : not named, not a file and not a database") ;
            return null ;
        }

        SourceController srcCtl = (SourceController)moduleLoader.loadAndInstantiate(module, SourceController.class) ;

        if ( srcCtl == null )
        {
            log.warn("Can't load find source controller for "+serverURI) ;
            return null ;
        }
        
        SourceModel a = srcCtl.createSourceModel(sourceDesc, serverURI) ;
        log.trace("Built model source controller") ;
        return a ;
    }

    // Do query processors
    private void buildQueryProcs(Resource modelSource, SourceModel src)
    {
        StmtIterator queryBindings = modelSource.listProperties(JosekiVocab.hasQueryOperation);
        for (; queryBindings.hasNext();)
        {
            Resource binding = queryBindings.nextStatement().getResource();
            QueryProcessor qProc = (QueryProcessor)moduleLoader.loadAndInstantiate(binding, QueryProcessor.class) ; 
            
            if ( qProc == null )
                continue ;
                
            // Several names for the same query processor
            // Can be a URI or a string.
            StmtIterator langNameIter = binding.listProperties(JosekiVocab.queryOperationName);
            for (; langNameIter.hasNext();)
            {
                Statement s = langNameIter.nextStatement();
                String langName = null ;
                if ( s.getObject() instanceof Literal )
                    langName = s.getString();
                else
                {
                    if ( s.getResource().isAnon() )
                    {
                        log.warn("Query langauge name must be a literal or a URI") ;
                        continue ;
                    } 
                    langName = s.getResource().getURI() ;
                }
                log.info("  Query Language: " + langName);
                if ( src.getProcessorRegistry().hasQueryProcessor(langName) )
                    log.warn("  Query language '"+langName+"' already assigned") ;
                src.getProcessorRegistry().registerQueryProcessor(langName, qProc);
            }
            langNameIter.close() ;
        }
    }

    // Do other operation processors
    private void buildOtherProcs(Resource modelSource, SourceModel src)
    {
        StmtIterator operationBindings = modelSource.listProperties(JosekiVocab.hasOperation);
        for (; operationBindings.hasNext();)
        {
            Resource binding = operationBindings.nextStatement().getResource();
            Processor proc = (Processor)moduleLoader.loadAndInstantiate(binding, Processor.class) ; 
            
            if (proc == null)
                continue;
                
            StmtIterator opNameIter = binding.listProperties(JosekiVocab.operationName) ;
            for ( ; opNameIter.hasNext() ;)
            {
                Statement s = opNameIter.nextStatement();
                if ( s.getPredicate().equals(JosekiVocab.queryOperationName))
                    continue ;
                String opName = null ;
                if ( s.getObject() instanceof Literal )
                    opName = s.getString();
                else
                {
                    if ( s.getResource().isAnon() )
                    {
                        log.warn("Operation name must be a literal or a URI") ;
                        continue ;
                    } 
                    opName = s.getResource().getURI() ;
                }
                log.info("  Operation: " + opName);
                if ( src.getProcessorRegistry().hasProcessor(opName) )
                    log.warn("  Operation '"+opName+"' already assigned") ;

                src.getProcessorRegistry().registerProcessor(opName, proc);
            }
            opNameIter.close() ;
        }
    }

      
    private void assignNamespaces(Resource modelSource, SourceModel src)
    {
        // Find the namespace prefixes.
        StmtIterator prefixIter = modelSource.listProperties(JosekiVocab.namespacePrefix);
        for (; prefixIter.hasNext();)
        {
            Statement s = prefixIter.nextStatement();
            if (!(s.getObject() instanceof Resource))
            {
                log.warn("Error in configuration file: " + modelSource + " has non-resource namespace prefix");
                break;
            }
            try
            {
                String prefix = s.getResource().getRequiredProperty(JosekiVocab.prefix).getString();
                String nsURI = s.getResource().getRequiredProperty(JosekiVocab.nsURI).getResource().getURI();
                log.debug("  Namespace: " + prefix + " <=> " + nsURI);
                src.setPrefix(prefix, nsURI);
            }
            catch (RDFException rdfEx)
            {
                log.warn("Error in configuration file: " + modelSource + " has ill-formed prefix declaration");
                break;
            }
        }
    }

    private ClassLoader chooseClassLoader()
    {
        ClassLoader classLoader = null ;
    
        if ( classLoader == null )
        {
            // Find our classloader - one that uses the /WEB-INF/lib and classes directory.
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader != null )
                log.trace("Using thread classloader") ;
        }
        
        if (classLoader == null)
        {
            classLoader = this.getClass().getClassLoader();
            if ( classLoader != null )
                log.trace("Using 'this' classloader") ;
        }
        
        if ( classLoader == null )
        {
            classLoader = ClassLoader.getSystemClassLoader() ;
            if ( classLoader != null )
                log.trace("Using system classloader") ;
        }
        
        if ( classLoader == null )
            log.warn("Failed to find a classloader") ;
        return classLoader ;
    }
    
//    static public void main(String argv[])
//    {
//      try {
//          ModelSet mm = new ModelSet() ;
//          mm.load(RDFServer.defaultConfigFile) ;
//      } catch (Exception ex)
//      {
//          logger.error("Unhandled: "+ex); 
//      }
//    }
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
