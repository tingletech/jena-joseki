/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

/* Vocabulary Class generated by Jena vocabulary generator
 *
 * Version $Id: JosekiSchema.java,v 1.1 2004-11-03 10:15:03 andy_seaborne Exp $
 */

package org.joseki.vocabulary;

import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions for Joseki configuration vocabulary. 
 * @author Auto-generated by schemagen on 01 Jul 2003
 */
public class JosekiSchema {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabalary as a string ({@value})</p> */
    public static final String NS = "http://joseki.org/2003/07/configuration#";
    
    /** <p>The namespace of the vocabalary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabalary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>This model uses an adapter</p> */
    public static final Property sourceController = m_model.createProperty( "http://joseki.org/2003/07/configuration#sourceController" );
    
    /** <p>Other resources to read and merge (e.g. file:...)</p> */
    public static final Property alsoInclude = m_model.createProperty( "http://joseki.org/2003/07/configuration#alsoInclude" );
    
    /** <p>Type for a relational database</p> */
    public static final Property dbType = m_model.createProperty( "http://joseki.org/2003/07/configuration#dbType" );
    
    /** <p>The name used identify the operationon this model</p> */
    public static final Property operationName = m_model.createProperty( "http://joseki.org/2003/07/configuration#operationName" );
    
    /** <p>The name used identify the operation on this model - can be a URI or a short 
     *  name (string)</p>
     */
    public static final Property queryOperationName = m_model.createProperty( "http://joseki.org/2003/07/configuration#queryOperationName" );
    
    /** <p>The namespace URI part of a namespace prefix</p> */
    public static final Property nsURI = m_model.createProperty( "http://joseki.org/2003/07/configuration#nsURI" );
    
    /** <p>Multi-instance property</p> */
    public static final Property hasOperation = m_model.createProperty( "http://joseki.org/2003/07/configuration#hasOperation" );
    
    /** <p>Driver name relational databases</p> */
    public static final Property dbDriver = m_model.createProperty( "http://joseki.org/2003/07/configuration#dbDriver" );
    
    /** <p>User ID for relational database</p> */
    public static final Property user = m_model.createProperty( "http://joseki.org/2003/07/configuration#user" );
    
    /** <p>Indicate whether this model changes</p> */
    public static final Property isImmutable = m_model.createProperty( "http://joseki.org/2003/07/configuration#isImmutable" );
    
    /** <p>Model name a relational database model</p> */
    public static final Property dbModelName = m_model.createProperty( "http://joseki.org/2003/07/configuration#dbModelName" );
    
    public static final Property josekiSchema = m_model.createProperty( "http://joseki.org/2003/07/configuration#josekiSchema" );
    
    /** <p>Model spec to be used in creating the Jena model to back an attached model</p> */
    public static final Property modelSpec = m_model.createProperty( "http://joseki.org/2003/07/configuration#modelSpec" );
    
    /** <p>code to load</p> */
    public static final Property className = m_model.createProperty( "http://joseki.org/2003/07/configuration#className" );
    
    /** <p>Required property</p> */
    public static final Property attachedModel = m_model.createProperty( "http://joseki.org/2003/07/configuration#attachedModel" );
    
    /** <p>The query language of this request</p> */
    public static final Property requestQueryLanguage = m_model.createProperty( "http://joseki.org/2003/07/configuration#requestQueryLanguage" );
    
    /** <p>Port number (if not implicit through servlet)Not normally needed</p> */
    public static final Property serverPort = m_model.createProperty( "http://joseki.org/2003/07/configuration#serverPort" );
    
    /** <p>Optional specification of an OWL ontology to use with this data</p> */
    public static final Property ontology = m_model.createProperty( "http://joseki.org/2003/07/configuration#ontology" );
    
    /** <p>URI of this server - Not normally needed</p> */
    public static final Property serverURI = m_model.createProperty( "http://joseki.org/2003/07/configuration#serverURI" );
    
    /** <p>Password for relational database</p> */
    public static final Property password = m_model.createProperty( "http://joseki.org/2003/07/configuration#password" );
    
    /** <p>The namespace prefix part of a namespace prefix</p> */
    public static final Property prefix = m_model.createProperty( "http://joseki.org/2003/07/configuration#prefix" );
    
