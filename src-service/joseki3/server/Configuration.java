/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package joseki3.server;

import java.util.*;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.resultset.ResultSetRewindable;
import com.hp.hpl.jena.query.util.RelURI;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import dev.RunUtils;

import org.apache.commons.logging.*;

public class Configuration
{
    static { RunUtils.setLog4j() ; }
    static Log log = LogFactory.getLog(Configuration.class) ;
    Model confModel ;
    Resource server ;
    // --------
    
    ServiceRegistry registry = null ;
    Map services = new HashMap() ;
    Set datasets = new HashSet() ;
    
    static public void main(String argv[])
    {
        ServiceRegistry reg = new ServiceRegistry(); 
        Configuration conf = new Configuration(argv[0], reg) ;
    }
    
    public Configuration(String filename, ServiceRegistry registry)
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
            Set definedServices = findServices() ;
            log.info("==== Bind services to the server ====") ;
            Set boundServices = bindServices(registry, definedServices) ;
            checkBoundServices(definedServices, boundServices) ;
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
        
        log.info("Loading : "+Utils.strForURI(filename, null) ) ;
        Model conf = null ; 
            
        try {
            conf = FileManager.get().loadModel(filename) ;
            filesDone.add(filename) ;
        } catch (NotFoundException ex)
        {
            log.warn("** Failed to load: "+ex.getMessage()) ;
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
                    log.warn("** Skipped: include should be a URI, not a literal: "+Utils.nodeLabel(rn) ) ;
                    continue ;
                }
                Resource r  = (Resource)rn ;
                if ( r.isAnon() )
                {
                    log.warn("** Skipped: include should be a URI, not a blank node") ;
                    continue ;
                }
                    
