/**
 * WSQuerySoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.joseki.ws;

public class WSQuerySoapBindingSkeleton implements org.joseki.ws.JosekiQuery, org.apache.axis.wsdl.Skeleton {
    private org.joseki.ws.JosekiQuery impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:sparql1", "ArrayOf_soapenc_string"), java.lang.String[].class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("query", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("urn:sparql1", "query"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("query") == null) {
            _myOperations.put("query", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("query")).add(_oper);
    }

    public WSQuerySoapBindingSkeleton() {
        this.impl = new org.joseki.ws.WSQuerySoapBindingImpl();
    }

    public WSQuerySoapBindingSkeleton(org.joseki.ws.JosekiQuery impl) {
        this.impl = impl;
    }
    public void query(java.lang.String in0, java.lang.String in1, java.lang.String[] in2) throws java.rmi.RemoteException
    {
        impl.query(in0, in1, in2);
    }

}
