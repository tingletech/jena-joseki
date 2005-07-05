/**
 * WSQuerySoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.joseki.ws;

public class WSQuerySoapBindingImpl implements org.joseki.ws.JosekiQuery{
    public String query(String in0, 
						String in1,
						String[] in2)
		throws java.rmi.RemoteException
	{
		String queryString = in0 ;
		String defaultGraphURI = in1 ;
		String[] namedGraphURIs = in2 ;


		System.out.println("Query = "+queryString) ;
		System.out.println("Default graph URI = "+defaultGraphURI) ;
		for ( int i = 0 ; i < namedGraphURIs.length ; i++ )
			{
				System.out.println("Named graph URI = "+namedGraphURIs[i]) ;
			}
        return "JOSEKI" ;
    }

}
