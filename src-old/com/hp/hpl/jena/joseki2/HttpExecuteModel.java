/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.joseki2;

import org.apache.commons.logging.* ;
import java.net.* ;
import java.io.* ;

import com.hp.hpl.jena.rdf.model.*;
import org.joseki.*;

/**
 * @author Andy Seaborne
 * @version $Id: HttpExecuteModel.java,v 1.1 2005-06-23 09:55:59 andy_seaborne Exp $
 */
/** Common code for performing an HTTP operation which takes a single
 *  model as argument.
 *  
 *  @see HttpAdd
 *  @see HttpRemove
 *  @see HttpQuery
 * @author      Andy Seaborne
 * @version     $Id: HttpExecuteModel.java,v 1.1 2005-06-23 09:55:59 andy_seaborne Exp $
 */

public class HttpExecuteModel extends HttpExecute
{
    private static Log log = LogFactory.getLog(HttpExecuteModel.class.getName()) ;
    
    private Model model = null ;
    
    /** Usual way to contruct the request
     * @param target
     * @param opName
     * @throws MalformedURLException
     */ 
    
    protected HttpExecuteModel(String target, String opName)
        throws MalformedURLException
    { super(target, opName) ; }
    
    /** Allow 2 stage constructor because subclasses may wish to
     *  compute before setting the URL (@see setURL)
     */
    protected HttpExecuteModel() { super(); }
    
    
    /** Send the single model argument.  Will use UTF-8.
     *  @param mediaType   MIME type (no charset) to be used.
     *  @param out      The OutputStream to use
     */
    
    protected void onSend(String mediaType, OutputStream out)
    {
        model.write(out, Joseki.getWriterType(mediaType)) ;
    }
    
    public Model getModel()
    {
        ensureModel() ;
        return model ;
    }
    
    public void setModel(Model m)
    {
        if ( model != null )
            log.warn("Replacing allocated model") ;
        model = m ;
    }
    
    // Not called "add" because the underlying operation, like HttpRemove,
    // may not wish to call it "add".
    
    protected void collect(Statement s)
    {
        ensureModel() ;
        model.add(s) ;
    }
    
    protected void collect(Model m)
    {
        ensureModel() ;
        model.add(m) ;
    }
    
    
    private void ensureModel()
    {
        if ( model == null )
            // ModelFactory.createNonreifyingModel??
            model = ModelFactory.createDefaultModel() ;
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

