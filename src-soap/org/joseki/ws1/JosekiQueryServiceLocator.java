/**
 * JosekiQueryServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.joseki.ws1;

public class JosekiQueryServiceLocator extends org.apache.axis.client.Service implements org.joseki.ws1.JosekiQueryService {

    public JosekiQueryServiceLocator() {
    }


    public JosekiQueryServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public JosekiQueryServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SparqlQuery
    private java.lang.String SparqlQuery_address = "http://localhost:2525/axis/services/sparql-query";

    public java.lang.String getSparqlQueryAddress() {
        return SparqlQuery_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SparqlQueryWSDDServiceName = "sparql-query";

    public java.lang.String getSparqlQueryWSDDServiceName() {
        return SparqlQueryWSDDServiceName;
    }

    public void setSparqlQueryWSDDServiceName(java.lang.String name) {
        SparqlQueryWSDDServiceName = name;
    }

    public org.joseki.ws1.SparqlQuery getSparqlQuery() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SparqlQuery_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSparqlQuery(endpoint);
    }

    public org.joseki.ws1.SparqlQuery getSparqlQuery(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.joseki.ws1.QuerySoapBindingStub _stub = new org.joseki.ws1.QuerySoapBindingStub(portAddress, this);
            _stub.setPortName(getSparqlQueryWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSparqlQueryEndpointAddress(java.lang.String address) {
        SparqlQuery_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.joseki.ws1.SparqlQuery.class.isAssignableFrom(serviceEndpointInterface)) {
                org.joseki.ws1.QuerySoapBindingStub _stub = new org.joseki.ws1.QuerySoapBindingStub(new java.net.URL(SparqlQuery_address), this);
                _stub.setPortName(getSparqlQueryWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("sparql-query".equals(inputPortName)) {
            return getSparqlQuery();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.w3.org/2005/08/sparql-protocol-query/#", "JosekiQueryService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.w3.org/2005/08/sparql-protocol-query/#", "sparql-query"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SparqlQuery".equals(portName)) {
            setSparqlQueryEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
