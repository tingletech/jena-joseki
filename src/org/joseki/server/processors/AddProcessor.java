/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */
 
package org.joseki.server.processors;

import org.joseki.server.*;
import org.joseki.vocabulary.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFException;

/** ProcessorModel to add the statements in the argument model into the target model.
 *
 * @author      Andy Seaborne
 * @version     $Id: AddProcessor.java,v 1.3 2004-11-11 11:52:39 andy_seaborne Exp $
 */
public class AddProcessor extends OneArgProcessor
{
    public AddProcessor()
    {
        super("add", ProcessorModelCom.WriteOperation, ProcessorModelCom.MutatesModel) ;
    }

    public String getInterfaceURI() { return JosekiVocab.opAdd ; }

    public Model execOneArg(SourceModel src, Model graph, Request req)
        throws RDFException, ExecutionException
    {
        if (!(src instanceof SourceModelJena))
            throw new ExecutionException(
                ExecutionError.rcOperationNotSupported,
                "Wrong implementation - this Fetch processor works with Jena models");         
        Model target = ((SourceModelJena)src).getModel() ;
        
        target.add(graph) ;
        return super.emptyModel ;
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
