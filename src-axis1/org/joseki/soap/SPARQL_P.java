/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.soap;


import java.io.File;
import java.io.IOException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.types.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joseki.Dispatcher;
import org.joseki.Request;
import org.joseki.processors.SPARQL;

import org.w3.www._2005._09.sparql_protocol_types.* ;


/** The implementation of SPARQL protocol over SOAP */

public class SPARQL_P
{
    static private Log log = LogFactory.getLog(SPARQL_P.class) ; 
    
    static boolean initialized = false ;
    
        
        public static void init()
    {
        if ( initialized )
            return ;
        
        initialized = true ;
        File f = new File(".") ;
        // Because user.dir may have been changed. 
        log.info("File base: "+f.getAbsolutePath()) ;
        try{
            log.info("File base: "+f.getCanonicalPath()) ;
        } catch (IOException ex)
        {
            log.info("File base 9absolute path): "+f.getAbsolutePath()) ;
        }
        //log.info("user.dir = "+System.getProperty("user.dir")) ;
        
        // Initialize the service registry (ir it is not already initialized)
        Dispatcher.initServiceRegistry() ;
    }

    public QueryResult query(QueryRequest request) throws java.rmi.RemoteException
    {
        init() ;
        if ( log.isDebugEnabled() )
            log.debug("SOAP request received") ;
        try {
            // Axis has already parsed the message into nice Java datastructures.  
            MessageContext cxt = MessageContext.getCurrentContext() ;
            cxt.setProperty("disablePrettyXML", new Boolean(false)) ;
            
            if ( true )
            {
                // Print incoming.
                SOAPMessage msg = cxt.getMessage() ;
                
                SOAPBody b = (SOAPBody)msg.getSOAPBody() ;
                String s = SOAPUtils.elementAsString(cxt, b) ;
                log.info("\n"+s) ;
            }

//            SOAPService srv = cxt.getService() ;
            
//            String target = cxt.getTargetService() ;
//            String url = (String)cxt.getProperty("transport.url") ;
//            String realpath = (String)cxt.getProperty("realpath") ;
            
            String path = (String)cxt.getProperty("path") ;
            int ind = path.lastIndexOf('/') ;
            // Works if i = -1.
            String serviceURI = path.substring(ind+1) ;
            Request serviceRequest = new Request(serviceURI, null) ;

            // ---- Query
            
            String queryString = request.getQuery() ;
            
            if ( log.isDebugEnabled() )
                log.debug("Query string: "+stringOrNull(queryString)) ;
           
            serviceRequest.setParam(SPARQL.P_QUERY, queryString) ;
            
            // ---- Default graph
            
            URI[] dftURIs = request.getNamedGraphUri() ;
            if ( dftURIs == null )
            {
                if ( log.isDebugEnabled() )
                    log.debug("No default graphs") ;
                
            }
            else
            {
                for ( int i = 0 ; i < dftURIs.length ; i++ )
                {
                    URI u = dftURIs[i] ;
                    if ( log.isDebugEnabled() )
                        log.debug("Default graph: "+stringOrNull(u)) ;
                    serviceRequest.setParam(SPARQL.P_DEFAULT_GRAPH, queryString) ;
                }
            }
            
            
            // ---- Named graphs
            URI[] names = request.getNamedGraphUri() ;
            if ( names == null )
            {
                if ( log.isDebugEnabled() )
                    log.debug("No named graphs") ;
            }
            else
            {
                for ( int i = 0 ; i < names.length ; i++ )
                {
                    URI u = names[i] ;
                    if ( log.isDebugEnabled() )
                        log.debug("Named graph: "+stringOrNull(u)) ;
                    serviceRequest.setParam(SPARQL.P_NAMED_GRAPH, queryString) ;
                }
            }

            // ---- Response
            
            ResponseSOAP serviceResponse = new ResponseSOAP(serviceRequest) ;

            try {
                Dispatcher.dispatch(serviceURI, serviceRequest, serviceResponse) ;
                serviceResponse.execException() ;
                QueryResult r = serviceResponse.get() ;
                return r ;
            }
            catch (MalformedQuery f) { throw f ; }  
            catch (QueryRequestRefused f) { throw f ; }  
            catch (Exception ex)
            {
                log.warn("Internal server error", ex) ;
                throw new org.apache.axis.AxisFault("Internal server error") ;
//                    String faultString = null ;
//                    String faultActor = null ;
//                    Detail detail = null ;
//                    throw new SOAPFaultException(faultCode,
//                                                 faultString,
//                                                 faultActor,
//                                                 detail) ;
            }
        }
        
        // Pass out exceptions that are supposed to be generated 
        catch (MalformedQuery f) { throw f ; }
        catch (QueryRequestRefused f) { throw f ; }  
        catch (org.apache.axis.AxisFault ex) { throw ex ; }
        
        // Unexpected Axis exception
        catch (SOAPException ex)
        {
            System.err.println("SOAP: "+ex.getMessage()) ;
            ex.printStackTrace(System.err) ;
            throw new RuntimeException("SOAP", ex) ;
        }
        
        // Some unexpected problem
        catch (Exception ex)
        {
            System.err.println(ex.getMessage()) ;
            ex.printStackTrace(System.err) ;
            return null ;
        }
    }

        
    private static String stringOrNull(Object s)
    {
        if ( s == null )
            return "<<null>>" ;
        return s.toString() ;
    }
}

/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
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