/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.xml.namespace.QName;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.types.URI;
import org.joseki.soap.GraphDeserializerFactory;
import org.joseki.soap.SOAPUtils;
import org.joseki.ws1.JosekiQueryServiceLocator;
import org.joseki.ws1.QueryType;

import org.w3.www._2001.sw.DataAccess.rf1.result2.*;

import org.w3.www._2001.sw.DataAccess.sparql_protocol_types.Query;
import org.w3.www._2001.sw.DataAccess.sparql_protocol_types.QueryFault;
import org.w3.www._2001.sw.DataAccess.sparql_protocol_types.QueryResult;

public class WSClient
{
    static String now = null ;
    static {
        String fmt = "HH:mm:ss" ;
        
        SimpleDateFormat dFmt = new SimpleDateFormat(fmt) ;
        now = dFmt.format(new Date()) ;
    }
    
    public static void main(String[] args)
    {
        clientRaw() ;
    }
    
    public static void clientRaw()
    {
        try {
            String endpoint = "http://localhost:2525/axis/services/sparql-query" ;
            Service  service = new Service();
            Call call = (Call) service.createCall();
            MessageContext msgContext = call.getMessageContext() ;
            
            call.setTargetEndpointAddress( new java.net.URL(endpoint) );
            call.setOperationName(new QName("", "query")) ;
            
            QName op = new QName("http://www.w3.org/2001/sw/DataAccess/sparql-protocol-types",
                                 "query",  
                                 "sparql") ;
            
            SOAPBodyElement bodyElt = new SOAPBodyElement(op) ;
            bodyElt.addNamespaceDeclaration("st",
                                         "http://www.w3.org/2001/sw/DataAccess/sparql-protocol-types") ;
//            MessageElement mElt = new MessageElement(new QName("http://www.w3.org/2001/sw/DataAccess/sparql-protocol-types",
//                                                               "sparql-query")) ;
            MessageElement mElt = new MessageElement(new QName("sparql-query")) ;
            mElt.addTextNode("SELECT -- RAW -- "+now) ;
            bodyElt.addChild(mElt) ;

            String tmp = SOAPUtils.elementAsString(msgContext, bodyElt) ;
            System.out.println(tmp) ; 
            
            System.out.println() ;
            Object ret = (Object) call.invoke(new Object[]{bodyElt}) ;
            //System.out.println("Return:("+ret.getClass().getSimpleName()+")"+ret) ;
            Object obj = ((Vector)ret).get(0) ;
            //System.out.println("Return1:("+obj.getClass().getSimpleName()+")"+obj) ;
            RPCElement e = (RPCElement)obj ;
            msgContext.setProperty("disablePrettyXML", new Boolean(false)) ;
            System.out.println(SOAPUtils.elementAsString(msgContext, e)) ;

            //e.deserialize() ;
            // Not sure where the deserialized version goes to
            
        }
        catch (QueryFault ex)
        {
            System.err.println("RC = "+ex.getQueryFaultCode()+" "+ex.getQueryFaultMessage()) ;

        }
         catch (Exception ex)
         {
             System.err.println(ex.getMessage()) ;
             ex.printStackTrace(System.err) ;
         }
    }

    public static void clientOM()
    {
        try {
//            HappyClient hc = new HappyClient(System.out) ;
//            hc.verifyClientIsHappy(false) ;

            // Install deserialier for rdf:RDF
            String endpoint = "http://localhost:2525/axis/services/sparql-query" ;
            //String endpoint = "http://localhost:2020/dump-body" ;
            JosekiQueryServiceLocator  service = new JosekiQueryServiceLocator();
            service.setSparqlQueryEndpointAddress(endpoint) ;
            
            TypeMappingRegistry reg = service.getEngine().getTypeMappingRegistry() ;
            TypeMapping tm = (TypeMapping)reg.getTypeMapping("") ;
            tm.register(Model.class,
                        new QName(RDF.getURI(), "RDF") ,
                        null, 
                        new GraphDeserializerFactory()) ;
            
            QueryType qt = service.getSparqlQuery() ;
            Query q = new Query() ;
            q.setSparqlQuery("SELECT ("+now+")") ;
            q.setDefaultGraphUri(new URI("http://default")) ;
            q.setNamedGraphUri(new URI[]{
                new URI("http://host/name1"),
                new URI("http://host/name2")
            }) ;
            // Do it.
            QueryResult qr = null ;
            
            try {
                qt.query(q) ;
            } catch (QueryFault ex)
            {
               System.err.println("RC = "+ex.getQueryFaultCode()+" "+ex.getQueryFaultMessage()) ;
               //System.err.println("RC = "+ex.getFaultCode()+" "+ex.getFaultString()) ;
               return ;
            }
            catch (AxisFault axisFault)
            {
                System.err.println("Axis fault: "+axisFault.getFaultCode()) ;
                System.err.println(axisFault.getFaultString()) ;
                return ;
            }
            

            
            if ( qr.getRDF() != null )
            {
                Model m = (Model)qr.getRDF() ;
                m.write(System.out, "N3") ;
                return ;
            }
            
            if ( qr.getSparql() != null )
            {
                processResultSet(qr.getSparql()) ;
                return ;
            }
            
            System.err.println("Unknown result element!!") ;
            
        } catch (AxisFault ex)
        {
            System.err.println(ex.getFaultReason()) ;
            
            System.err.println(ex.getMessage()) ;
            //ex.printStackTrace(System.err) ;
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage()) ;
            ex.printStackTrace(System.err) ;
        }
    }
        
        
    private static void processResultSet(Sparql resultSet)
    {
        Variable[] vars = resultSet.getHead().getVariable() ;

        for ( int i = 0 ; i < vars.length; i++ )
        {
            Variable v = vars[i] ; 
            String vn = v.getName().toString() ;
            System.out.println("Var = "+vn) ;
        }

        Results results = resultSet.getResults() ;
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