/**
 * JosekiQueryService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.joseki.ws;

public interface JosekiQueryService extends javax.xml.rpc.Service {
    public java.lang.String getWSQueryAddress();

    public org.joseki.ws.JosekiQuery getWSQuery() throws javax.xml.rpc.ServiceException;

    public org.joseki.ws.JosekiQuery getWSQuery(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
