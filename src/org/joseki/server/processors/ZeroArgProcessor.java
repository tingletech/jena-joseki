/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;

import org.apache.commons.logging.* ;
import org.joseki.server.*;
import com.hp.hpl.jena.rdf.model.*;

/** General template for any operation that takes no models
 *  (in the request body) as arguments - they can have parameters.
 * 
 * @author      Andy Seaborne
 * @version     $Id: ZeroArgProcessor.java,v 1.1 2004-11-03 10:15:03 andy_seaborne Exp $
 */
public abstract class ZeroArgProcessor extends ProcessorCom
{
    static final Log logger = LogFactory.getLog(ZeroArgProcessor.class.getName()) ; 
 
    public ZeroArgProcessor(String n, int lockNeeded, int mutating)
    {
        super(n, lockNeeded, mutating) ;
    }

    public int argsNeeded() { return Processor.ARGS_ZERO ; }
    
    /**
     * @see org.joseki.server.Processor#exec(Request)
     */
    public Model exec(Request request) throws ExecutionException
    {
        SourceModel src = request.getSourceModel() ;
        if ( super.mutatingOp && src.isImmutable() )
            throw new ExecutionException(ExecutionError.rcImmutableModel, "Immutable Model") ;
        
        try {
            src.startOperation(readOnlyLock) ;
            try {
                Model resultModel = execZeroArg(src, request) ;
                return resultModel ;
            } catch (RDFException ex)
            {
                src.abortOperation() ;
                logger.trace("RDFException: "+ex.getMessage() ) ;
                throw new ExecutionException(ExecutionError.rcInternalError, null) ;
            }
            catch (Exception ex)
            {
                src.abortOperation() ;
                logger.trace("Exception: "+ex.getMessage() ) ;
                throw new ExecutionException(ExecutionError.rcInternalError, null) ;
            }
        } finally {src.endOperation(); }
        //return emptyModel ;
    }
    
    public abstract Model execZeroArg(SourceModel target, Request request)
        throws RDFException, ExecutionException ;
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
