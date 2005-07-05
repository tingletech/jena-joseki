package dev;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;

public class Client
{
	public static void main(String [] args)
	{
		try {
			String endpoint = 
				"http://localhost:2525/axis/services/WSQuery" ;
			
			Service  service = new Service();
			Call     call    = (Call) service.createCall();
			
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );

			// Method name
			call.setOperationName(new QName("urn:sparql1", 
											"query") );
			
			//call.addParameter("testParam",
			//                  org.apache.axis.Constants.XSD_STRING,
			//                  javax.xml.rpc.ParameterMode.IN);
			//call.setReturnType(org.apache.axis.Constants.XSD_STRING);

			String queryString = "SELECT" ;
			String defaultGraphURI = "<default graph>" ;
			String[] namegraphURIs = new String[]{"<g1>", "<g2>"} ;
			Object[] ws_args = 
				new Object[]{queryString, defaultGraphURI, namegraphURIs} ;

			String ret = (String)call.invoke(ws_args) ;

           System.out.println("Sent , got '" + ret + "'");

       } catch (Exception e) {
           System.err.println(e.getMessage()) ;
		   e.printStackTrace(System.err) ;
       }
   }
}