                log.info("Include : "+Utils.nodeLabel(r)) ;
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
            log.warn("** No server description found") ;
            throw new ConfigurationErrorException("No server description") ;
        }
        
        if ( x.size() > 1 )
        {
            log.warn("** Multiple server descriptions found") ;
            throw new ConfigurationErrorException("Too many server descriptions ("+x.size()+")") ;
        }
        
        return (Resource)x.get(0) ; 
    }

    private void findDataSets()
    {
        if ( false )
        {
            List x = findByType(JosekiVocab.RDFDataSet) ;
    
            for ( Iterator iter = x.iterator()  ; iter.hasNext() ; )
            {
                Resource r = (Resource)iter.next() ;
                log.info("Dataset: "+Utils.nodeLabel(r)) ;
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
                    log.warn("** Dataset with no default graph and no named graphs: "+ Utils.nodeLabel(x)) ;
                
                // New dataset
                if ( ! x.equals(ds) )
                {
                    log.info("New dataset: "+Utils.nodeLabel(x)) ;
                    
                    src = new DatasetDesc(confModel) ;
                    
                    // Place in the configuration
                    datasets.add(src) ;
                    
                    if ( dftGraph != null )
                    {
                        log.info("  Default graph : "+Utils.nodeLabel(dftGraph)) ;
                        src.setDefaultGraph(dftGraph) ;
                    }
                    dft = dftGraph ;
                }
                else
                {
                    // Check one default model.
                    if ( dftGraph != null && !dftGraph.equals(dft) )
                        log.warn("  ** Two default graphs") ;
                }
                
                if ( graphName != null )
                {
                    log.info("  Graph / named : <"+graphName+">") ;
                    
                    if ( graphName.isAnon() )
                        throw new ConfigurationErrorException("Graph name can't be a blank node") ; 
                    
                    if ( graphData == null )
                    {
                        log.warn("  ** Graph name but no graph data: <"+graphName.getURI()+">") ;
                        throw new ConfigurationErrorException("No data for graph <"+graphName.getURI()+">") ;
                    }
                    
                    if ( src.getNamedGraphs().containsKey(graphName.getURI()) )
                    {
                        log.warn("  ** Skipping duplicate named graph: <"+graphName.getURI()+">") ;
                        continue ;
                    }
                    
                    src.addNamedGraph(graphName.getURI(), graphData) ;
                }
                
                ds = x ;
            }
        } finally { qexec.close() ; }
        
        
        checkNamedGraphDescriptions() ;
    }
     
    private void checkNamedGraphDescriptions()
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
                    log.warn("** Named graph description with no name and no data. Part of "+Utils.nodeLabel(x)) ;
                
                if ( graphName != null && graphData == null )
                    log.warn("** Named graph description a name but no data: Name = "+Utils.nodeLabel(graphName)) ;
                
                if ( graphName == null && graphData != null )
                    log.warn("** Named graph description with data but no name: "+Utils.nodeLabel(graphData)) ;
            }
        } finally { qexec.close() ; }
    }

    private Set findServices()
    {
        String s[] = new String[]{
            "SELECT *",
            "{",
            "  ?service  joseki:serviceRef  ?serviceRef ;",
            "            joseki:processor   ?proc ." ,
            "  ?proc     module:implementation",
            "                [ module:className ?className ]" ,
            "    }",
            "ORDER BY ?serviceRef ?className" } ;

        Query query = makeQuery(s) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, confModel) ;
        
        // Does not mean the services are asscoiated with the server. 
        Set definedServices = new HashSet() ; 

        try {
            Resource currentService = null ;
            
            for ( ResultSet rs = qexec.execSelect() ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                RDFNode serviceNode = qs.getResource("service") ;
                Resource proc = qs.getResource("proc") ;
                RDFNode className = qs.get("className") ;
                
                RDFNode x = qs.getLiteral("serviceRef") ;
                if ( ! ( x instanceof Literal ) ) 
                {
                    log.warn("** Service references are literals (a URI ref which will be relative to the server)") ;
                    continue ;
                }
                
                String ref = qs.getLiteral("serviceRef").getLexicalForm() ;
                String javaClass = null ;

                log.info("Service: <"+ref+">") ;
                
                if ( ! ( serviceNode instanceof Resource ) )
                {
                    log.warn("** Service not a resource: "+Utils.nodeLabel(serviceNode)) ;
                    continue ;
                }

                // Checking.
                javaClass = classNameFromNode(className) ;
                if ( javaClass == null )
                    continue ;
                
                log.info("  Class name: "+javaClass) ;
                Service service = new Service(proc) ; 
                services.put(ref, service) ;
                
                // Record all well-formed services found.
                definedServices.add(serviceNode) ;
            }
        } catch (JenaException ex)
        {
            log.fatal("Problems finding services") ;
            throw ex ;
        }
        finally { qexec.close() ; }
        
        // Check that we don't find more part forms services 
        // than well formed service descriptions
        checkServices(definedServices) ;

        // Check that we don't find more part formed service impls
        // than well formed service descriptions
        checkServiceImpls(definedServices) ;
        
        return definedServices ;
    }

    private void checkServices(Set definedServices)
    {
        String[] s ;
        Query q ;
        QueryExecution qexec ;
        // ---- Check : class names for implementations
        List x = findByType(JosekiVocab.ServicePoint) ;
        for ( Iterator iter = x.iterator() ; iter.hasNext() ; )
        {
            Resource r = (Resource)iter.next() ;
            if ( !definedServices.contains(r) )
                 log.warn("** No implementation for service: "+Utils.nodeLabel(r) ) ;
        }
    }

    private void checkServiceImpls(Set definedServices)
    {
        String [] s = new String[]{
            "SELECT *",
            "{  ?SP  joseki:serviceRef ?serviceRef ; }"
        } ;
        
        Query query = makeQuery(s) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, confModel) ;
        try {
            for ( ResultSet rs = qexec.execSelect() ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                RDFNode n = qs.get("SP") ;
                RDFNode ref = qs.get("serviceRef") ;
                if ( ! (ref instanceof Literal) )
                    log.warn("** Service reference isn't a literal: "+Utils.nodeLabel(ref) ) ;
                
                if ( ! definedServices.contains(n) )
                    log.warn("** No class name for service with reference: "+Utils.nodeLabel(ref) ) ;
            }
        } finally { qexec.close() ; }
    }

    private Set bindServices(ServiceRegistry registry, Set definedServices)
    {
        String [] s = new String[]{
            "SELECT ?service ?serviceRef ",
            "{ []        joseki:service     ?service . ",
            "  ?service  joseki:serviceRef  ?serviceRef",
            "}",
            "ORDER BY ?serviceRef"
        } ;
        
        Set boundServices = new HashSet() ;
        Query query = makeQuery(s) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, confModel) ;
        try {
            for ( ResultSet rs = qexec.execSelect() ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                RDFNode srvNode = qs.get("service") ;
                String ref = qs.getLiteral("serviceRef").getLexicalForm() ;
                log.info("Configured service: <"+ref+">") ;
                Service srv = (Service)services.get(ref) ;
                registry.add(ref, srv) ;
                boundServices.add(srvNode) ;
            }
        } finally { qexec.close() ; }
        return boundServices ;
    }

    
    private void checkBoundServices(Set definedServices, Set boundServices)
    {
        Set x = new HashSet(definedServices) ;
        x.removeAll(boundServices) ;
        for ( Iterator iter = x.iterator() ; iter.hasNext() ; )
        {
            Resource srv = (Resource)iter.next() ;
            log.warn("** Service not attached to a server: "+Utils.nodeLabel(srv)) ;
        }
    }
    
    private List findByType(Resource r)
    {
        if ( r.isAnon() )
        {
            log.warn("** BNode for type - not supported (yet)") ;
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
    
    public static String classNameFromNode(RDFNode n)
    {
        String className = null ;
        
        if ( n instanceof Literal )
        {
            Literal lit = (Literal)n ;
            className = lit.getLexicalForm() ;
            if ( className.startsWith("java:") ) 
                className.substring("java:".length()) ; 
            return className ;
        }

        Resource r = (Resource)n ;
        if ( r.isAnon() )
        {
            log.warn("** Class name is a blank node") ;
            return null ;
        }
        
        if ( ! r.getURI().startsWith("java:") )
        {
            log.warn("** Class name is a URI but not from the java: scheme") ;
            return null ;
        }
        className = r.getURI().substring("java:".length()) ;
        return className ; 
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