/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.joseki;

import org.apache.commons.logging.* ;

import com.hp.hpl.jena.rdf.model.* ;

/** Execution object for a "fetch" over HTTP.
 *  The exact results returned wil depend on the server, which will have
 *  "fetch handlers" on remote model.  The defintion of a "fetch hanlder"
 *  is server-dependent.
 *  
 * There are two ways to identifiy the resource: either by its URI,
 * or by a property/value that identifies the resource.   
 * 
 * @author  Andy Seaborne
 * @version $Id: HttpFetch.java,v 1.1 2004-11-03 10:14:56 andy_seaborne Exp $
 */

public class HttpFetch extends HttpQuery
{
    static final Log logger = LogFactory.getLog(HttpFetch.class.getName()) ;
    
    /** Perform a fetch operation in the named model on the named resource.
     *    
     * @param url          Remote model
     * @param resource     Named resource - must have a URI
     */
    
    public HttpFetch(String url, Resource resource) 
    {
        super(url, "fetch") ;
        addParam("r", resource.getURI()) ;
    } 

    /** Perform a fetch operation in the named model on the named resource 
     *    
     * @param url          Remote model
     * @param resourceURI  Resource URI
     */
    public HttpFetch(String url, String resourceURI) 
    {
        super(url, "fetch") ;
        addParam("r", resourceURI) ;
    }
    
    /** Perform a fetch on a resource identified by predicate and value.
     *  
     * @param url           Remote model.
     * @param predicate     Property
     * @param value         Resource (with URI) or Literal (plain literal) 
     */
    public HttpFetch(String url, Property predicate, RDFNode value) 
    {
        super(url, "fetch") ;
        addParam("p", predicate.getURI()) ;
        if ( value instanceof  Resource)
            addParam("o", ((Resource)value).getURI() ) ;
        else
            addParam("v", ((Literal)value).getLexicalForm()) ;
    } 

    /** Perform a fetch on a resource identified by predciate and value.
     *  
     * @param url           Remote model
     * @param predicateURI  Predicate URI
     * @param objURIorValue String - either URI or literal value
     * @param isURI         Indicator for of kind of object
     */

    public HttpFetch(String url, String predicateURI, String objURIorValue, boolean isURI) 
    {
        super(url, "fetch") ;
        addParam("p", predicateURI) ;
        if ( isURI )
            addParam("o", objURIorValue) ;
        else
            addParam("v", objURIorValue) ;
    } 
    

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
