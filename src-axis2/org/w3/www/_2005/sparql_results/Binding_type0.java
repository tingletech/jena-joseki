/**
 * Binding_type0.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:58 LKT)
 */
package org.w3.www._2005.sparql_results;


/**
 *  Binding_type0 bean class
 */
public class Binding_type0 implements org.apache.axis2.databinding.ADBBean {
    /**
     * field for Uri
     */
    protected java.lang.String localUri;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localUriTracker = false;

    /**
     * field for Bnode
     */
    protected java.lang.String localBnode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localBnodeTracker = false;

    /**
     * field for Literal
     */
    protected org.w3.www._2005.sparql_results.Literal_type0 localLiteral;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localLiteralTracker = false;

    /**
     * field for Name
     * This was an Attribute!
     */
    protected org.apache.axis2.databinding.types.NMToken localName;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    protected boolean localNameTracker = false;

    /* This type was generated from the piece of schema that had
       name = binding_type0
       Namespace URI = http://www.w3.org/2005/sparql-results#
       Namespace Prefix = ns1
     */
    private static java.lang.String generatePrefix(java.lang.String namespace) {
        if (namespace.equals("http://www.w3.org/2005/sparql-results#")) {
            return "ns1";
        }

        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /** Whenever a new property is set ensure all others are unset
     *  There can be only one choice and the last one wins
     */
    private void clearAllSettingTrackers() {
        localUriTracker = false;

        localBnodeTracker = false;

        localLiteralTracker = false;

        localNameTracker = false;
    }

    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public java.lang.String getUri() {
        return localUri;
    }

    /**
     * Auto generated setter method
     * @param param Uri
     */
    public void setUri(java.lang.String param) {
        clearAllSettingTrackers();

        if (param != null) {
            //update the setting tracker
            localUriTracker = true;
        } else {
            localUriTracker = false;
        }

        this.localUri = param;
    }

    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public java.lang.String getBnode() {
        return localBnode;
    }

    /**
     * Auto generated setter method
     * @param param Bnode
     */
    public void setBnode(java.lang.String param) {
        clearAllSettingTrackers();

        if (param != null) {
            //update the setting tracker
            localBnodeTracker = true;
        } else {
            localBnodeTracker = false;
        }

        this.localBnode = param;
    }

    /**
     * Auto generated getter method
     * @return org.w3.www._2005.sparql_results.Literal_type0
     */
    public org.w3.www._2005.sparql_results.Literal_type0 getLiteral() {
        return localLiteral;
    }

    /**
     * Auto generated setter method
     * @param param Literal
     */
    public void setLiteral(org.w3.www._2005.sparql_results.Literal_type0 param) {
        clearAllSettingTrackers();

        if (param != null) {
            //update the setting tracker
            localLiteralTracker = true;
        } else {
            localLiteralTracker = false;
        }

        this.localLiteral = param;
    }

    /**
     * Auto generated getter method
     * @return org.apache.axis2.databinding.types.NMToken
     */
    public org.apache.axis2.databinding.types.NMToken getName() {
        return localName;
    }

    /**
     * Auto generated setter method
     * @param param Name
     */
    public void setName(org.apache.axis2.databinding.types.NMToken param) {
        clearAllSettingTrackers();

        if (param != null) {
            //update the setting tracker
            localNameTracker = true;
        } else {
            localNameTracker = false;
        }

        this.localName = param;
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
                parentQName) {
                public void serialize(
                    org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                    throws javax.xml.stream.XMLStreamException {
                    Binding_type0.this.serialize(parentQName, factory, xmlWriter);
                }
            };

        return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName,
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

        if (localName != null) {
            writeAttribute("", "name",
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                    localName), xmlWriter);
        } else {
            throw new org.apache.axis2.databinding.ADBException(
                "required attribute localName is null");
        }

        if (localUriTracker) {
            namespace = "http://www.w3.org/2005/sparql-results#";

            if (!namespace.equals("")) {
                prefix = xmlWriter.getPrefix(namespace);

                if (prefix == null) {
                    prefix = generatePrefix(namespace);

                    xmlWriter.writeStartElement(prefix, "uri", namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                } else {
                    xmlWriter.writeStartElement(namespace, "uri");
                }
            } else {
                xmlWriter.writeStartElement("uri");
            }

            if (localUri == null) {
                // write the nil attribute
                throw new org.apache.axis2.databinding.ADBException(
                    "uri cannot be null!!");
            } else {
                xmlWriter.writeCharacters(localUri);
            }

            xmlWriter.writeEndElement();
        }

        if (localBnodeTracker) {
            namespace = "http://www.w3.org/2005/sparql-results#";

            if (!namespace.equals("")) {
                prefix = xmlWriter.getPrefix(namespace);

                if (prefix == null) {
                    prefix = generatePrefix(namespace);

                    xmlWriter.writeStartElement(prefix, "bnode", namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                } else {
                    xmlWriter.writeStartElement(namespace, "bnode");
                }
            } else {
                xmlWriter.writeStartElement("bnode");
            }

            if (localBnode == null) {
                // write the nil attribute
                throw new org.apache.axis2.databinding.ADBException(
                    "bnode cannot be null!!");
            } else {
                xmlWriter.writeCharacters(localBnode);
            }

            xmlWriter.writeEndElement();
        }

        if (localLiteralTracker) {
            if (localLiteral == null) {
                throw new org.apache.axis2.databinding.ADBException(
                    "literal cannot be null!!");
            }

            localLiteral.serialize(new javax.xml.namespace.QName(
                    "http://www.w3.org/2005/sparql-results#", "literal"),
                factory, xmlWriter);
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

        if (localUriTracker) {
            elementList.add(new javax.xml.namespace.QName(
                    "http://www.w3.org/2005/sparql-results#", "uri"));

            if (localUri != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localUri));
            } else {
                throw new org.apache.axis2.databinding.ADBException(
                    "uri cannot be null!!");
            }
        }

        if (localBnodeTracker) {
            elementList.add(new javax.xml.namespace.QName(
                    "http://www.w3.org/2005/sparql-results#", "bnode"));

            if (localBnode != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                        localBnode));
            } else {
                throw new org.apache.axis2.databinding.ADBException(
                    "bnode cannot be null!!");
            }
        }

