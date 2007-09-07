/**
 * QueryRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:58 LKT)
 */
package org.w3.www._2005._09.sparql_protocol_types;


/**
 *  QueryRequest bean class
 */
public class QueryRequest implements org.apache.axis2.databinding.ADBBean {
    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("http://www.w3.org/2005/09/sparql-protocol-types/#",
            "query-request", "ns4");

    /**
     * field for Query
     */
    protected java.lang.String localQuery;

    /**
     * field for DefaultGraphUri
     * This was an Array!
     */
    protected org.apache.axis2.databinding.types.URI[] localDefaultGraphUri;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localDefaultGraphUriTracker = false;

    /**
     * field for NamedGraphUri
     * This was an Array!
     */
    protected org.apache.axis2.databinding.types.URI[] localNamedGraphUri;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localNamedGraphUriTracker = false;

    private static java.lang.String generatePrefix(java.lang.String namespace) {
        if (namespace.equals(
                    "http://www.w3.org/2005/09/sparql-protocol-types/#")) {
            return "ns4";
        }

        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public java.lang.String getQuery() {
        return localQuery;
    }

    /**
     * Auto generated setter method
     * @param param Query
     */
    public void setQuery(java.lang.String param) {
        this.localQuery = param;
    }

    /**
     * Auto generated getter method
     * @return org.apache.axis2.databinding.types.URI[]
     */
    public org.apache.axis2.databinding.types.URI[] getDefaultGraphUri() {
        return localDefaultGraphUri;
    }

    /**
     * validate the array for DefaultGraphUri
     */
    protected void validateDefaultGraphUri(
        org.apache.axis2.databinding.types.URI[] param) {
    }

    /**
     * Auto generated setter method
     * @param param DefaultGraphUri
     */
    public void setDefaultGraphUri(
        org.apache.axis2.databinding.types.URI[] param) {
        validateDefaultGraphUri(param);

        if (param != null) {
            //update the setting tracker
            localDefaultGraphUriTracker = true;
        } else {
            localDefaultGraphUriTracker = false;
        }

        this.localDefaultGraphUri = param;
    }

    /**
     * Auto generated add method for the array for convenience
     * @param param org.apache.axis2.databinding.types.URI
     */
    public void addDefaultGraphUri(org.apache.axis2.databinding.types.URI param) {
        if (localDefaultGraphUri == null) {
            localDefaultGraphUri = new org.apache.axis2.databinding.types.URI[] {  };
        }

        //update the setting tracker
        localDefaultGraphUriTracker = true;

        java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localDefaultGraphUri);
        list.add(param);
        this.localDefaultGraphUri = (org.apache.axis2.databinding.types.URI[]) list.toArray(new org.apache.axis2.databinding.types.URI[list.size()]);
    }

    /**
     * Auto generated getter method
     * @return org.apache.axis2.databinding.types.URI[]
     */
    public org.apache.axis2.databinding.types.URI[] getNamedGraphUri() {
        return localNamedGraphUri;
    }

    /**
     * validate the array for NamedGraphUri
     */
    protected void validateNamedGraphUri(
        org.apache.axis2.databinding.types.URI[] param) {
    }

    /**
     * Auto generated setter method
     * @param param NamedGraphUri
     */
    public void setNamedGraphUri(org.apache.axis2.databinding.types.URI[] param) {
        validateNamedGraphUri(param);

        if (param != null) {
            //update the setting tracker
            localNamedGraphUriTracker = true;
        } else {
            localNamedGraphUriTracker = false;
        }

        this.localNamedGraphUri = param;
    }

