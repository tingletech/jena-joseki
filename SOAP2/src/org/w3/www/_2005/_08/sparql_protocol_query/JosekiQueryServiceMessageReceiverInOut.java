/**
 * JosekiQueryServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:47 LKT)
 */
package org.w3.www._2005._08.sparql_protocol_query;


/**
 *  JosekiQueryServiceMessageReceiverInOut message receiver
 */
public class JosekiQueryServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver {
    public void invokeBusinessLogic(
        org.apache.axis2.context.MessageContext msgContext,
        org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault {
        try {
            // get the implementation class for the Web Service
            Object obj = getTheImplementationObject(msgContext);

            JosekiQueryServiceSkeleton skel = (JosekiQueryServiceSkeleton) obj;

            //Out Envelop
            org.apache.axiom.soap.SOAPEnvelope envelope = null;

            //Find the axisOperation that has been set by the Dispatch phase.
            org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext()
                                                                      .getAxisOperation();

            if (op == null) {
                throw new org.apache.axis2.AxisFault(
                    "Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
            }

            java.lang.String methodName;

            if ((op.getName() != null) &&
                    ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJava(
                            op.getName().getLocalPart())) != null)) {
                if ("query".equals(methodName)) {
                    org.w3.www._2005._09.sparql_protocol_types.QueryResult queryResult1 =
                        null;
                    org.w3.www._2005._09.sparql_protocol_types.QueryRequest wrappedParam =
                        (org.w3.www._2005._09.sparql_protocol_types.QueryRequest) fromOM(msgContext.getEnvelope()
                                                                                                   .getBody()
                                                                                                   .getFirstElement(),
                            org.w3.www._2005._09.sparql_protocol_types.QueryRequest.class,
                            getEnvelopeNamespaces(msgContext.getEnvelope()));

                    queryResult1 = skel.query(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            queryResult1, false);
                } else {
                    throw new java.lang.RuntimeException("method not found");
                }

                newMsgContext.setEnvelope(envelope);
            }
        } catch (QueryRequestRefused e) {
            msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME,
                "query-request-refused");

            org.apache.axis2.AxisFault f = createAxisFault(e);

            if (e.getFaultMessage() != null) {
                f.setDetail(toOM(e.getFaultMessage(), false));
            }

            throw f;
        } catch (MalformedQuery e) {
            msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME,
                "malformed-query");

            org.apache.axis2.AxisFault f = createAxisFault(e);

            if (e.getFaultMessage() != null) {
                f.setDetail(toOM(e.getFaultMessage(), false));
            }

            throw f;
        }
        catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    //
    private org.apache.axiom.om.OMElement toOM(
        org.w3.www._2005._09.sparql_protocol_types.QueryRequest param,
        boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(org.w3.www._2005._09.sparql_protocol_types.QueryRequest.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        org.w3.www._2005._09.sparql_protocol_types.QueryResult param,
        boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(org.w3.www._2005._09.sparql_protocol_types.QueryResult.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        org.w3.www._2005._09.sparql_protocol_types.MalformedQuery param,
        boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(org.w3.www._2005._09.sparql_protocol_types.MalformedQuery.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        org.w3.www._2005._09.sparql_protocol_types.QueryRequestRefused param,
        boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(org.w3.www._2005._09.sparql_protocol_types.QueryRequestRefused.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory,
        org.w3.www._2005._09.sparql_protocol_types.QueryResult param,
        boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(
                    org.w3.www._2005._09.sparql_protocol_types.QueryResult.MY_QNAME,
                    factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    /**
     *  get the default envelope
     */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    private java.lang.Object fromOM(org.apache.axiom.om.OMElement param,
        java.lang.Class type, java.util.Map extraNamespaces)
        throws org.apache.axis2.AxisFault {
        try {
            if (org.w3.www._2005._09.sparql_protocol_types.QueryRequest.class.equals(
                        type)) {
                return org.w3.www._2005._09.sparql_protocol_types.QueryRequest.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (org.w3.www._2005._09.sparql_protocol_types.QueryResult.class.equals(
                        type)) {
                return org.w3.www._2005._09.sparql_protocol_types.QueryResult.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (org.w3.www._2005._09.sparql_protocol_types.MalformedQuery.class.equals(
                        type)) {
                return org.w3.www._2005._09.sparql_protocol_types.MalformedQuery.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (org.w3.www._2005._09.sparql_protocol_types.QueryRequestRefused.class.equals(
                        type)) {
                return org.w3.www._2005._09.sparql_protocol_types.QueryRequestRefused.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
        } catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

        return null;
    }

    /**
     *  A utility method that copies the namepaces from the SOAPEnvelope
     */
    private java.util.Map getEnvelopeNamespaces(
        org.apache.axiom.soap.SOAPEnvelope env) {
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();

        while (namespaceIterator.hasNext()) {
            org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
            returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
        }

        return returnMap;
    }

    private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
        org.apache.axis2.AxisFault f;
        Throwable cause = e.getCause();

        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }

        return f;
    }
} //end of class
