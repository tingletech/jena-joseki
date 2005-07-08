/**
 * QuerySoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.joseki.ws1;
//import org.apache.axis.message.MessageElement;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NMToken;
import org.apache.axis.types.URI;
import org.w3.www._2001.sw.DataAccess.rf1.result2.*;
import org.w3.www._2001.sw.DataAccess.sparql_protocol_types.* ;

public class QuerySoapBindingImpl implements org.joseki.ws1.QueryType
{
    public QueryResult query(Query request) throws java.rmi.RemoteException
	{
        try {
    		System.out.println("Hello") ;
            String queryString = request.getSparqlQuery() ;
    
            System.out.println("Query string: "+stringOrNull(queryString)) ;
            
            URI uri = request.getDefaultGraphUri() ;
            
            System.out.println("Default Graph: "+stringOrNull(uri)) ;
            
            URI[] names = request.getNamedGraphUri() ;
            if ( names == null )
                System.out.println("No named graphs") ;
            else
            {
                for ( int i = 0 ; i < names.length ; i++ )
                {
                    URI u = names[i] ; 
                    System.out.println("Named graph: "+stringOrNull(u)) ;
                }
            }
            
            if ( false )
            {
                //RDF/XML
                // Try custom serialization as well??
               
                MessageElement m = new MessageElement() ;
                
            }
            
            // A result
            QueryResult result = new QueryResult() ;
            Sparql r = new Sparql();
            Results xmlResults = new Results() ;
            Result soln = new Result() ;

            Binding b1 = new Binding() ;
            b1.setName(new NMToken("x")) ;
            b1.setUri("http://example.org/#") ;

            Binding b2 = new Binding() ;
            b2.setName(new NMToken("y")) ;
            b2.setBnode("BNODE") ;
            
//            Literal lit = new Literal() ;
//            MessageElement mElt = new MessageElement() ;
//            mElt.setValue("I'm a literal") ;
//            lit.set_any(new MessageElement[]{mElt}) ;
//            b2.setLiteral(lit) ;
//            
            soln.setBinding(new Binding[]{b1, b2}) ;
            xmlResults.setResult(new Result[]{soln}) ;
            r.setResults(xmlResults) ;
            result.setSparql(r) ;
            
            Head h = new Head() ;
            Variable v1 = new Variable() ; 
            v1.setName(new NMToken("x")) ;
            Variable v2 = new Variable() ;
            v2.setName(new NMToken("y")) ;
            h.setVariable(new Variable[]{v1, v2}) ;
            r.setHead(h) ;
            return result ;
            
            
        } catch (RuntimeException ex)
        {
            System.err.println(ex.getMessage()) ;
            ex.printStackTrace(System.err) ;
            throw ex ;
        }
    }
    
    private static String stringOrNull(Object s)
    {
        if ( s == null )
            return "<<null>>" ;
        return s.toString() ;
    }
}
