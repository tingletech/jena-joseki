/* CVS $Id$ */
 
package dev ;

import com.hp.hpl.jena.rdf.model.*;

/**
 * Vocabulary definitions from Vocabularies/joseki-schema-new.ttl 
 * @author Auto-generated by schemagen on 27 May 2005 16:24 
 */
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
    
    /** <p></p> */
    public static final Property graphData = m_model.createProperty( "http://joseki.org/2005/06/configuration#graphData" );
    
    /** <p></p> */
    public static final Property graphName = m_model.createProperty( "http://joseki.org/2005/06/configuration#graphName" );
    
    /** <p>Other resources to read and merge (e.g. file:...)</p> */
    public static final Property include = m_model.createProperty( "http://joseki.org/2005/06/configuration#include" );
    
    /** <p></p> */
    public static final Property defaultGraph = m_model.createProperty( "http://joseki.org/2005/06/configuration#defaultGraph" );
    
    /** <p>Service</p> */
    public static final Resource Service = m_model.createResource( "http://joseki.org/2005/06/configuration#Service" );
    
    /** <p></p> */
    public static final Resource RDFDataSet = m_model.createResource( "http://joseki.org/2005/06/configuration#RDFDataSet" );
    
    /** <p>Server</p> */
    public static final Resource Server = m_model.createResource( "http://joseki.org/2005/06/configuration#Server" );
    
}