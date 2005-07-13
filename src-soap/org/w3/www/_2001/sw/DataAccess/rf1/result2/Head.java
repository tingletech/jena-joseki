/**
 * Head.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.w3.www._2001.sw.DataAccess.rf1.result2;

public class Head  implements java.io.Serializable {
    private org.w3.www._2001.sw.DataAccess.rf1.result2.Variable[] variable;
    private org.w3.www._2001.sw.DataAccess.rf1.result2.Link link;

    public Head() {
    }

    public Head(
           org.w3.www._2001.sw.DataAccess.rf1.result2.Variable[] variable,
           org.w3.www._2001.sw.DataAccess.rf1.result2.Link link) {
           this.variable = variable;
           this.link = link;
    }


    /**
     * Gets the variable value for this Head.
     * 
     * @return variable
     */
    public org.w3.www._2001.sw.DataAccess.rf1.result2.Variable[] getVariable() {
        return variable;
    }


    /**
     * Sets the variable value for this Head.
     * 
     * @param variable
     */
    public void setVariable(org.w3.www._2001.sw.DataAccess.rf1.result2.Variable[] variable) {
        this.variable = variable;
    }

    public org.w3.www._2001.sw.DataAccess.rf1.result2.Variable getVariable(int i) {
        return this.variable[i];
    }

    public void setVariable(int i, org.w3.www._2001.sw.DataAccess.rf1.result2.Variable _value) {
        this.variable[i] = _value;
    }


    /**
     * Gets the link value for this Head.
     * 
     * @return link
     */
    public org.w3.www._2001.sw.DataAccess.rf1.result2.Link getLink() {
        return link;
    }


    /**
     * Sets the link value for this Head.
     * 
     * @param link
     */
    public void setLink(org.w3.www._2001.sw.DataAccess.rf1.result2.Link link) {
        this.link = link;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Head)) return false;
        Head other = (Head) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.variable==null && other.getVariable()==null) || 
             (this.variable!=null &&
              java.util.Arrays.equals(this.variable, other.getVariable()))) &&
            ((this.link==null && other.getLink()==null) || 
             (this.link!=null &&
              this.link.equals(other.getLink())));
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
        if (getVariable() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVariable());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVariable(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLink() != null) {
            _hashCode += getLink().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Head.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", ">head"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("variable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "variable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "variable"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("link");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "link"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "link"));
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

}
