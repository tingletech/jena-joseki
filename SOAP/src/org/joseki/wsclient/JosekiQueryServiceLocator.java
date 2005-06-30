/**
 * JosekiQueryServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.joseki.wsclient;

public class JosekiQueryServiceLocator extends org.apache.axis.client.Service implements org.joseki.wsclient.JosekiQueryService {

    public JosekiQueryServiceLocator() {
    }


    public JosekiQueryServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public JosekiQueryServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WSQuery
    private java.lang.String WSQuery_address = "http://localhost:8080/axis/services/WSQuery";

    public java.lang.String getWSQueryAddress() {
        return WSQuery_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WSQueryWSDDServiceName = "WSQuery";

    public java.lang.String getWSQueryWSDDServiceName() {
        return WSQueryWSDDServiceName;
    }

    public void setWSQueryWSDDServiceName(java.lang.String name) {
        WSQueryWSDDServiceName = name;
    }

    public org.joseki.wsclient.JosekiQuery getWSQuery() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WSQuery_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWSQuery(endpoint);
    }

    public org.joseki.wsclient.JosekiQuery getWSQuery(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.joseki.wsclient.WSQuerySoapBindingStub _stub = new org.joseki.wsclient.WSQuerySoapBindingStub(portAddress, this);
            _stub.setPortName(getWSQueryWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWSQueryEndpointAddress(java.lang.String address) {
        WSQuery_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.joseki.wsclient.JosekiQuery.class.isAssignableFrom(serviceEndpointInterface)) {
                org.joseki.wsclient.WSQuerySoapBindingStub _stub = new org.joseki.wsclient.WSQuerySoapBindingStub(new java.net.URL(WSQuery_address), this);
                _stub.setPortName(getWSQueryWSDDServiceName());
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
        if ("WSQuery".equals(inputPortName)) {
            return getWSQuery();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:sparql1", "JosekiQueryService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:sparql1", "WSQuery"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WSQuery".equals(portName)) {
            setWSQueryEndpointAddress(address);
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
