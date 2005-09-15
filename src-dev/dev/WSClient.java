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

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.SOAPBodyElement;
import org.joseki.soap.ResultSetAxis;
import org.joseki.soap.SOAPUtils;

import org.w3.www._2005.sparql_results.*;

import org.w3.www._2005._09.sparql_protocol_types.MalformedQuery;
import org.w3.www._2005._09.sparql_protocol_types.QueryRequestRefused;

public class WSClient
{
    static String now = null ;
    static {
        String fmt = "HH:mm:ss" ;
        
        SimpleDateFormat dFmt = new SimpleDateFormat(fmt) ;
        now = dFmt.format(new Date()) ;
    }
    
    
    
    
    // ============ OLD CODE ==============================
    
    private static void processResultSet(Sparql resultSet)
    {
        ResultSet rs = new ResultSetAxis(resultSet) ;
        ResultSetFormatter.out(System.out, rs) ;
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
                Object ret = call.invoke(new Object[]{bodyElt}) ;
                //System.out.println("Return:("+ret.getClass().getSimpleName()+")"+ret) ;
                Object obj = ((Vector)ret).get(0) ;
                //System.out.println("Return1:("+obj.getClass().getSimpleName()+")"+obj) ;
                RPCElement e = (RPCElement)obj ;
                msgContext.setProperty("disablePrettyXML", new Boolean(false)) ;
                System.out.println(SOAPUtils.elementAsString(msgContext, e)) ;
    
                //e.deserialize() ;
                // Not sure where the deserialized version goes to
                
            }
            catch (MalformedQuery ex)
            {
                System.err.println("MalformedQuery : "+ex.getFaultDetails1()) ;
    
            }
            catch (QueryRequestRefused ex)
            {
                System.err.println("QueryRequestRefused : "+ex.getFaultDetails1()) ;
    
            }
             catch (Exception ex)
             {
                 System.err.println(ex.getMessage()) ;
                 ex.printStackTrace(System.err) ;
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