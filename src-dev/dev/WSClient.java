/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.axis.AxisFault;
import org.apache.axis.types.URI;
import org.joseki.ws1client.JosekiQueryServiceLocator;
import org.joseki.ws1client.QueryType;

import org.w3.www._2001.sw.DataAccess.rf1.result2.Binding;
import org.w3.www._2001.sw.DataAccess.rf1.result2.Result;
import org.w3.www._2001.sw.DataAccess.rf1.result2.Results;
import org.w3.www._2001.sw.DataAccess.rf1.result2.Variable;

import org.w3.www._2001.sw.DataAccess.sparql_protocol_types.Query;
import org.w3.www._2001.sw.DataAccess.sparql_protocol_types.QueryResult;

public class WSClient
{
    public static void main(String[] args)
    {
        try {
            //String fmt = "yyyy-MM-dd'T'HH:mm:ss.SZ" ;
            String fmt = "HH:mm:ss" ;
            
            SimpleDateFormat dFmt = new SimpleDateFormat(fmt) ;
            String now = dFmt.format(new Date()) ;
            
//            HappyClient hc = new HappyClient(System.out) ;
//            hc.verifyClientIsHappy(false) ;

            String endpoint = "http://localhost:2525/axis/services/sparql-query" ;
            //String endpoint = "http://localhost:2020/dump-body" ;
            JosekiQueryServiceLocator  service = new JosekiQueryServiceLocator();
            service.setSparqlQueryEndpointAddress(endpoint) ;

            QueryType qt = service.getSparqlQuery() ;
            Query q = new Query() ;
            q.setSparqlQuery("SELECT ("+now+")") ;
            q.setDefaultGraphUri(new URI("http://default")) ;
            q.setNamedGraphUri(new URI[]{
                new URI("http://host/name1"),
                new URI("http://host/name2")
            }) ;
            
            // Do it.
            QueryResult qr = qt.query(q) ;
            
            Variable[] vars = qr.getSparql().getHead().getVariable() ;
            
            for ( int i = 0 ; i < vars.length; i++ )
            {
                Variable v = vars[i] ; 
                String vn = v.getName().toString() ;
                System.out.println("Var = "+vn) ;
            }

            Results results = qr.getSparql().getResults() ;
            Result[] r = results.getResult() ;
            for ( int i = 0 ; i < r.length; i++ )
            {
                Result result = r[i] ;
                Binding[] bindings = result.getBinding() ;
                for ( int j = 0 ; j < bindings.length; j++ )
                {
                    Binding bind = bindings[j] ;
                    String vn = bind.getName().toString() ;
                    System.out.print("Binding: ("+vn+" = ") ;
                    String bLab = bind.getBnode() ;
                    if ( bLab != null )
                        System.out.print("_:"+bLab) ;
                    String uri = bind.getUri() ;
                    if ( uri != null )
                        System.out.print("<"+uri+">") ;
                    System.out.println(")") ;
                }
            }
            
            
            
            //String ret = (String) call.invoke( new Object[] { "Hello!" } );
        } catch (AxisFault ex)
        {
            System.err.println(ex.getFaultReason()) ;
            
            System.err.println(ex.getMessage()) ;
            //ex.printStackTrace(System.err) ;
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage()) ;
            //ex.printStackTrace(System.err) ;
        }
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