/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.joseki;

import java.net.* ;
import com.hp.hpl.jena.rdf.model.*;

/** Add statements to a remote model.
 *  A typical code sequence might be:
 *  <pre>
 *    HttpAdd addOp = new HttpAdd(URL of target) ;
 *    addOp.setModel(your model);
 *    addOp.exec() ;
 *    // resultModel should be an empty model
 *  </pre>
 * or
 *  <pre>
 *    HttpAdd addOp = new HttpAdd(URL of target) ;
 *    // Collect statements to be added
 *    addOp.add(statement) ;
 *    addOp.add(model) ;  // Adds all the statements in a model
 *    addOp.exec() ;      // Result is an empty model
 *  </pre>
 * 
 * The result model will be an empty model for a successful operation.
 * Any problems cause {@link HttpException} to be thrown on .exec()
 * 
 * @author      Andy Seaborne
 * @version     $Id: HttpAdd.java,v 1.2 2005-01-03 20:26:32 andy_seaborne Exp $
 */
public class HttpAdd extends HttpExecuteModel
{
    /** Create an operation for adding statements to a remote model */
    public HttpAdd(URL url) throws MalformedURLException
    {
        this(url.toString()) ;
    }
    
    /** Create an operation for adding statements to a remote model */
    public HttpAdd(String urlStr) throws MalformedURLException
    {
        super(urlStr, "add") ;
    }

    /** Accumulate statements to be added when the operations is executed
     *  @param model            A set of statements to be added on execution
     */
    public void add(Model model) { collect(model) ; }
    
    /** Accumulate statements to be added when the operations is executed
     *  @param statement         A statement to be added on execution
     */
    public void add(Statement statement) { collect(statement) ; }
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
 
