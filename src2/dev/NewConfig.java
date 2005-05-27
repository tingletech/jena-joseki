/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;

import java.util.*;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.core.ResultBinding;
import com.hp.hpl.jena.query.util.QueryPrintUtils;
import com.hp.hpl.jena.query.util.RelURI;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.NotFoundException;
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
            server = findServer() ;
            findDataSets() ;
            findServices() ;
        } catch (Exception ex)
        {
            log.fatal("Failed to parse configuration file", ex) ;
            confModel = null ;
        }
    }
    
    public void readConfFile(Model confModel2, String filename, Set filesDone)
    {
        if ( filesDone.contains(filename) )
            return ;
        
        log.info("Loading : "+filename) ;
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
                    Literal lit = (Literal)rn ;
                    String tmp = QueryPrintUtils.stringForRDFNode(rn, query.getPrefixMapping()) ;
                    log.warn("Skipped: include should be a URI, not a literal: "+tmp ) ;
                    continue ;
                }
                Resource r  = (Resource)rn ;
                if ( r.isAnon() )
                {
                    log.warn("Skipped: include should be a URI, not a blank node") ;
                    continue ;
                }
                    
                log.info("Include : "+r.getURI()) ;
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


    
    private void findServices()
    {
        String s[] = new String[]{ "SELECT ?service { ?server joseki:service ?service }" } ;
        ResultBinding rb = new ResultBinding(confModel) ;
        rb.add("server", server) ;
        Query q = makeQuery(s) ;
        QueryExecution qexec = QueryExecutionFactory.create(q, confModel, rb) ;

        try {
            for ( ResultSet rs = qexec.execSelect() ; rs.hasNext() ; )
            {
                QuerySolution qs = rs.nextSolution() ;
                RDFNode n = qs.get("service") ;
                log.info("Service: "+n) ;
            }
        } finally { qexec.close() ; }
    }

    private void findDataSets()
    {
        List x = findByType(JosekiVocab.RDFDataSet) ;

        for ( Iterator iter = x.iterator()  ; iter.hasNext() ; )
        {
            Resource r = (Resource)iter.next() ;
            log.info("Dataset: "+r) ;
        }
    }

    // ------------------------------------------------------

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