/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:58 LKT)
 */
package org.w3.www._2005.sparql_results;


/**
 *  ExtensionMapper class
 */
public class ExtensionMapper {
    public static java.lang.Object getTypeObject(
        java.lang.String namespaceURI, java.lang.String typeName,
        javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
        if ("http://www.w3.org/XML/1998/namespace".equals(namespaceURI) &&
                "lang_type0".equals(typeName)) {
            return org.w3.www.xml._1998.namespace.Lang_type0.Factory.parse(reader);
        }

        if ("http://www.w3.org/2005/sparql-results#".equals(namespaceURI) &&
                "URI-reference".equals(typeName)) {
            return org.w3.www._2005.sparql_results.URIReference.Factory.parse(reader);
        }

        throw new org.apache.axis2.databinding.ADBException("Unsupported type " +
            namespaceURI + " " + typeName);
    }
}
