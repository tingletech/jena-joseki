/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;

import org.apache.commons.logging.* ;
import org.joseki.server.*;
//import org.joseki.server.http.HttpUtils;

import com.hp.hpl.jena.rdf.model.*;

/** General purpose template for implementing ProcessorModel's.
 *  Use ProcessorCom for locking - this class provides translation
 *  between the general Processor Request=>Response framework and
 *  the model-based ProcessorModel Request=>Model framework.    
 * 
 * @see Processor
 * @see ProcessorCom
 * 
 * @author      Andy Seaborne
 * @version     $Id: ProcessorModelCom.java,v 1.6 2004-11-12 20:01:02 andy_seaborne Exp $
 */
public abstract class ProcessorModelCom extends ProcessorCom implements ProcessorModel
{
    Log logger = LogFactory.getLog(ProcessorModelCom.class) ;
    // Useful constant
    static protected Model emptyModel = ModelFactory.createDefaultModel() ;
    

    public ProcessorModelCom(String opName, int opType)
    {
        super(opName, opType) ;
    }

    
    /** @see org.joseki.server.module.Loadable#init(Resource, Resource)
     */
    public void init(Resource processor, Resource implementation) { }
    
    /** Convert from Request/Response framework to Request/Model framework */ 
    public void execute(Request request, Response response)  throws ExecutionException
    {
        try {
            Model resultModel = exec(request) ;
            response.setResponseCode(Response.rcOK) ;
            response.startResponse() ;
            response.doResponse(resultModel) ;
            response.finishResponse() ;
        } catch (ExecutionException ex)
        {
            response.setResponseCode(ex.returnCode) ;
            response.startResponse() ;
            response.doException(ex) ;
            response.finishResponse() ;
        }
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
