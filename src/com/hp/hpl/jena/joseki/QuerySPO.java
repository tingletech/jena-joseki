/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */


/** Client acess to the SPO query language.
 * <p>
 *  An SPO request takes three arguments:
 *  <pre>
 *      subject URI,m predciate URI and object (URI or string)
 *  </pre>
 *  where not specifying a value means wildcard.  See the Jena Model.listStatements(S,P,O)
 *  operation.
 * 
 * @author     Andy Seaborne
 * @version    $Id: QuerySPO.java,v 1.2 2005-01-03 20:26:32 andy_seaborne Exp $
 */
 
package com.hp.hpl.jena.joseki;

import org.apache.commons.logging.* ;
import com.hp.hpl.jena.rdf.model.* ;

public class QuerySPO extends HttpQuery
{
    static Log logger = LogFactory.getLog(QuerySPO.class.getName()) ;
    
    String subject = null ;
    String predicate = null ;
    String object = null ;
    String value = null ;
    boolean closure = false ;
    
    /** Create a request going to the named model.  
     * 
     * @param target
     */
    
    public QuerySPO(String target)
    {
        super(target, "Triples") ;
    }

    /** Create a request going to the named model, with specified
     *  subject, predicate and string value
     * 
     * @param target
     * @param r
     * @param p
     * @param lit
     */  
    
    public QuerySPO(String target, Resource r, Property p, Literal lit)
    {
        super(target, "Triples") ;
        if ( r != null )
            setSubject(r.getURI()) ;
        if ( p != null )
            setPredicate(p.getURI()) ;
        if ( lit != null )
            setValue(lit.getLexicalForm()) ;
    }

    /** Create a request going to the named model, with specified
     *  subject, predicate and object resource.
     * 
     * @param target
     * @param r
     * @param p
     * @param obj
     */

    public QuerySPO(String target, Resource r, Property p, Resource obj)
    {
        super(target, "Triples") ;
        if ( r != null )
            setSubject(r.getURI()) ;
        if ( p != null )
            setPredicate(p.getURI()) ;
        if ( obj != null )
            setObject(obj.getURI()) ;
    }

    public void setSubject(String subj)
    {
        subject = subj ;
        super.addParam("s", subj) ;
    }

    public void setPredicate(String pred)
    {
        predicate = pred ;
        super.addParam("p", pred) ;
    }

    public void setObject(String obj)
    {
        object = obj ;
        super.addParam("o", obj) ;
    }

    public void setValue(String val)
    {
        value = val ;
        super.addParam("v", val) ;
    }

    public void setClosure(boolean close)
    {
        closure = close ;
        super.addParam("close", String.valueOf(close)) ;
    }
}
    

/*
 *  (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
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
