package dev;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

//import javax.xml.namespace.QName;

public class Client
{
	public static void main(String [] args)
	{
		try {
			String endpoint = 
				"http://localhost:8080/axis/services/WSQuery" ;
			
			Service  service = new Service();
			Call     call    = (Call) service.createCall();
			
			call.setTargetEndpointAddress( new java.net.URL(endpoint) );
			call.setOperationName(new QName("urn:sparql1", "WSquery") );
			
			//call.addParameter("testParam",
			//                  org.apache.axis.Constants.XSD_STRING,
			//                  javax.xml.rpc.ParameterMode.IN);
			//call.setReturnType(org.apache.axis.Constants.XSD_STRING);

			String queryString = "SELECT" ;
			String defaultGraphURI = "<default graph>" ;
			String[] namegraphURIs = new String[]{"<g1>", "<g2>"} ;
			Object[] args = 
				new Object[]{queryString, defaultGraphURI, namegraphURIs} ;

			String ret = (String)call.invoke(args) ;

           System.out.println("Sent , got '" + ret + "'");

       } catch (Exception e) {
           System.err.println(e.getMessage()) ;
		   e.printStacktrace(System.err) ;
       }
   }
}
