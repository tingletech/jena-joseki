/**
 * QueryResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.w3.www._2001.sw.DataAccess.sparql_protocol_types;

public class QueryResult  implements java.io.Serializable {
    private org.w3.www._2005._06.sparqlResults.Sparql sparql;
    private java.lang.Object RDF;

    public QueryResult() {
    }

    public QueryResult(
           org.w3.www._2005._06.sparqlResults.Sparql sparql,
           java.lang.Object RDF) {
           this.sparql = sparql;
           this.RDF = RDF;
    }


    /**
     * Gets the sparql value for this QueryResult.
     * 
     * @return sparql
     */
    public org.w3.www._2005._06.sparqlResults.Sparql getSparql() {
        return sparql;
    }


    /**
     * Sets the sparql value for this QueryResult.
     * 
     * @param sparql
     */
    public void setSparql(org.w3.www._2005._06.sparqlResults.Sparql sparql) {
        this.sparql = sparql;
    }


    /**
     * Gets the RDF value for this QueryResult.
     * 
     * @return RDF
     */
    public java.lang.Object getRDF() {
        return RDF;
    }


    /**
     * Sets the RDF value for this QueryResult.
     * 
     * @param RDF
     */
    public void setRDF(java.lang.Object RDF) {
        this.RDF = RDF;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QueryResult)) return false;
        QueryResult other = (QueryResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.sparql==null && other.getSparql()==null) || 
             (this.sparql!=null &&
              this.sparql.equals(other.getSparql()))) &&
            ((this.RDF==null && other.getRDF()==null) || 
             (this.RDF!=null &&
              this.RDF.equals(other.getRDF())));
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
        if (getSparql() != null) {
            _hashCode += getSparql().hashCode();
        }
        if (getRDF() != null) {
            _hashCode += getRDF().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(QueryResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/sw/DataAccess/sparql-protocol-types", ">query-result"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sparql");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", "sparql"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2005/06/sparqlResults", "sparql"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RDF");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF"));
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
