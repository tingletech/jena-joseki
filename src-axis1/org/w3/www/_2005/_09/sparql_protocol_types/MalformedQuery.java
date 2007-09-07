/**
 * MalformedQuery.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.w3.www._2005._09.sparql_protocol_types;

public class MalformedQuery  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    private java.lang.String faultDetails1;

    public MalformedQuery() {
    }

    public MalformedQuery(
           java.lang.String faultDetails1) {
        this.faultDetails1 = faultDetails1;
    }


    /**
     * Gets the faultDetails1 value for this MalformedQuery.
     * 
     * @return faultDetails1
     */
    public java.lang.String getFaultDetails1() {
        return faultDetails1;
    }


    /**
     * Sets the faultDetails1 value for this MalformedQuery.
     * 
     * @param faultDetails1
     */
    public void setFaultDetails1(java.lang.String faultDetails1) {
        this.faultDetails1 = faultDetails1;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MalformedQuery)) return false;
        MalformedQuery other = (MalformedQuery) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.faultDetails1==null && other.getFaultDetails1()==null) || 
             (this.faultDetails1!=null &&
              this.faultDetails1.equals(other.getFaultDetails1())));
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
        if (getFaultDetails1() != null) {
            _hashCode += getFaultDetails1().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MalformedQuery.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/09/sparql-protocol-types/#", ">malformed-query"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("faultDetails1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2005/09/sparql-protocol-types/#", "fault-details"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/09/sparql-protocol-types/#", "fault-details"));
        elemField.setMinOccurs(0);
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
