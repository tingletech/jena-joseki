/* CVS $Id: JosekiVocab.java,v 1.1 2005-06-23 09:55:59 andy_seaborne Exp $ */
 
package org.joseki ;

import com.hp.hpl.jena.rdf.model.*;

public class JosekiVocab {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://joseki.org/2005/06/configuration#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>Association of a server with a service</p> */
    public static final Property service = m_model.createProperty( "http://joseki.org/2005/06/configuration#service" );
    
    /** <p></p> */
    public static final Property namedGraph = m_model.createProperty( "http://joseki.org/2005/06/configuration#namedGraph" );
    
    public static final Property allowWebLoading = m_model.createProperty( "http://joseki.org/2005/06/configuration#allowWebLoading" );
    
    /** <p></p> */
    public static final Property graphData = m_model.createProperty( "http://joseki.org/2005/06/configuration#graphData" );
    
    /** <p></p> */
    public static final Property graphName = m_model.createProperty( "http://joseki.org/2005/06/configuration#graphName" );
    
    /** <p>query operation name</p> */
    public static final Property queryOperationName = m_model.createProperty( "http://joseki.org/2005/06/configuration#queryOperationName" );
    
    /** <p>URI reference for service URI (relative to where the server is)</p> */
    public static final Property serviceRef = m_model.createProperty( "http://joseki.org/2005/06/configuration#serviceRef" );
    
    public static final Property allowExplicitDataset = m_model.createProperty( "http://joseki.org/2005/06/configuration#allowExplicitDataset" );
    
    /** <p>Other resources to read and merge (e.g. file:...)</p> */
    public static final Property include = m_model.createProperty( "http://joseki.org/2005/06/configuration#include" );
    
    /** <p>URI reference for service URI (relative to where the server is)</p> */
    public static final Property processor = m_model.createProperty( "http://joseki.org/2005/06/configuration#processor" );
    
    /** <p></p> */
    public static final Property defaultGraph = m_model.createProperty( "http://joseki.org/2005/06/configuration#defaultGraph" );
    
    /** <p></p> */
    public static final Resource RDFDataSet = m_model.createResource( "http://joseki.org/2005/06/configuration#RDFDataSet" );
    
    /** <p>Processor</p> */
    public static final Resource Processor = m_model.createResource( "http://joseki.org/2005/06/configuration#Processor" );
    
    /** <p>Service</p> */
    public static final Resource ServicePoint = m_model.createResource( "http://joseki.org/2005/06/configuration#ServicePoint" );
    
    /** <p>Server</p> */
    public static final Resource Server = m_model.createResource( "http://joseki.org/2005/06/configuration#Server" );
    
}
