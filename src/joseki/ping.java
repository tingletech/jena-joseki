/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package joseki;

import jena.cmdline.*;

import com.hp.hpl.jena.joseki2.*;
import com.hp.hpl.jena.rdf.model.* ;

/** Command line application to issue a ping on a remote model.
 *
 * @author  Andy Seaborne
 * @version $Id: ping.java,v 1.2 2005-05-24 13:22:27 andy_seaborne Exp $
 */


public class ping
{
    public static final String defaultURL = "http://localhost:2020/rdfserver/rdf" ;
    
    public static boolean VERBOSE = false ;
    public static boolean DEBUG = false ;
    
    public static void main (String args[])
    {
        try {
            String usageMessage = ping.class.getName()+
                                    " [--verbose] --model modelURL";

            CommandLine cmd = new CommandLine() ;
            cmd.setUsage(usageMessage) ;

            ArgDecl verboseDecl = new ArgDecl(false, "-v", "--verbose") ;
            ArgDecl modelDecl = new ArgDecl(true, "model", "url") ;
            
            cmd.add(verboseDecl) ;
            cmd.add(modelDecl) ;
            
            cmd.add("--debug", false) ;

            // No addition arguments after the flags
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

            String modelStr = cmd.getArg(modelDecl).getValue() ;
            
            HttpPing pingOp = new HttpPing(modelStr) ;
            Model obj = pingOp.exec() ;
            
            //obj.write(System.out, format) ;
            
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
