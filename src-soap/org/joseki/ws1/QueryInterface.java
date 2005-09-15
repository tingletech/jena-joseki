/**
 * QueryInterface.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.joseki.ws1;

import org.w3.www._2005._09.sparql_protocol_types.MalformedQuery;
import org.w3.www._2005._09.sparql_protocol_types.QueryRequestRefused;

public interface QueryInterface extends java.rmi.Remote {
    public org.w3.www._2005._09.sparql_protocol_types.QueryResult query(org.w3.www._2005._09.sparql_protocol_types.QueryRequest query)
    throws java.rmi.RemoteException, MalformedQuery, QueryRequestRefused;
    
}
