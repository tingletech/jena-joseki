/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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
import org.joseki.ReturnCodes;
import org.joseki.processors.SPARQL;

import org.w3.www._2001.sw.DataAccess.sparql_protocol_types.* ;


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

    public QueryResult query(Query request) throws java.rmi.RemoteException
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
            Request serviceRequest = new Request(serviceURI) ;

            // ---- Query
            
            String queryString = request.getSparqlQuery() ;
            
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
            catch (QueryFault f) { throw f ; }  
            catch (Exception ex)
            {
                log.warn("Internal server error", ex) ;
                throw new QueryFault(ReturnCodes.rcInternalError, "Internal server error") ;
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
        catch (QueryFault ex) { throw ex ; }
        catch (SOAPException ex)
        {
            System.err.println("SOAP: "+ex.getMessage()) ;
            ex.printStackTrace(System.err) ;
            throw new RuntimeException("SOAP", ex) ;
        }
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

//      // To go.
//      public QueryResult query2(Query request) throws java.rmi.RemoteException
//      {
//          if ( log.isDebugEnabled() )
//              log.debug("SOAP request received") ;
//          try {
//              
//              // Axis has already parsed the message into nice Java datastructures.  
//              MessageContext cxt = MessageContext.getCurrentContext() ;
//              cxt.setProperty("disablePrettyXML", new Boolean(false)) ;
//              
//              if ( true )
//              {
//                  // Print incoming.
//                  SOAPMessage msg = cxt.getMessage() ;
//                  
//                  SOAPBody b = (SOAPBody)msg.getSOAPBody() ;
//                  String s = SOAPUtils.elementAsString(cxt, b) ;
//                  log.info("\n"+s) ;
//              }
  //
//              SOAPService srv = cxt.getService() ;
//              
////              System.out.println("ALL") ;
////              for ( Iterator iter = cxt.getAllPropertyNames() ; iter.hasNext() ; )
////              {
////                  String p = (String)iter.next() ; 
////                  Object v = cxt.getProperty(p) ;
////                  System.out.println(p+" = "+v) ; 
////              }
////              System.out.println("NAMES") ;
////              for ( Iterator iter = cxt.getPropertyNames() ; iter.hasNext() ; )
////              {
////                  String p = (String)iter.next() ; 
////                  Object v = cxt.getProperty(p) ;
////                  System.out.println(p+" = "+v) ; 
////              }
//              
  //
//              String target = cxt.getTargetService() ;
//              
//              String url = (String)cxt.getProperty("transport.url") ;
//              String path = (String)cxt.getProperty("path") ;
//              String realpath = (String)cxt.getProperty("realpath") ;
//              
//              int ind = path.lastIndexOf('/') ;
//              // Works if i = -1.
//              String serviceURI = path.substring(ind+1) ;
//              Request serviceRequest = new Request(serviceURI) ;
  //
//              // ---- Query
//              
//              String queryString = request.getSparqlQuery() ;
//              
//              if ( log.isDebugEnabled() )
//                  log.debug("Query string: "+stringOrNull(queryString)) ;
//             
//              serviceRequest.setParam(SPARQL.P_QUERY, queryString) ;
//              
//              // ---- Default graph
//              
//              URI uri = request.getDefaultGraphUri() ;
//              
//              if ( log.isDebugEnabled() )
//                  log.debug("Default Graph: "+stringOrNull(uri)) ;
//              
//              serviceRequest.setParam(SPARQL.P_DEFAULT_GRAPH, queryString) ;
//              
//              // ---- Named graphs
//              URI[] names = request.getNamedGraphUri() ;
//              if ( names == null )
//              {
//                  if ( log.isDebugEnabled() )
//                      log.debug("No named graphs") ;
//              }
//              else
//              {
//                  for ( int i = 0 ; i < names.length ; i++ )
//                  {
//                      URI u = names[i] ;
//                      if ( log.isDebugEnabled() )
//                          log.debug("Named graph: "+stringOrNull(u)) ;
//                      serviceRequest.setParam(SPARQL.P_NAMED_GRAPH, queryString) ;
//                  }
//              }
  //
//              // ---- Response
//              
//              ResponseSOAP serviceResponse = new ResponseSOAP(serviceRequest) ;
//              if ( false  )
//              {
//                  log.info("Sending exception") ;
//                  throw new QueryFault(ReturnCodes.rcInternalError, "Internal server error") ;
//              }
//              if ( false )
//              {
//                  try {
//                      Dispatcher.dispatch(serviceURI, serviceRequest, serviceResponse) ;
//                  }
//                  catch (Exception ex)
//                  {
//                      log.warn("Internal server error", ex) ;
//                      QName faultCode = null ;
//                      throw new QueryFault(ReturnCodes.rcInternalError, "Internal server error") ;
////                      String faultString = null ;
////                      String faultActor = null ;
////                      Detail detail = null ;
////                      throw new SOAPFaultException(faultCode,
////                                                   faultString,
////                                                   faultActor,
////                                                   detail) ;
//                  }     
//              }
//              
//              
//              // A result
//              QueryResult result = new QueryResult() ;
  //
//              if ( false )
//              {
//                  // Custom type
//                  Model model = ModelFactory.createDefaultModel() ;
//                  Resource r = model.createResource("http://example.org/r1") ;
//                  Property p = model.createProperty("http://example.org/r1") ;
//                  model.add(r, p, "value") ;
//                  result.setRDF(model) ;
//                  return result ;
//              }
//              
//              Sparql r = new Sparql();
//              Results xmlResults = new Results() ;
//              Result soln = new Result() ;
  //
//              Binding b1 = new Binding() ;
//              b1.setName(new NMToken("x")) ;
//              b1.setUri("http://example.org/#") ;
  //
//              Binding b2 = new Binding() ;
//              b2.setName(new NMToken("y")) ;
////              b2.setBnode("BNODE") ;
//              
//              // May need custom serializer :-(
//              Literal lit = new Literal() ;
//              Text txt = new Text("I'm a literal") ;
//              MessageElement mElt = new MessageElement(txt) ;
////              AttributesImpl a = new AttributesImpl() ;
////              a.addAttribute("", "", "xml:lang", "CDATA", "en") ;
////              mElt.setAllAttributes(a) ;
//              lit.set_any(new MessageElement[]{mElt}) ;
//              b2.setLiteral(lit) ;
//              
//              
//              soln.setBinding(new Binding[]{b1, b2}) ;
//              xmlResults.setResult(new Result[]{soln}) ;
//              r.setResults(xmlResults) ;
//              result.setSparql(r) ;
//              
//              Head h = new Head() ;
//              Variable v1 = new Variable() ; 
//              v1.setName(new NMToken("x")) ;
//              Variable v2 = new Variable() ;
//              v2.setName(new NMToken("y")) ;
//              h.setVariable(new Variable[]{v1, v2}) ;
//              r.setHead(h) ;
//              return result ;
//          }
//          
//          // Pass out exceptions that are supposed to be generated 
//          catch (QueryFault ex) { throw ex ; }
//          catch (SOAPException ex)
//          {
//              System.err.println("SOAP: "+ex.getMessage()) ;
//              ex.printStackTrace(System.err) ;
//              throw new RuntimeException("SOAP", ex) ;
//          }
//          catch (Exception ex)
//          {
//              System.err.println(ex.getMessage()) ;
//              ex.printStackTrace(System.err) ;
//              return null ;
//          }
//      }


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