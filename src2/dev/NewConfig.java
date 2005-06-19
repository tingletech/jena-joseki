/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;

import java.util.*;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.core.ResultBinding;
import com.hp.hpl.jena.query.resultset.ResultSetRewindable;
import com.hp.hpl.jena.query.util.RelURI;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import org.apache.commons.logging.*;

public class NewConfig
{
    static { RunUtils.setLog4j() ; }
    static Log log = LogFactory.getLog(NewConfig.class) ;
    Model confModel ;
    Resource server ;
    // --------
    // The configuration (one server)
    // Datasets
    // Services
    
    Set services = new HashSet() ;
    Set datasets = new HashSet() ;
    
    static void main(String argv[])
    {
        NewConfig conf = new NewConfig(argv[0]) ;
    }
    
    public NewConfig(String filename)
    {
        filename = RelURI.resolveFileURL(filename) ;
        confModel = ModelFactory.createDefaultModel() ;

        Set filesDone = new HashSet() ;
        
        try {
            readConfFile(confModel, filename, filesDone) ;
            verify() ;
            server = findServer() ;
            log.info("==== Datasets ====") ;
            findDataSets() ;
            log.info("==== Services ====") ;
            findServices() ;
        } catch (RuntimeException ex)
        {
            log.fatal("Failed to parse configuration file", ex) ;
            // Clear an structures we may have partialy built.
            confModel = null ;
            services = null ;
            datasets = null ;
            throw ex ;
        }
    }
    
    private void verify()
    {
        // Eyeball?
        // Check processors
        // One service - one processor
        
        // May change to removing the level of indirection to the class
        // Do we want shared classes and arguments?
        
        // a   joseki:QueryLanguageBinding ;
        //     joseki:queryOperationName
        //  --module:implementation-->
        //     a joseki:QueryOperation ;
        //     module:className
        
        // a   joseki:OperationBinding
        //     joseki:operationName
        //  --module:implementation-->
        //     a joseki:QueryOperation ;
        //     module:className
        
        
        // Check models
        // ??
        
        // Check datasets
        // One default graph
        // Named graphs have name and data
        
        // Check server
        // One server
        // Has at least one service endpoint.
    }

    private void readConfFile(Model confModel2, String filename, Set filesDone)
    {
        if ( filesDone.contains(filename) )
            return ;
        
        log.info("Loading : "+strForURI(filename, null) ) ;
        Model conf = null ; 
            
        try {
            conf = FileManager.get().loadModel(filename) ;
            filesDone.add(filename) ;
        } catch (NotFoundException ex)
        {
            log.warn("Failed to load: "+ex.getMessage()) ;
            return ;
        }
        
        String s[] = new String[]{ "SELECT ?i { ?x joseki:include ?i }" } ;
        Query query = makeQuery(s) ; 
        QueryExecution qexec = QueryExecutionFactory.create(query, confModel);
        ResultSet rs = qexec.execSelect() ;
        
        List includes = new ArrayList() ;

        try {
            for ( ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                RDFNode rn = qs.get("i") ;
                if ( rn instanceof Literal )
                {
                    log.warn("Skipped: include should be a URI, not a literal: "+strForNode(rn) ) ;
                    continue ;
                }
                Resource r  = (Resource)rn ;
                if ( r.isAnon() )
                {
                    log.warn("Skipped: include should be a URI, not a blank node") ;
                    continue ;
                }
                    
                log.info("Include : "+strForResource(r)) ;
                includes.add(r.getURI()) ;
            }
        } finally { qexec.close() ; } 
        
        confModel.add(conf) ;
        for ( Iterator iter = includes.iterator() ; iter.hasNext() ; )
        {
            String fn = (String)iter.next() ; 
            readConfFile(confModel, fn, filesDone) ; 
        }
    }
        
    private Resource findServer()
    {
        List x = findByType(JosekiVocab.Server) ;
        if ( x.size() == 0 )
        {
            log.warn("No server description found") ;
            throw new ConfigurationErrorException("No server description") ;
        }
        
        if ( x.size() > 1 )
        {
            log.warn("Multiple server descriptions found") ;
            throw new ConfigurationErrorException("Too many server descriptions ("+x.size()+")") ;
        }
        
        return (Resource)x.get(0) ; 
    }

    // Test cases
    static class DatasetDesc
    {
        Model confModel ;
        // Resource wil keep the config model around as well. 
        private Resource defaultGraph = null ;
        private Map namedGraphs = new HashMap() ;
        
