/**
 * Results.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.w3.www._2001.sw.DataAccess.rf1.result2;

public class Results  implements java.io.Serializable {
    private java.lang.Boolean _boolean;
    private org.w3.www._2001.sw.DataAccess.rf1.result2.Result[] result;

    public Results() {
    }

    public Results(
           java.lang.Boolean _boolean,
           org.w3.www._2001.sw.DataAccess.rf1.result2.Result[] result) {
           this._boolean = _boolean;
           this.result = result;
    }


    /**
     * Gets the _boolean value for this Results.
     * 
     * @return _boolean
     */
    public java.lang.Boolean get_boolean() {
        return _boolean;
    }


    /**
     * Sets the _boolean value for this Results.
     * 
     * @param _boolean
     */
    public void set_boolean(java.lang.Boolean _boolean) {
        this._boolean = _boolean;
    }


    /**
     * Gets the result value for this Results.
     * 
     * @return result
     */
    public org.w3.www._2001.sw.DataAccess.rf1.result2.Result[] getResult() {
        return result;
    }


    /**
     * Sets the result value for this Results.
     * 
     * @param result
     */
    public void setResult(org.w3.www._2001.sw.DataAccess.rf1.result2.Result[] result) {
        this.result = result;
    }

    public org.w3.www._2001.sw.DataAccess.rf1.result2.Result getResult(int i) {
        return this.result[i];
    }

    public void setResult(int i, org.w3.www._2001.sw.DataAccess.rf1.result2.Result _value) {
        this.result[i] = _value;
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
            ((this._boolean==null && other.get_boolean()==null) || 
             (this._boolean!=null &&
              this._boolean.equals(other.get_boolean()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              java.util.Arrays.equals(this.result, other.getResult())));
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
        if (get_boolean() != null) {
            _hashCode += get_boolean().hashCode();
        }
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Results.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", ">results"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_boolean");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "boolean"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "result"));
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
