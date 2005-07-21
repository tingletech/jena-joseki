/**
 * Binding.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.w3.www._2005._06.sparqlResults;

public class Binding  implements java.io.Serializable {
    private java.lang.String uri;
    private java.lang.String bnode;
    private org.w3.www._2005._06.sparqlResults.Literal literal;
    private org.w3.www._2005._06.sparqlResults.Unbound unbound;
    private org.apache.axis.types.NMToken name;  // attribute

    public Binding() {
    }

    public Binding(
           java.lang.String uri,
           java.lang.String bnode,
           org.w3.www._2005._06.sparqlResults.Literal literal,
           org.w3.www._2005._06.sparqlResults.Unbound unbound,
           org.apache.axis.types.NMToken name) {
           this.uri = uri;
           this.bnode = bnode;
           this.literal = literal;
           this.unbound = unbound;
           this.name = name;
    }


    /**
     * Gets the uri value for this Binding.
     * 
     * @return uri
     */
    public java.lang.String getUri() {
        return uri;
    }


    /**
     * Sets the uri value for this Binding.
     * 
     * @param uri
     */
    public void setUri(java.lang.String uri) {
        this.uri = uri;
    }


    /**
     * Gets the bnode value for this Binding.
     * 
     * @return bnode
     */
    public java.lang.String getBnode() {
        return bnode;
    }


    /**
     * Sets the bnode value for this Binding.
     * 
     * @param bnode
     */
    public void setBnode(java.lang.String bnode) {
        this.bnode = bnode;
    }


    /**
     * Gets the literal value for this Binding.
     * 
     * @return literal
     */
    public org.w3.www._2005._06.sparqlResults.Literal getLiteral() {
        return literal;
    }


    /**
     * Sets the literal value for this Binding.
     * 
     * @param literal
     */
    public void setLiteral(org.w3.www._2005._06.sparqlResults.Literal literal) {
        this.literal = literal;
    }


    /**
     * Gets the unbound value for this Binding.
     * 
     * @return unbound
     */
    public org.w3.www._2005._06.sparqlResults.Unbound getUnbound() {
        return unbound;
    }


    /**
     * Sets the unbound value for this Binding.
     * 
     * @param unbound
     */
    public void setUnbound(org.w3.www._2005._06.sparqlResults.Unbound unbound) {
        this.unbound = unbound;
    }


    /**
     * Gets the name value for this Binding.
     * 
     * @return name
     */
    public org.apache.axis.types.NMToken getName() {
        return name;
    }


    /**
     * Sets the name value for this Binding.
     * 
     * @param name
     */
    public void setName(org.apache.axis.types.NMToken name) {
        this.name = name;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Binding)) return false;
        Binding other = (Binding) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.uri==null && other.getUri()==null) || 
             (this.uri!=null &&
              this.uri.equals(other.getUri()))) &&
            ((this.bnode==null && other.getBnode()==null) || 
             (this.bnode!=null &&
              this.bnode.equals(other.getBnode()))) &&
            ((this.literal==null && other.getLiteral()==null) || 
             (this.literal!=null &&
              this.literal.equals(other.getLiteral()))) &&
            ((this.unbound==null && other.getUnbound()==null) || 
             (this.unbound!=null &&
              this.unbound.equals(other.getUnbound()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName())));
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
        if (getUri() != null) {
            _hashCode += getUri().hashCode();
        }
        if (getBnode() != null) {
            _hashCode += getBnode().hashCode();
        }
        if (getLiteral() != null) {
            _hashCode += getLiteral().hashCode();
        }
        if (getUnbound() != null) {
            _hashCode += getUnbound().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Binding.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", ">binding"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("name");
        attrField.setXmlName(new javax.xml.namespace.QName("", "name"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "NMTOKEN"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uri");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", "uri"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", "uri"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bnode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", "bnode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", "bnode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("literal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", "literal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", "literal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unbound");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", "unbound"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", "unbound"));
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
