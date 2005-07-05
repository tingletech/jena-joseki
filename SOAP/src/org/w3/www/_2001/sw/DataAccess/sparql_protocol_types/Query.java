/**
 * Query.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.w3.www._2001.sw.DataAccess.sparql_protocol_types;

public class Query  implements java.io.Serializable {
    private java.lang.String sparqlQuery;
    private org.apache.axis.types.URI defaultGraphUri;
    private org.apache.axis.types.URI[] namedGraphUri;

    public Query() {
    }

    public Query(
           java.lang.String sparqlQuery,
           org.apache.axis.types.URI defaultGraphUri,
           org.apache.axis.types.URI[] namedGraphUri) {
           this.sparqlQuery = sparqlQuery;
           this.defaultGraphUri = defaultGraphUri;
           this.namedGraphUri = namedGraphUri;
    }


    /**
     * Gets the sparqlQuery value for this Query.
     * 
     * @return sparqlQuery
     */
    public java.lang.String getSparqlQuery() {
        return sparqlQuery;
    }


    /**
     * Sets the sparqlQuery value for this Query.
     * 
     * @param sparqlQuery
     */
    public void setSparqlQuery(java.lang.String sparqlQuery) {
        this.sparqlQuery = sparqlQuery;
    }


    /**
     * Gets the defaultGraphUri value for this Query.
     * 
     * @return defaultGraphUri
     */
    public org.apache.axis.types.URI getDefaultGraphUri() {
        return defaultGraphUri;
    }


    /**
     * Sets the defaultGraphUri value for this Query.
     * 
     * @param defaultGraphUri
     */
    public void setDefaultGraphUri(org.apache.axis.types.URI defaultGraphUri) {
        this.defaultGraphUri = defaultGraphUri;
    }


    /**
     * Gets the namedGraphUri value for this Query.
     * 
     * @return namedGraphUri
     */
    public org.apache.axis.types.URI[] getNamedGraphUri() {
        return namedGraphUri;
    }


    /**
     * Sets the namedGraphUri value for this Query.
     * 
     * @param namedGraphUri
     */
    public void setNamedGraphUri(org.apache.axis.types.URI[] namedGraphUri) {
        this.namedGraphUri = namedGraphUri;
    }

    public org.apache.axis.types.URI getNamedGraphUri(int i) {
        return this.namedGraphUri[i];
    }

    public void setNamedGraphUri(int i, org.apache.axis.types.URI _value) {
        this.namedGraphUri[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Query)) return false;
        Query other = (Query) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.sparqlQuery==null && other.getSparqlQuery()==null) || 
             (this.sparqlQuery!=null &&
              this.sparqlQuery.equals(other.getSparqlQuery()))) &&
            ((this.defaultGraphUri==null && other.getDefaultGraphUri()==null) || 
             (this.defaultGraphUri!=null &&
              this.defaultGraphUri.equals(other.getDefaultGraphUri()))) &&
            ((this.namedGraphUri==null && other.getNamedGraphUri()==null) || 
             (this.namedGraphUri!=null &&
              java.util.Arrays.equals(this.namedGraphUri, other.getNamedGraphUri())));
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
        if (getSparqlQuery() != null) {
            _hashCode += getSparqlQuery().hashCode();
        }
        if (getDefaultGraphUri() != null) {
            _hashCode += getDefaultGraphUri().hashCode();
        }
        if (getNamedGraphUri() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getNamedGraphUri());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getNamedGraphUri(), i);
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
        new org.apache.axis.description.TypeDesc(Query.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/sparql-protocol-types", "query"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sparqlQuery");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sparql-query"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("defaultGraphUri");
        elemField.setXmlName(new javax.xml.namespace.QName("", "default-graph-uri"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("namedGraphUri");
        elemField.setXmlName(new javax.xml.namespace.QName("", "named-graph-uri"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
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
