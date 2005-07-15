/**
 * QueryFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.w3.www._2001.sw.DataAccess.sparql_protocol_types;

public class QueryFault  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    private int queryFaultCode;
    private java.lang.String queryFaultMessage;

    public QueryFault() {
    }

    public QueryFault(
           int queryFaultCode,
           java.lang.String queryFaultMessage) {
        this.queryFaultCode = queryFaultCode;
        this.queryFaultMessage = queryFaultMessage;
    }


    /**
     * Gets the queryFaultCode value for this QueryFault.
     * 
     * @return queryFaultCode
     */
    public int getQueryFaultCode() {
        return queryFaultCode;
    }


    /**
     * Sets the queryFaultCode value for this QueryFault.
     * 
     * @param queryFaultCode
     */
    public void setQueryFaultCode(int queryFaultCode) {
        this.queryFaultCode = queryFaultCode;
    }


    /**
     * Gets the queryFaultMessage value for this QueryFault.
     * 
     * @return queryFaultMessage
     */
    public java.lang.String getQueryFaultMessage() {
        return queryFaultMessage;
    }


    /**
     * Sets the queryFaultMessage value for this QueryFault.
     * 
     * @param queryFaultMessage
     */
    public void setQueryFaultMessage(java.lang.String queryFaultMessage) {
        this.queryFaultMessage = queryFaultMessage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QueryFault)) return false;
        QueryFault other = (QueryFault) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.queryFaultCode == other.getQueryFaultCode() &&
            ((this.queryFaultMessage==null && other.getQueryFaultMessage()==null) || 
             (this.queryFaultMessage!=null &&
              this.queryFaultMessage.equals(other.getQueryFaultMessage())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getQueryFaultCode();
        if (getQueryFaultMessage() != null) {
            _hashCode += getQueryFaultMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(QueryFault.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/sparql-protocol-types", ">query-fault"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryFaultCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "query-fault-code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryFaultMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "query-fault-message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }


    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, this);
    }
}