        DatasetDesc(Model conf) { confModel = conf ; }
        
        /** @return Returns the dftGraph. */
        public Resource getDefaultGraph() { return defaultGraph ;  }
        /**  * @param dftGraph The dftGraph to set. */
        public void setDefaultGraph(Resource dftGraph) { this.defaultGraph = dftGraph ; }
    
        /** @return Returns the namedGraphs. */
        public Map getNamedGraphs()
        {
            return namedGraphs ;
        }
        /**  * @param namedGraphs The namedGraphs to set. */
        public void addNamedGraph(String uri, Resource r)
        {
            namedGraphs.put(uri, r) ;
        }
        
        
        private Model buildModel(Resource r)
        {
            ModelSpec mSpec = ModelFactory.createSpec(r, confModel) ; 
            Model m = ModelFactory.createModel(mSpec) ;
            log.info("Building model: "+strForResource(r)) ;
            m.write(System.out, "N3") ;
            return m ;
        }
    }
    
    class Service
    {
        Service(String className) {}
    }
    
    // Fix configuration file!
    // This shortcuts service -> classname 
    // from service -> implementation -> classname
    // because implmentation == class
    // Maybe use java:org.joseki... scheme
    
    private void findDataSets()
    {
        if ( false )
        {
            List x = findByType(JosekiVocab.RDFDataSet) ;
    
            for ( Iterator iter = x.iterator()  ; iter.hasNext() ; )
            {
                Resource r = (Resource)iter.next() ;
                log.info("Dataset: "+strForResource(r)) ;
            }
        }
        // Need to do validity checking on the configuration model.
        // Can also do like services - once with a fixed query then reduce elements
        // to see if we find the same things
        String[] s = new String[] {
                // Downside is that we have inlined the URIs
//               "SELECT ?x ?dft ?named ?graphName ?graphData",
//               "{ ?x a joseki:RDFDataSet ;",
//               "  OPTIONAL { ?x joseki:defaultGraph ?dft }",
//               "  OPTIONAL { ?x joseki:namedGraph   ?named",
//                            // OPTIONAL here is for error checking. 
//                            "OPTIONAL { ?named joseki:graphName ?graphName }",
//                            "OPTIONAL { ?named joseki:graphData ?graphData }",
//               "           }",
//               "}", 
//               "ORDER BY ?x ?dft ?named"
               "SELECT ?x ?dft ?graphName ?graphData",
               "{ ?x a joseki:RDFDataSet ;",
               "  OPTIONAL { ?x joseki:defaultGraph ?dft }",
               "  OPTIONAL { ?x joseki:namedGraph  [ joseki:graphName ?graphName ; joseki:graphData ?graphData ] }",  
               "}", 
               "ORDER BY ?x ?dft ?graphName"
               } ;
               
        
        Query query = makeQuery(s) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, confModel) ;
        try {
            ResultSet rs = qexec.execSelect() ;
            if ( false )
            {
                ResultSetRewindable rs2 = ResultSetFactory.makeRewindable(rs) ;
                ResultSetFormatter.out(System.out, rs2, query) ;
                rs2.reset() ;
                rs = rs2 ;
            }
            
            DatasetDesc src = null ;
            Resource ds = null ;
            Resource dft = null ;
            for ( ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                Resource x = qs.getResource("x") ;
                
                // Impossible
                // if ( x == null ) {}
                
                Resource dftGraph = qs.getResource("dft") ; 
                //Resource namedGraph = qs.getResource("named") ;
                Resource graphName = qs.getResource("graphName") ;
                Resource graphData = qs.getResource("graphData") ;
                
                if ( dftGraph == null &&  graphName == null )
                    // Note the named graph match assumed a well-formed name/data pair
                    log.warn("Dataset with no default graph and no named graphs: "+ strForResource(x)) ;
                
                // New dataset
                if ( ! x.equals(ds) )
                {
                    log.info("New dataset: "+strForResource(x)) ;
                    
                    src = new DatasetDesc(confModel) ;
                    
                    // Place in the configuration
                    datasets.add(src) ;
                    
                    if ( dftGraph != null )
                    {
                        log.info("Default graph : "+strForResource(dftGraph)) ;
                        src.setDefaultGraph(dftGraph) ;
                    }
                    dft = dftGraph ;
                }
                else
                {
                    // Check one default model.
                    if ( dftGraph != null && !dftGraph.equals(dft) )
                        log.warn("Two default graphs") ;
                }
                
                if ( graphName != null )
                {
                    log.info("Graph / named : <"+graphName+">") ;
                    
                    if ( graphName.isAnon() )
                        throw new ConfigurationErrorException("Graph name can't be a blank node") ; 
                    
                    if ( graphData == null )
                    {
                        log.warn("Graph name but no graph data: <"+graphName.getURI()+">") ;
                        throw new ConfigurationErrorException("No data for graph <"+graphName.getURI()+">") ;
                    }
                    
                    if ( src.getNamedGraphs().containsKey(graphName.getURI()) )
                    {
                        log.warn("Skipping duplicate named graph: <"+graphName.getURI()+">") ;
                        continue ;
                    }
                    
                    src.addNamedGraph(graphName.getURI(), graphData) ;
                }
                
                ds = x ;
            }
        } finally { qexec.close() ; }
        
        
        doNamedGraphCheck() ;
    }
     
    private void doNamedGraphCheck()
    {
        // Check with reduced queries
        
        String[] s = new String[] {
           "SELECT ?x ?ng ?graphName ?graphData",
           "{ ?x joseki:namedGraph  ?ng ." +
           "  OPTIONAL { ?ng joseki:graphName ?graphName }",  
           "  OPTIONAL { ?ng joseki:graphData ?graphData }",  
           "}", 
           "ORDER BY ?ng ?graphName ?graphData"
           } ;
        Query query = makeQuery(s) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, confModel) ;
        try {
            ResultSet rs = qexec.execSelect() ;
            for ( ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                Resource x         = qs.getResource("x") ;
                Resource ng        = qs.getResource("ng") ;
                Resource graphName = qs.getResource("graphName") ;
                Resource graphData = qs.getResource("graphData") ;
                
                if ( graphName == null && graphData == null )
                    log.warn("Named graph description with no name and no data: "+strForResource(x)) ;
                
                if ( graphName != null && graphData == null )
                    log.warn("Named graph description a name but no data: Name = "+strForResource(graphName)) ;
                
                if ( graphName == null && graphData != null )
                    log.warn("Named graph description with data but no name: "+strForResource(graphData)) ;
            }
        } finally { qexec.close() ; }
    }

    private void findServices()
    {
        String s[] = new String[]{
            "SELECT ?service ?className",
            "{",
            "[] joseki:service ?service .",
            "?service module:implementation",
            "          [ module:className ?className ]" ,
            "    }",
            "ORDER BY ?service ?className" } ;

        ResultBinding rb = new ResultBinding(confModel) ;
        rb.add("server", server) ;
        Query q = makeQuery(s) ;
        QueryExecution qexec = QueryExecutionFactory.create(q, confModel, rb) ;
        Set serviceResources = new HashSet() ; 

        try {
            Resource currentService = null ;
            
            for ( ResultSet rs = qexec.execSelect() ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                RDFNode n = qs.get("service") ;
                log.info("Service:    "+strForNode(n)) ;
                if ( ! ( n instanceof Resource ) )
                {
                    log.warn("Service not a resource: "+strForNode(n)) ;
                    continue ;
                }
                Resource serv = (Resource)n ;

                if ( currentService == serv )
                {
                    log.warn("More than one implementation or more that one class: "+strForResource(serv)) ;
                    throw new ConfigurationErrorException("Malformed service definition: "+strForResource(serv)) ;
                }
                
                String className = null ;
                RDFNode cn = qs.get("className") ;
                
                if ( cn instanceof Literal )
                {
                    className = ((Literal)cn).getLexicalForm() ;
                }
                else
                {
                    Resource r = (Resource)cn ;
                    if ( r.isAnon() )
                    {
                        log.warn("Class name is a blank node!") ;
                        continue ;
                    }
                    if ( ! r.getURI().startsWith("java:") )
                    {
                        log.warn("Class name is a URI but not from the java: scheme") ;
                        continue ;
                    }
                    className = r.getURI().substring("java:".length()) ; 
                }
                log.info("Class name: "+className) ;
                    
                 services.add(new Service(className)) ;
                 //log.info("Service resource: "+n) ;
                 serviceResources.add(n) ;
                 currentService = serv ;
            }
        } finally { qexec.close() ; }
        
        // CHECKING
        
        // ---- Check : class names for implementations
        s = new String[]{
            "SELECT DISTINCT ?service",
            "{ []  joseki:service ?service .",
            "  ?service module:implementation []  }"
            } ;
        
        q = makeQuery(s) ;
        qexec = QueryExecutionFactory.create(q, confModel, rb) ;
        try {
            for ( ResultSet rs = qexec.execSelect() ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                RDFNode n = qs.get("service") ;
                if ( ! serviceResources.contains(n) )
                    log.warn("No class name of service: "+strForNode(n) ) ;
            }
        } finally { qexec.close() ; }

        // ---- Check : class names for implementations
        s = new String[]{
            "SELECT DISTINCT ?service",
            "{ [] joseki:service ?service }" } ;
        
        q = makeQuery(s) ;
        qexec = QueryExecutionFactory.create(q, confModel, rb) ;
        try {
            for ( ResultSet rs = qexec.execSelect() ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                RDFNode n = qs.get("service") ;
                if ( !serviceResources.contains(n) )
                    log.warn("No implementation for service: "+strForNode(n) ) ;
            }
        } finally { qexec.close() ; }
    }

    private List findByType(Resource r)
    {
        if ( r.isAnon() )
        {
            log.warn("BNode for type - not supported (yet)") ;
            return null ;
        }
        
        return findByType(r.getURI()) ;
    }
    
    private List findByType(String classURI)
    {
        // Keep in same order that the query finds them.
        List things = new ArrayList() ; 
        
        String s = "PREFIX rdf: <"+RDF.getURI()+">\nSELECT ?x { ?x rdf:type <"+classURI+"> }" ;
        Query q = makeQuery(s) ;
        QueryExecution qexec = QueryExecutionFactory.create(q, confModel) ;

        try {
            for ( ResultSet rs = qexec.execSelect() ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                RDFNode n = qs.get("x") ;
                things.add(n) ;
            }
        } finally { qexec.close() ; }
        return things ;
    }
    
    
    // ------------------------------------------------------
        
    private Query makeQuery(String[] a) 
    {
        StringBuffer sBuff = new StringBuffer() ;
        stdHeaders(sBuff) ;
        sBuff.append("\n") ;
        
        for ( int i = 0 ; i < a.length ; i++ )
        {
            if ( i != 0 )
                sBuff.append("\n") ;
            sBuff.append(a[i]) ;
        }
        
        String qs = sBuff.toString() ;
        return makeQuery(qs) ;
    }

    private Query makeQuery(String qs) 
    {
        try {
            Query query = QueryFactory.create(qs) ;
            return query ;
        } catch (QueryParseException ex)
        {
            System.out.println(qs) ;
            log.fatal("Query failed: "+ex.getMessage()) ;
            throw new ConfigurationErrorException("Internal error") ;
        }
    }

    
    private static void stdHeaders(StringBuffer sBuff)
    {
        stdNS(sBuff, "rdf",  RDF.getURI()) ;
        stdNS(sBuff, "rdfs", RDFS.getURI()) ;
        stdNS(sBuff, "module" , "http://joseki.org/2003/06/module#") ;
        stdNS(sBuff, "joseki" ,  JosekiVocab.getURI()) ;
    }
    
    private static void stdNS(StringBuffer sBuff, String prefix, String namespace)
    {
        sBuff.append("PREFIX ") ;
        sBuff.append(prefix) ;
        sBuff.append(":") ;
        sBuff.append("  ") ;
        sBuff.append("<") ;
        sBuff.append(namespace) ;
        sBuff.append(">") ;
        sBuff.append("\n") ;
    }
    
    
    private static String strForNode(RDFNode n)
    {
        if ( n instanceof Resource )
            return strForResource((Resource)n, null) ;
        
        Literal lit = (Literal)n ;
        return lit.getLexicalForm() ;
    }
        
    
    
    private static String strForResource(Resource r) { return strForResource(r, null) ; }
    
    private static String strForResource(Resource r, PrefixMapping pm)
    {
        if ( r == null )
            return "NULL ";
        if ( r.hasProperty(RDFS.label))
        {
            RDFNode n = r.getProperty(RDFS.label).getObject() ;
            if ( n instanceof Literal )
                return ((Literal)n).getString() ;
        }
        
        if ( r.isAnon() )
            return "<<blank node>>" ;

        if ( pm == null )
            pm = r.getModel() ;

        return strForURI(r.getURI(), pm ) ;
    }
    
    private static String strForURI(String uri, PrefixMapping pm)
    {
        if ( pm != null )
        {
            String x = pm.shortForm(uri) ;
            
            if ( ! x.equals(uri) )
                return x ;
        }
        return "<"+uri+">" ;
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