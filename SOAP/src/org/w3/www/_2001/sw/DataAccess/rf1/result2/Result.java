/**
 * Result.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.w3.www._2001.sw.DataAccess.rf1.result2;

public class Result  implements java.io.Serializable {
    private org.w3.www._2001.sw.DataAccess.rf1.result2.Binding[] binding;
    private org.apache.axis.types.PositiveInteger index;  // attribute

    public Result() {
    }

    public Result(
           org.w3.www._2001.sw.DataAccess.rf1.result2.Binding[] binding,
           org.apache.axis.types.PositiveInteger index) {
           this.binding = binding;
           this.index = index;
    }


    /**
     * Gets the binding value for this Result.
     * 
     * @return binding
     */
    public org.w3.www._2001.sw.DataAccess.rf1.result2.Binding[] getBinding() {
        return binding;
    }


    /**
     * Sets the binding value for this Result.
     * 
     * @param binding
     */
    public void setBinding(org.w3.www._2001.sw.DataAccess.rf1.result2.Binding[] binding) {
        this.binding = binding;
    }

    public org.w3.www._2001.sw.DataAccess.rf1.result2.Binding getBinding(int i) {
        return this.binding[i];
    }

    public void setBinding(int i, org.w3.www._2001.sw.DataAccess.rf1.result2.Binding _value) {
        this.binding[i] = _value;
    }


    /**
     * Gets the index value for this Result.
     * 
     * @return index
     */
    public org.apache.axis.types.PositiveInteger getIndex() {
        return index;
    }


    /**
     * Sets the index value for this Result.
     * 
     * @param index
     */
    public void setIndex(org.apache.axis.types.PositiveInteger index) {
        this.index = index;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Result)) return false;
        Result other = (Result) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.binding==null && other.getBinding()==null) || 
             (this.binding!=null &&
              java.util.Arrays.equals(this.binding, other.getBinding()))) &&
            ((this.index==null && other.getIndex()==null) || 
             (this.index!=null &&
              this.index.equals(other.getIndex())));
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
        if (getBinding() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBinding());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBinding(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getIndex() != null) {
            _hashCode += getIndex().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Result.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", ">result"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("index");
        attrField.setXmlName(new javax.xml.namespace.QName("", "index"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "positiveInteger"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("binding");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "binding"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "binding"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