    /** <p>Optional specification of an RDFS vocabulary to use with this data</p> */
    public static final Property vocabulary = m_model.createProperty( "http://joseki.org/2003/07/configuration#vocabulary" );
    
    /** <p>Defines an optional namespace prefix for this model.</p> */
    public static final Property namespacePrefix = m_model.createProperty( "http://joseki.org/2003/07/configuration#namespacePrefix" );
    
    /** <p></p> */
    public static final Property operation = m_model.createProperty( "http://joseki.org/2003/07/configuration#operation" );
    
    /** <p>The query as a string</p> */
    public static final Property queryScript = m_model.createProperty( "http://joseki.org/2003/07/configuration#queryScript" );
    
    /** <p>Set server debugging mode</p> */
    public static final Property serverDebug = m_model.createProperty( "http://joseki.org/2003/07/configuration#serverDebug" );
    
    /** <p>Optionally set the document manager for the ontology</p> */
    public static final Property ontDocumentManager = m_model.createProperty( "http://joseki.org/2003/07/configuration#ontDocumentManager" );
    
    /** <p></p> */
    public static final Property queryOperation = m_model.createProperty( "http://joseki.org/2003/07/configuration#queryOperation" );
    
    /** <p>Query language descriptionMulti-instance property</p> */
    public static final Property hasQueryOperation = m_model.createProperty( "http://joseki.org/2003/07/configuration#hasQueryOperation" );
    
    /** <p>Set the MIME type for the protocol</p> */
    public static final Property useContentType = m_model.createProperty( "http://joseki.org/2003/07/configuration#useContentType" );
    
    public static final Resource QueryLanguage = m_model.createResource( "http://joseki.org/2003/07/configuration#QueryLanguage" );
    
    public static final Resource QueryLanguageBinding = m_model.createResource( "http://joseki.org/2003/07/configuration#QueryLanguageBinding" );
    
    public static final Resource Operation = m_model.createResource( "http://joseki.org/2003/07/configuration#Operation" );
    
    public static final Resource QueryOperation = m_model.createResource( "http://joseki.org/2003/07/configuration#QueryOperation" );
    
    public static final Resource OperationBinding = m_model.createResource( "http://joseki.org/2003/07/configuration#OperationBinding" );
    
    public static final Resource AttachedModel = m_model.createResource( "http://joseki.org/2003/07/configuration#AttachedModel" );
    
    /** <p>An adapter to an external model source</p> */
    public static final Resource SourceController = m_model.createResource( "http://joseki.org/2003/07/configuration#SourceController" );
    
    /** <p>Required type</p> */
    public static final Resource JosekiServer = m_model.createResource( "http://joseki.org/2003/07/configuration#JosekiServer" );
    
    public static final Resource ModelDescription = m_model.createResource( "http://joseki.org/2003/07/configuration#ModelDescription" );
    
    public static final Resource QueryOperationBinding = m_model.createResource( "http://joseki.org/2003/07/configuration#QueryOperationBinding" );
    
    public static final Resource opQueryModel = m_model.createResource( "http://joseki.org/2003/07/configuration#opQueryModel" );
    
    public static final Resource opUpdate = m_model.createResource( "http://joseki.org/2003/07/configuration#opUpdate" );
    
    public static final Resource opUnlock = m_model.createResource( "http://joseki.org/2003/07/configuration#opUnlock" );
    
    public static final Resource opRemove = m_model.createResource( "http://joseki.org/2003/07/configuration#opRemove" );
    
    public static final Resource queryRDQL = m_model.createResource( "http://joseki.org/2003/07/configuration#queryRDQL" );
    
    public static final Resource opLock = m_model.createResource( "http://joseki.org/2003/07/configuration#opLock" );
    
    public static final Resource opAdd = m_model.createResource( "http://joseki.org/2003/07/configuration#opAdd" );
    
    public static final Resource opOptions = m_model.createResource( "http://joseki.org/2003/07/configuration#opOptions" );
    
    public static final Resource queryGET = m_model.createResource( "http://joseki.org/2003/07/configuration#queryGET" );
    
}



/*
 *  (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
