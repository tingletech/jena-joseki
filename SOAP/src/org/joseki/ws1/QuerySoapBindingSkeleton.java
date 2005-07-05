/**
 * QuerySoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.joseki.ws1;

public class QuerySoapBindingSkeleton implements org.joseki.ws1.QueryType, org.apache.axis.wsdl.Skeleton {
    private org.joseki.ws1.QueryType impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "query"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("query", _params, new javax.xml.namespace.QName("", "response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "query"));
        _myOperationsList.add(_oper);
        if (_myOperations.get("query") == null) {
            _myOperations.put("query", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("query")).add(_oper);
    }

    public QuerySoapBindingSkeleton() {
        this.impl = new org.joseki.ws1.QuerySoapBindingImpl();
    }

    public QuerySoapBindingSkeleton(org.joseki.ws1.QueryType impl) {
        this.impl = impl;
    }
    public java.lang.String query(java.lang.String query) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.query(query);
        return ret;
    }

}
