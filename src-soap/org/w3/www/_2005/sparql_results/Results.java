/**
 * Results.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.w3.www._2005.sparql_results;

public class Results  implements java.io.Serializable {
    private org.w3.www._2005.sparql_results.Result[] result;
    private boolean ordered;  // attribute
    private boolean distinct;  // attribute

    public Results() {
    }

    public Results(
           org.w3.www._2005.sparql_results.Result[] result,
           boolean ordered,
           boolean distinct) {
           this.result = result;
           this.ordered = ordered;
           this.distinct = distinct;
    }


    /**
     * Gets the result value for this Results.
     * 
     * @return result
     */
    public org.w3.www._2005.sparql_results.Result[] getResult() {
        return result;
    }


    /**
     * Sets the result value for this Results.
     * 
     * @param result
     */
    public void setResult(org.w3.www._2005.sparql_results.Result[] result) {
        this.result = result;
    }

    public org.w3.www._2005.sparql_results.Result getResult(int i) {
        return this.result[i];
    }

    public void setResult(int i, org.w3.www._2005.sparql_results.Result _value) {
        this.result[i] = _value;
    }


    /**
     * Gets the ordered value for this Results.
     * 
     * @return ordered
     */
    public boolean isOrdered() {
        return ordered;
    }


    /**
     * Sets the ordered value for this Results.
     * 
     * @param ordered
     */
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }


    /**
     * Gets the distinct value for this Results.
     * 
     * @return distinct
     */
    public boolean isDistinct() {
        return distinct;
    }


    /**
     * Sets the distinct value for this Results.
     * 
     * @param distinct
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Results)) return false;
        Results other = (Results) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              java.util.Arrays.equals(this.result, other.getResult()))) &&
            this.ordered == other.isOrdered() &&
            this.distinct == other.isDistinct();
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
        if (getResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResult(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += (isOrdered() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isDistinct() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Results.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/sparql-results#", ">results"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("ordered");
        attrField.setXmlName(new javax.xml.namespace.QName("", "ordered"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("distinct");
        attrField.setXmlName(new javax.xml.namespace.QName("", "distinct"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2005/sparql-results#", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/sparql-results#", "result"));
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
