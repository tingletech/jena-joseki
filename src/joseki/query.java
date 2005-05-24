/*
 * (c) Copyright 2004 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package joseki;

import com.hp.hpl.jena.joseki2.*;
import com.hp.hpl.jena.rdf.model.*;

import jena.cmdline.*;
import org.apache.commons.logging.* ;

import java.util.*;


/** joseki.query
 * A simple application that builds and executes a Joseki-style GET query.
 * 
 * @author Andy Seaborne
 * @version $Id: query.java,v 1.4 2005-05-24 13:22:27 andy_seaborne Exp $
 */

public class query
{
    static {
        if ( System.getProperty("log4j.configuration") == null )
            System.setProperty("log4j.configuration", "file:etc/log4j.properties") ;
    } ;

    static Log log = LogFactory.getLog(query.class);
    
    public static void main(String[] args)
    {
        CommandLine cmd = new CommandLine() ;
        
        boolean verbose = false ;
        boolean debug = false ;
        boolean reallyDoIt = true ;
        
        // Usage: --model URL --lang name=value
        // NB, values do not need to be encoded.
        
        // Lang is special : it gest to the front of the front of the query string
        // (not necessary - just pretty)
        
        ArgDecl verboseDecl = new ArgDecl(false, "-v", "--verbose") ;
        ArgDecl modelDecl = new ArgDecl(true, "model", "url") ;
        ArgDecl langDecl = new ArgDecl(true, "lang") ;
        ArgDecl debugDecl = new ArgDecl(false, "debug") ;
        ArgDecl helpDecl = new ArgDecl(false, "help", "h") ;
        ArgDecl formatDecl = new ArgDecl(true, "--fmt", "--format") ;
        ArgDecl noActionDecl = new ArgDecl(false, "-n", "--noAction") ;
        
        cmd.add(verboseDecl) ;
        cmd.add(debugDecl) ;
        cmd.add(helpDecl) ;
        cmd.add(modelDecl) ;
        cmd.add(langDecl) ;
        cmd.add(formatDecl) ;
        cmd.add(noActionDecl) ;
        
        cmd.process(args) ;
        
        if ( cmd.contains(helpDecl) )
        {
            usage(System.out) ;
            System.exit(0) ;
        }
        
        if ( cmd.contains(verboseDecl))
            verbose = true ;
        
        if ( cmd.contains(noActionDecl) )
            reallyDoIt = false ; 
        
        if ( !cmd.contains(modelDecl) )
        {
            System.err.println("Missing required parameter: --model") ;
            System.exit(2) ;
        }
        
        String url = cmd.getArg(modelDecl).getValue() ;
        log.debug("URL of model = '"+url+"'") ;
        
        String lang = null ;
        if ( cmd.contains(langDecl) )
            lang = cmd.getArg(langDecl).getValue() ;

        if ( lang != null )
            log.debug("Language of query = '"+lang+"'") ;
        else
            log.debug("No --lang supplied") ;
        
        
        String format = "N3" ;
        
        if ( cmd.contains(formatDecl) )
            format = cmd.getArg(formatDecl).getValue() ;
        
        // Now process remainder.
        
        List paramNames = new ArrayList() ;
        Map params = new HashMap() ;
        
        for ( Iterator iter = cmd.items().iterator() ; iter.hasNext() ; )
        {
            String s = (String)iter.next();
            int j = s.indexOf('=') ;
            String name = s.substring(0,j) ;
            String value = s.substring(j+1) ;
            if ( name.equals("lang" ))
            {
                lang = value ;
                continue ;
            }
            
            //value = Convert.encWWWForm(value) ;
            log.debug("Name = "+name+" :: Value = "+value) ;
            params.put(name, value) ;
            paramNames.add(name) ;
        }

        HttpQuery q = new HttpQuery(url, lang) ;
        
        for ( Iterator iter = paramNames.iterator() ; iter.hasNext() ; )
        {
            String name = (String)iter.next();
            String value = (String)params.get(name) ;
            log.debug("Name = "+name+" :: Value = "+value) ;
                
            q.addParam(name,value) ;
        }
        
        log.debug("URL:: "+q.toString()) ;
        
        if ( verbose )
            System.out.println("URL:: "+q.toString()) ;
        
        if ( !reallyDoIt )
            System.exit(0) ;
        
        log.debug("Attempt request") ;
        
        Model model = null ;
        
        try {
            model = q.exec();
            log.debug("200 - OK") ;
        } catch (HttpException httpEx)
        {
            log.debug(httpEx.getResponseCode()+" - "+httpEx.getMessage());
            System.err.println("Http error: "+httpEx.getResponseCode()+" - "+httpEx.getMessage()) ;
            System.exit(1) ;
        }
        model.write(System.out, format) ;
    }
    
    static void usage(java.io.PrintStream out)
    {
        out.println("Usage: "+query.class.getName()+" --model URL [--lang queryLang] httpQueryString");
        out.println("   --help        Print this message") ;
        out.println("   -n            No action (don't do the query - just build it") ;
        out.println("   -v            Verbose - print the request") ;
        out.println("   --format fmt  Print model using RDF syntax N3, RDF/XML, RDF/XML-ABBREV or N-TRIPLES") ;
    }
}

/*
 * (c) Copyright 2004 Hewlett-Packard Development Company, LP
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