        if (localLiteralTracker) {
            elementList.add(new javax.xml.namespace.QName(
                    "http://www.w3.org/2005/sparql-results#", "literal"));

            if (localLiteral == null) {
                throw new org.apache.axis2.databinding.ADBException(
                    "literal cannot be null!!");
            }

            elementList.add(localLiteral);
        }

        attribList.add(new javax.xml.namespace.QName("", "name"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                localName));

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
        public static Binding_type0 parse(
            javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            Binding_type0 object = new Binding_type0();

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

                        if (!"binding_type0".equals(type)) {
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext()
                                                           .getNamespaceURI(nsPrefix);

                            return (Binding_type0) org.w3.www._2005.sparql_results.ExtensionMapper.getTypeObject(nsUri,
                                type, reader);
                        }
                    }
                }

                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();

                // handle attribute "name"
                java.lang.String tempAttribName = reader.getAttributeValue(null,
                        "name");

                if (tempAttribName != null) {
                    java.lang.String content = tempAttribName;

                    object.setName(org.apache.axis2.databinding.utils.ConverterUtil.convertToNMTOKEN(
                            tempAttribName));
                } else {
                    throw new org.apache.axis2.databinding.ADBException(
                        "Required attribute name is missing");
                }

                handledAttributes.add("name");

                reader.next();

                while (!reader.isEndElement()) {
                    if (reader.isStartElement()) {
                        if (reader.isStartElement() &&
                                new javax.xml.namespace.QName(
                                    "http://www.w3.org/2005/sparql-results#",
                                    "uri").equals(reader.getName())) {
                            java.lang.String content = reader.getElementText();

                            object.setUri(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    content));

                            reader.next();
                        } // End of if for expected property start element

                        else if (reader.isStartElement() &&
                                new javax.xml.namespace.QName(
                                    "http://www.w3.org/2005/sparql-results#",
                                    "bnode").equals(reader.getName())) {
                            java.lang.String content = reader.getElementText();

                            object.setBnode(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                                    content));

                            reader.next();
                        } // End of if for expected property start element

                        else if (reader.isStartElement() &&
                                new javax.xml.namespace.QName(
                                    "http://www.w3.org/2005/sparql-results#",
                                    "literal").equals(reader.getName())) {
                            object.setLiteral(org.w3.www._2005.sparql_results.Literal_type0.Factory.parse(
                                    reader));

                            reader.next();
                        } // End of if for expected property start element

                        else {
                            // A start element we are not expecting indicates an invalid parameter was passed
                            throw new org.apache.axis2.databinding.ADBException(
                                "Unexpected subelement " +
                                reader.getLocalName());
                        }
                    } else {
                        reader.next();
                    }
                } // end of while loop
            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }
    } //end of factory class
}
