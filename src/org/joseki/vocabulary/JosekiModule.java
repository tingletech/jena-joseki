/*
 * (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.vocabulary ;
 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from etc/joseki-module.n3 
 * @author Auto-generated by schemagen on 20 Jun 2003 15:22 
 */
public class JosekiModule {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabalary as a string ({@value})</p> */
    public static final String NS = "http://joseki.org/2003/06/module#";
    
    /** <p>The namespace of the vocabalary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabalary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>module</p> */
    public static final Property module = m_model.createProperty( "http://joseki.org/2003/06/module#module" );
    
    /** <p>URI of the interface for the binding.</p> */
    public static final Property interface_ = m_model.createProperty( "http://joseki.org/2003/06/module#interface" );
    
    /** <p>Identifies the implementation to use</p> */
    public static final Property implementation = m_model.createProperty( "http://joseki.org/2003/06/module#implementation" );
    
    /** <p>Load Java class</p> */
    public static final Property className = m_model.createProperty( "http://joseki.org/2003/06/module#className" );
    
    public static final Resource Module = m_model.createResource( "http://joseki.org/2003/06/module#Module" );
    
    public static final Resource ModuleBinding = m_model.createResource( "http://joseki.org/2003/06/module#ModuleBinding" );
    
}


/*
 *  (c) Copyright 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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
