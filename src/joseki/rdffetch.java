/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package joseki;

import jena.cmdline.*;
import com.hp.hpl.jena.joseki.* ;
import com.hp.hpl.jena.rdf.model.* ;

/** Command line application to issue queries against a remote model.
 *
 * @author  Andy Seaborne
 * @version $Id: rdffetch.java,v 1.1 2004-11-03 10:15:05 andy_seaborne Exp $
 */


public class rdffetch
{
    public static final String defaultURL = "http://localhost:2020/rdfserver/rdf" ;
    
    public static boolean VERBOSE = false ;
    public static boolean DEBUG = false ;
    
    public static void main (String args[])
    {
        try {
            String usageMessage = rdffetch.class.getName()+
                                    " [--verbose] [--format fmt] "+
                                    "--model modelURL --resource URI";

            CommandLine cmd = new CommandLine() ;
            cmd.setUsage(usageMessage) ;

            ArgDecl verboseDecl = new ArgDecl(false, "-v", "--verbose") ;
            ArgDecl modelDecl = new ArgDecl(true, "model", "url") ;
            ArgDecl resDecl = new ArgDecl(true, "resource", "r") ;
            
            cmd.add(verboseDecl) ;
            cmd.add(modelDecl) ;
            cmd.add(resDecl) ;
            
            cmd.add("--format", true) ;
            cmd.add("--debug", false) ;

            // Addition argument, after the flags, is a query
            cmd.process(args) ;

            if ( cmd.contains("--debug") )
                DEBUG = true ;

            if ( cmd.contains("--help") )
            {
                System.err.println(usageMessage) ;
                System.exit(0) ;
            }

            if ( cmd.contains(verboseDecl) )
                VERBOSE = true ;
            
                
            if ( ! cmd.contains(modelDecl) )
            {
                System.err.println(usageMessage) ;
                System.err.println("Required argument: --model") ;
                System.exit(8) ;
            }

            if ( ! cmd.contains(resDecl) )
            {
                System.err.println(usageMessage) ;
                System.err.println("Required argument: --resource") ;
                System.exit(8) ;
            }
            
            String format = "N3" ;
            String modelStr = cmd.getArg(modelDecl).getValue() ;
            String resource = cmd.getArg(resDecl).getValue() ;
            
            if ( cmd.contains("format") )
                format = cmd.getArg("format").getValue() ;
            
            HttpFetch fetch = new HttpFetch(modelStr, resource) ;
            Model obj = fetch.exec() ;
            
            obj.write(System.out, format) ;
            
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err) ;
            System.exit(9) ;
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
