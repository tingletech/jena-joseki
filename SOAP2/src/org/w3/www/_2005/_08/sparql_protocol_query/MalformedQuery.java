/**
 * MalformedQuery.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:47 LKT)
 */
package org.w3.www._2005._08.sparql_protocol_query;

public class MalformedQuery extends java.lang.Exception {
    private org.w3.www._2005._09.sparql_protocol_types.MalformedQuery faultMessage;

    public MalformedQuery() {
        super("MalformedQuery");
    }

    public MalformedQuery(java.lang.String s) {
        super(s);
    }

    public MalformedQuery(java.lang.String s, java.lang.Throwable ex) {
        super(s, ex);
    }

    public void setFaultMessage(
        org.w3.www._2005._09.sparql_protocol_types.MalformedQuery msg) {
        faultMessage = msg;
    }

    public org.w3.www._2005._09.sparql_protocol_types.MalformedQuery getFaultMessage() {
        return faultMessage;
    }
}
