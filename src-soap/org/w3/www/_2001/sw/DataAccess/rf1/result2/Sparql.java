/**
 * Sparql.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.w3.www._2001.sw.DataAccess.rf1.result2;

public class Sparql  implements java.io.Serializable {
    private org.w3.www._2001.sw.DataAccess.rf1.result2.Head head;
    private org.w3.www._2001.sw.DataAccess.rf1.result2.Results results;
    private java.lang.Boolean _boolean;

    public Sparql() {
    }

    public Sparql(
           org.w3.www._2001.sw.DataAccess.rf1.result2.Head head,
           org.w3.www._2001.sw.DataAccess.rf1.result2.Results results,
           java.lang.Boolean _boolean) {
           this.head = head;
           this.results = results;
           this._boolean = _boolean;
    }


    /**
     * Gets the head value for this Sparql.
     * 
     * @return head
     */
    public org.w3.www._2001.sw.DataAccess.rf1.result2.Head getHead() {
        return head;
    }


    /**
     * Sets the head value for this Sparql.
     * 
     * @param head
     */
    public void setHead(org.w3.www._2001.sw.DataAccess.rf1.result2.Head head) {
        this.head = head;
    }


    /**
     * Gets the results value for this Sparql.
     * 
     * @return results
     */
    public org.w3.www._2001.sw.DataAccess.rf1.result2.Results getResults() {
        return results;
    }


    /**
     * Sets the results value for this Sparql.
     * 
     * @param results
     */
    public void setResults(org.w3.www._2001.sw.DataAccess.rf1.result2.Results results) {
        this.results = results;
    }


    /**
     * Gets the _boolean value for this Sparql.
     * 
     * @return _boolean
     */
    public java.lang.Boolean get_boolean() {
        return _boolean;
    }


    /**
     * Sets the _boolean value for this Sparql.
     * 
     * @param _boolean
     */
    public void set_boolean(java.lang.Boolean _boolean) {
        this._boolean = _boolean;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Sparql)) return false;
        Sparql other = (Sparql) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.head==null && other.getHead()==null) || 
             (this.head!=null &&
              this.head.equals(other.getHead()))) &&
            ((this.results==null && other.getResults()==null) || 
             (this.results!=null &&
              this.results.equals(other.getResults()))) &&
            ((this._boolean==null && other.get_boolean()==null) || 
             (this._boolean!=null &&
              this._boolean.equals(other.get_boolean())));
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
        if (getHead() != null) {
            _hashCode += getHead().hashCode();
        }
        if (getResults() != null) {
            _hashCode += getResults().hashCode();
        }
        if (get_boolean() != null) {
            _hashCode += get_boolean().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Sparql.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", ">sparql"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("head");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "head"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "head"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("results");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "results"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "results"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_boolean");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "boolean"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/rf1/result2", "boolean"));
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
