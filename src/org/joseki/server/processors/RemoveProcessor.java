/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */
 
package org.joseki.server.processors;

//import org.apache.commons.logging.* ;
import org.joseki.server.*;
import org.joseki.vocabulary.*;

import com.hp.hpl.jena.rdf.model.*;

/** ProcessorModel to remove the statements in the argument model from the target model.
 * @author      Andy Seaborne
 * @version     $Id: RemoveProcessor.java,v 1.2 2004-11-03 17:37:55 andy_seaborne Exp $
 */
public class RemoveProcessor extends OneArgProcessor
{

    public RemoveProcessor()
    {
        super("remove", ProcessorCom.WriteOperation, ProcessorCom.MutatesModel) ;
    }

    public String getInterfaceURI() { return JosekiVocab.opRemove ; }

    public Model execOneArg(SourceModel src, Model graph, Request req)
        throws RDFException, ExecutionException
    {
        if (!(src instanceof SourceModelJena))
            throw new ExecutionException(
                ExecutionError.rcOperationNotSupported,
                "Wrong implementation - this Fetch processor works with Jena models");         
        Model target = ((SourceModelJena)src).getModel() ;
        target.remove(graph) ;
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