    /**
     * Auto generated add method for the array for convenience
     * @param param org.apache.axis2.databinding.types.URI
     */
    public void addNamedGraphUri(org.apache.axis2.databinding.types.URI param) {
        if (localNamedGraphUri == null) {
            localNamedGraphUri = new org.apache.axis2.databinding.types.URI[] {  };
        }

        //update the setting tracker
        localNamedGraphUriTracker = true;

        java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localNamedGraphUri);
        list.add(param);
        this.localNamedGraphUri = (org.apache.axis2.databinding.types.URI[]) list.toArray(new org.apache.axis2.databinding.types.URI[list.size()]);
    }

    /**
     * isReaderMTOMAware
     * @return true if the reader supports MTOM
     */
    public static boolean isReaderMTOMAware(
        javax.xml.stream.XMLStreamReader reader) {
        boolean isReaderMTOMAware = false;

        try {
            isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(
                        org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
        } catch (java.lang.IllegalArgumentException e) {
            isReaderMTOMAware = false;
        }

        return isReaderMTOMAware;
    }

    /**
     *
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
        final javax.xml.namespace.QName parentQName,
        final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {
        org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                MY_QNAME) {
                public void serialize(
                    org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                    throws javax.xml.stream.XMLStreamException {
                    QueryRequest.this.serialize(MY_QNAME, factory, xmlWriter);
                }
            };

        return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME,
            factory, dataSource);
    }

    public void serialize(final javax.xml.namespace.QName parentQName,
        final org.apache.axiom.om.OMFactory factory,
        org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException,
            org.apache.axis2.databinding.ADBException {
        java.lang.String prefix = null;
        java.lang.String namespace = null;

        prefix = parentQName.getPrefix();
        namespace = parentQName.getNamespaceURI();

        if (namespace != null) {
            java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);

            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace,
                    parentQName.getLocalPart());
            } else {
                if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(),
                    namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        } else {
            xmlWriter.writeStartElement(parentQName.getLocalPart());
        }

        namespace = "";

        if (!namespace.equals("")) {
            prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                xmlWriter.writeStartElement(prefix, "query", namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            } else {
                xmlWriter.writeStartElement(namespace, "query");
            }
        } else {
            xmlWriter.writeStartElement("query");
        }

        if (localQuery == null) {
            // write the nil attribute
            throw new org.apache.axis2.databinding.ADBException(
                "query cannot be null!!");
        } else {
            xmlWriter.writeCharacters(localQuery);
        }

        xmlWriter.writeEndElement();

        if (localDefaultGraphUriTracker) {
            if (localDefaultGraphUri != null) {
                namespace = "";

                boolean emptyNamespace = (namespace == null) ||
                    (namespace.length() == 0);
                prefix = emptyNamespace ? null : xmlWriter.getPrefix(namespace);

                for (int i = 0; i < localDefaultGraphUri.length; i++) {
                    if (localDefaultGraphUri[i] != null) {
                        if (!emptyNamespace) {
                            if (prefix == null) {
                                java.lang.String prefix2 = generatePrefix(namespace);

                                xmlWriter.writeStartElement(prefix2,
                                    "default-graph-uri", namespace);
                                xmlWriter.writeNamespace(prefix2, namespace);
                                xmlWriter.setPrefix(prefix2, namespace);
                            } else {
                                xmlWriter.writeStartElement(namespace,
                                    "default-graph-uri");
                            }
                        } else {
                            xmlWriter.writeStartElement("default-graph-uri");
                        }

                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localDefaultGraphUri[i]));

                        xmlWriter.writeEndElement();
                    } else {
                        // we have to do nothing since minOccurs is zero
                    }
                }
            } else {
                throw new org.apache.axis2.databinding.ADBException(
                    "default-graph-uri cannot be null!!");
            }
        }

        if (localNamedGraphUriTracker) {
            if (localNamedGraphUri != null) {
                namespace = "";

                boolean emptyNamespace = (namespace == null) ||
                    (namespace.length() == 0);
                prefix = emptyNamespace ? null : xmlWriter.getPrefix(namespace);

                for (int i = 0; i < localNamedGraphUri.length; i++) {
                    if (localNamedGraphUri[i] != null) {
                        if (!emptyNamespace) {
                            if (prefix == null) {
                                java.lang.String prefix2 = generatePrefix(namespace);

                                xmlWriter.writeStartElement(prefix2,
                                    "named-graph-uri", namespace);
                                xmlWriter.writeNamespace(prefix2, namespace);
                                xmlWriter.setPrefix(prefix2, namespace);
                            } else {
                                xmlWriter.writeStartElement(namespace,
                                    "named-graph-uri");
                            }
                        } else {
                            xmlWriter.writeStartElement("named-graph-uri");
                        }

                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localNamedGraphUri[i]));

                        xmlWriter.writeEndElement();
                    } else {
                        // we have to do nothing since minOccurs is zero
                    }
                }
            } else {
                throw new org.apache.axis2.databinding.ADBException(
                    "named-graph-uri cannot be null!!");
            }
        }

        xmlWriter.writeEndElement();
    }

    /**
     * Util method to write an attribute with the ns prefix
     */
    private void writeAttribute(java.lang.String prefix,
        java.lang.String namespace, java.lang.String attName,
        java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }

        xmlWriter.writeAttribute(namespace, attName, attValue);
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(java.lang.String namespace,
        java.lang.String attName, java.lang.String attValue,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName, attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeQNameAttribute(java.lang.String namespace,
        java.lang.String attName, javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
        java.lang.String attributeNamespace = qname.getNamespaceURI();
        java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);

        if (attributePrefix == null) {
            attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
        }

        java.lang.String attributeValue;

        if (attributePrefix.trim().length() > 0) {
            attributeValue = attributePrefix + ":" + qname.getLocalPart();
        } else {
            attributeValue = qname.getLocalPart();
        }

        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName, attributeValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace, attName, attributeValue);
        }
    }

    /**
     *  method to handle Qnames
     */
    private void writeQName(javax.xml.namespace.QName qname,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
        java.lang.String namespaceURI = qname.getNamespaceURI();

        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);

            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
                xmlWriter.writeCharacters(prefix + ":" +
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        qname));
            }
        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                    qname));
        }
    }

    private void writeQNames(javax.xml.namespace.QName[] qnames,
        javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
            java.lang.String namespaceURI = null;
            java.lang.String prefix = null;

            for (int i = 0; i < qnames.length; i++) {
                if (i > 0) {
                    stringToWrite.append(" ");
                }

                namespaceURI = qnames[i].getNamespaceURI();

                if (namespaceURI != null) {
                    prefix = xmlWriter.getPrefix(namespaceURI);

                    if ((prefix == null) || (prefix.length() == 0)) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix, namespaceURI);
                    }

                    if (prefix.trim().length() > 0) {
                        stringToWrite.append(prefix).append(":")
                                     .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                qnames[i]));
                    }
                } else {
                    stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                            qnames[i]));
                }
            }

            xmlWriter.writeCharacters(stringToWrite.toString());
        }
    }

    /**
     * Register a namespace prefix
     */
    private java.lang.String registerPrefix(
        javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
        throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);

        if (prefix == null) {
            prefix = generatePrefix(namespace);

            while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
            }

            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }

        return prefix;
    }

    /**
     * databinding method to get an XML representation of this object
     *
     */
    public javax.xml.stream.XMLStreamReader getPullParser(
        javax.xml.namespace.QName qName)
        throws org.apache.axis2.databinding.ADBException {
        java.util.ArrayList elementList = new java.util.ArrayList();
        java.util.ArrayList attribList = new java.util.ArrayList();

        elementList.add(new javax.xml.namespace.QName("", "query"));

        if (localQuery != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                    localQuery));
        } else {
            throw new org.apache.axis2.databinding.ADBException(
                "query cannot be null!!");
        }

        if (localDefaultGraphUriTracker) {
            if (localDefaultGraphUri != null) {
                for (int i = 0; i < localDefaultGraphUri.length; i++) {
                    if (localDefaultGraphUri[i] != null) {
                        elementList.add(new javax.xml.namespace.QName("",
                                "default-graph-uri"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localDefaultGraphUri[i]));
                    } else {
                        // have to do nothing
                    }
                }
            } else {
                throw new org.apache.axis2.databinding.ADBException(
                    "default-graph-uri cannot be null!!");
            }
        }

        if (localNamedGraphUriTracker) {
            if (localNamedGraphUri != null) {
                for (int i = 0; i < localNamedGraphUri.length; i++) {
                    if (localNamedGraphUri[i] != null) {
                        elementList.add(new javax.xml.namespace.QName("",
                                "named-graph-uri"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                localNamedGraphUri[i]));
                    } else {
                        // have to do nothing
                    }
                }
            } else {
                throw new org.apache.axis2.databinding.ADBException(
                    "named-graph-uri cannot be null!!");
            }
        }

        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName,
            elementList.toArray(), attribList.toArray());
    }

    /**
     *  Factory class that keeps the parse method
     */
    public static class Factory {
        /**
         * static method to create the object
         * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
         *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
         * Postcondition: If this object is an element, the reader is positioned at its end element
         *                If this object is a complex type, the reader is positioned at the end element of its outer element
         */
        public static QueryRequest parse(
            javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            QueryRequest object = new QueryRequest();

            int event;
            java.lang.String nillableValue = null;
            java.lang.String prefix = "";
            java.lang.String namespaceuri = "";

            try {
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.getAttributeValue(
                            "http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                    java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                            "type");

                    if (fullTypeName != null) {
                        java.lang.String nsPrefix = null;

                        if (fullTypeName.indexOf(":") > -1) {
                            nsPrefix = fullTypeName.substring(0,
                                    fullTypeName.indexOf(":"));
                        }

                        nsPrefix = (nsPrefix == null) ? "" : nsPrefix;

                        java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(
                                    ":") + 1);

                        if (!"query-request".equals(type)) {
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext()
                                                           .getNamespaceURI(nsPrefix);

                            return (QueryRequest) org.w3.www._2005.sparql_results.ExtensionMapper.getTypeObject(nsUri,
                                type, reader);
                        }
                    }
                }

                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();

                reader.next();

                java.util.ArrayList list2 = new java.util.ArrayList();

                java.util.ArrayList list3 = new java.util.ArrayList();

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.isStartElement() &&
                        new javax.xml.namespace.QName("", "query").equals(
                            reader.getName())) {
                    java.lang.String content = reader.getElementText();

                    object.setQuery(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                            content));

                    reader.next();
                } // End of if for expected property start element

                else {
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException(
                        "Unexpected subelement " + reader.getLocalName());
                }

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.isStartElement() &&
                        new javax.xml.namespace.QName("", "default-graph-uri").equals(
                            reader.getName())) {
                    // Process the array and step past its final element's end.
                    list2.add(reader.getElementText());

                    //loop until we find a start element that is not part of this array
                    boolean loopDone2 = false;

                    while (!loopDone2) {
                        // Ensure we are at the EndElement
                        while (!reader.isEndElement()) {
                            reader.next();
                        }

                        // Step out of this element
                        reader.next();

                        // Step to next element event.
                        while (!reader.isStartElement() &&
                                !reader.isEndElement())
                            reader.next();

                        if (reader.isEndElement()) {
                            //two continuous end elements means we are exiting the xml structure
                            loopDone2 = true;
                        } else {
                            if (new javax.xml.namespace.QName("",
                                        "default-graph-uri").equals(
                                        reader.getName())) {
                                list2.add(reader.getElementText());
                            } else {
                                loopDone2 = true;
                            }
                        }
                    }

                    // call the converter utility  to convert and set the array
                    object.setDefaultGraphUri((org.apache.axis2.databinding.types.URI[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                            org.apache.axis2.databinding.types.URI.class, list2));
                } // End of if for expected property start element

                else {
                }

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.isStartElement() &&
                        new javax.xml.namespace.QName("", "named-graph-uri").equals(
                            reader.getName())) {
                    // Process the array and step past its final element's end.
                    list3.add(reader.getElementText());

                    //loop until we find a start element that is not part of this array
                    boolean loopDone3 = false;

                    while (!loopDone3) {
                        // Ensure we are at the EndElement
                        while (!reader.isEndElement()) {
                            reader.next();
                        }

                        // Step out of this element
                        reader.next();

                        // Step to next element event.
                        while (!reader.isStartElement() &&
                                !reader.isEndElement())
                            reader.next();

                        if (reader.isEndElement()) {
                            //two continuous end elements means we are exiting the xml structure
                            loopDone3 = true;
                        } else {
                            if (new javax.xml.namespace.QName("",
                                        "named-graph-uri").equals(
                                        reader.getName())) {
                                list3.add(reader.getElementText());
                            } else {
                                loopDone3 = true;
                            }
                        }
                    }

                    // call the converter utility  to convert and set the array
                    object.setNamedGraphUri((org.apache.axis2.databinding.types.URI[]) org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                            org.apache.axis2.databinding.types.URI.class, list3));
                } // End of if for expected property start element

                else {
                }

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.isStartElement()) {
                    // A start element we are not expecting indicates a trailing invalid property
                    throw new org.apache.axis2.databinding.ADBException(
                        "Unexpected subelement " + reader.getLocalName());
                }
            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }
    } //end of factory class
}
