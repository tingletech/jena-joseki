/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package joseki;

import java.util.* ;
import java.io.* ;
import java.net.* ;

import jena.cmdline.*;

import com.hp.hpl.jena.joseki.* ;

import com.hp.hpl.jena.rdf.model.* ;
//import com.hp.hpl.jena.rdf.model.impl.* ;
//import com.hp.hpl.jena.vocabulary.* ;
import com.hp.hpl.jena.rdql.* ;

/** Command line application to issue queries against a remote model.
 *
 * @author  Andy Seaborne
 * @version $Id: rdfqueryremote.java,v 1.1 2004-11-03 10:15:05 andy_seaborne Exp $
 */


public class rdfqueryremote
{
    public static final String defaultURL = "http://localhost:2020/rdfserver/rdf" ;
    public static URL targetURL = null ;
    public static String modelURLStr = null ;
    
    public static final String FormatArg   = "format" ;
    public static final String QueryFile   = "query" ;
    public static final String ModelURLArg = "model" ;
    public static final String ModelURLArgAlt = "url" ;
    
    public static boolean VERBOSE = false ;
    public static boolean DEBUG = false ;
    
    static final int FMT_NONE    = -1 ;
    static final int FMT_TUPLES  = 0 ;
    static final int FMT_TEXT    = 1 ;
    static final int FMT_HTML    = 2 ;
    static final int FMT_DUMP    = 3 ;

    static public int outputFormat = FMT_TEXT ;
    
    
    public static void main (String args[])
    {
        try {
            String usageMessage = rdfqueryremote.class.getName()+
                                    " [--verbose] [--format fmt] "+
                                    "[--"+ModelURLArg+" modelURL] {--query file | queryString}" ;

            CommandLine cmd = new CommandLine() ;
            cmd.setUsage(usageMessage) ;

            ArgDecl verboseDecl = new ArgDecl(false, "-v", "--verbose") ;
            ArgDecl modelDecl = new ArgDecl(true, ModelURLArg, ModelURLArgAlt) ;
            
            cmd.add(verboseDecl) ;
            cmd.add(modelDecl) ;
            
            cmd.add("--format", true) ;

            cmd.add("--debug", false) ;
            cmd.add(QueryFile, true) ;

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

            if ( ! cmd.contains(QueryFile) && cmd.args().size() == 0 )
            {
                //System.err.println("Need either a query string or a query in a file") ;
                System.err.println(usageMessage) ;
                System.exit(0) ;
            }

            if ( cmd.contains(modelDecl) )
                modelURLStr = cmd.getArg(modelDecl).getValue() ;
           
            for (Iterator iter = cmd.args().iterator(); iter.hasNext();)
            {
                Arg arg = (Arg) iter.next();

                if (arg.getName().equals(FormatArg))
                {
                    String argValue = arg.getValue();
                    if (argValue.equalsIgnoreCase("none"))
                        outputFormat = FMT_NONE;
                    else if (argValue.equalsIgnoreCase("tuples"))
                        outputFormat = FMT_TUPLES;
                    else if (argValue.equalsIgnoreCase("tuple"))
                        outputFormat = FMT_TUPLES;
                    else if (argValue.equalsIgnoreCase("text"))
                        outputFormat = FMT_TEXT;
                    else if (argValue.equalsIgnoreCase("html"))
                        outputFormat = FMT_HTML;
                    else if (argValue.equalsIgnoreCase("dump"))
                        outputFormat = FMT_DUMP;
                    else
                    {
                        System.err.println("Unrecognized output format: " + argValue);
                        System.exit(1);
                    }
                    continue;
                }
            }

            List queries = new ArrayList() ;

            if ( cmd.contains(QueryFile) )
            {
                try {
                    String qs = com.hp.hpl.jena.util.FileUtils.readWholeFileAsUTF8(cmd.getArg(QueryFile).getValue()) ;
                    // Goes first
                    queries.add(qs) ;
                } catch (IOException ioEx)
                {
                    System.err.println("Failed to read file: "+cmd.getArg(QueryFile).getValue()) ;
                    ioEx.printStackTrace(System.err) ;
                    System.exit(1) ;
                }
            }

            for ( Iterator iter = cmd.items().iterator() ; iter.hasNext() ; )
                queries.add(iter.next()) ;

            if ( queries.size() > 0 )
            {
                for ( Iterator iter = queries.iterator() ; iter.hasNext(); )
                {
                    String queryString = (String)iter.next() ;
                    doOneQuery(queryString, modelURLStr) ;
                }
            }
            else
            {
                // No query - do a plain GET
                doPlainGet(modelURLStr) ;
            }
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage()) ;
            //e.printStackTrace(System.err) ;
            System.exit(9) ;
        }
    }
    
    
    static void doOneQuery(String queryString, String modelURLstr)
    {
        try
        {
            boolean doBlank = false;
            Query q = new Query(queryString);
            
            if ( VERBOSE )
                System.out.println(q.toString()) ;
            
            String u = modelURLStr;

            if (u == null)
                u = q.getSourceURL();

            if (u == null)
            {
                System.err.println("No target model for query");
                System.exit(1);
            }

            QueryEngineHTTP qHTTP = new QueryEngineHTTP(q, u, "RDQL");
            QueryExecution qe = qHTTP;
            
            if ( VERBOSE )
            {
                System.out.println(qHTTP.getHttpQuery().toString()) ;
                System.out.println() ;
            }
            
            QueryResults results = qe.exec();

            if (results == null)
            {
                System.err.println("doOneQuery: Null results iterator");
                return;
            }

            QueryResultsFormatter fmt = new QueryResultsFormatter(results);
            PrintWriter pw = new PrintWriter(System.out);

            if (outputFormat == FMT_NONE)
                fmt.consume();
            else
            {
                if (doBlank)
                    System.out.println();
                switch (outputFormat)
                {
                    case FMT_TEXT :
                        fmt.printAll(pw);
                        break;
                    case FMT_HTML :
                        fmt.printHTML(pw);
                        break;
                    case FMT_TUPLES :
                        fmt.dump(pw, true);
                        break;
                    case FMT_DUMP :
                        fmt.dump(pw, false);
                        break;
                }
                pw.flush();
                doBlank = true;
            } 
            
            if ( VERBOSE )
            {
                System.out.println() ;
                ((QueryEngineHTTP)qe).getResultModel().write(System.out,"N3") ;
            }
            fmt.close() ;   
            results.close() ;
            qe.close() ;
        }
        catch (QueryException qEx)
        {
            System.err.println("rdfqueryremote: "+qEx.getMessage()) ;
            return ;
        }
        catch (HttpException httpEx)
        {
            if ( httpEx.getResponseCode() == HttpException.NoServer )
                System.err.println("rdfqueryremote: Failed to contact the server") ;
            else
                System.err.println("Error: "+httpEx.getResponseCode()+" : "+httpEx.getResponseMessage()) ;
            return ;
        }
        catch (JosekiException ex)
        {
            System.err.println("Exception: "+modelURLStr+" :: "+ex) ;
            System.exit(2) ;
        }
    }
    
    static private void doPlainGet(String urlStr) throws MalformedURLException
    {
        try
        {
            HttpQuery q = new HttpQuery(urlStr, null);
            Model resultModel = q.exec();

            PrintWriter pw = new PrintWriter(System.out);
            resultModel.write(pw, "N3");
            pw.flush();
            return;
        }
        catch (JosekiException jEx)
        {
            System.err.println("Exception for " + urlStr);
            return;
        }
        catch (RDFException rdfEx)
        {
            System.err.println("Unexception rdf exception: " + rdfEx);
            return;
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
