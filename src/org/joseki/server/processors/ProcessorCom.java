/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;

import org.apache.commons.logging.* ;
import org.joseki.server.*;
import org.joseki.server.module.* ;
import com.hp.hpl.jena.rdf.model.*;

/** General purpose template for implementing Processors.
 *  Provides transaction or MRSW locking aroud a call to 
 * 
 * @see Processor

 * @author      Andy Seaborne
 * @version     $Id: ProcessorCom.java,v 1.8 2004-11-12 16:41:38 andy_seaborne Exp $
 */
public abstract class ProcessorCom implements Processor, Loadable
{
    private static Log log = LogFactory.getLog(ProcessorCom.class) ;
    
    protected String operationName = null ;
    protected boolean readOnly = false ;

    public ProcessorCom(String opName, int lockType)
    {
        switch (lockType)
        {
            case LockType.ReadOperation: readOnly = true ; break ;
            case LockType.WriteOperation: readOnly = false ; break ;
            default:
                log.warn("Unknown lockType: "+lockType) ;
                readOnly = false ;
        }
        
        operationName = opName ;
    }

    
    /** @see org.joseki.server.Processor */
    public void exec(Request request, Response response) throws ExecutionException
    {
        SourceModel src = request.getSourceModel() ;
        if ( !readOnly && src.isImmutable() )
            throw new ExecutionException(ExecutionError.rcImmutableModel, "Immutable Model") ;
        
        boolean needsAbortOperation = false ; 
        try {
            src.startOperation(readOnly) ;
            needsAbortOperation = true ;
            execute(request, response) ;
            src.endOperation() ;
            needsAbortOperation = false ;
        }
        catch (ExecutionException ex)
        {
            response.setResponseCode(ex.returnCode) ;
            // Send error.
            // FIXME
            log.warn("NOT TRANSLATED: ExecutionException: "+ex.getMessage() ) ;
            //try { response.getOutput().write(ex.getMessage().getBytes()) ; }
            //catch (java.io.IOException ex2) { log.fatal("PANIC",ex2) ; return ; }
            needsAbortOperation = true ;
            log.trace("ExecutionException: "+ex.getMessage() ) ;
            throw ex ;
        }
        catch (Exception ex)
        {
            needsAbortOperation = false ;
            src.abortOperation() ;
            log.trace("Exception: "+ex.getMessage() ) ;
            throw new ExecutionException(ExecutionError.rcInternalError, null) ;
        }
        finally
        {
            if ( needsAbortOperation )
            {
                needsAbortOperation = false ;
                src.abortOperation();
            }
        }
    }
    
    /** @see org.joseki.server.module.Loadable#init(Resource, Resource)
     */
    public void init(Resource processor, Resource implementation) { }

    
    /** Same as Processor.exec except that it is inside a
     * transaction or MRSW lock when this is called
     * @see Processor#exec(Request, Response)
     */ 
    public abstract void execute(Request request, Response response) throws ExecutionException ;
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
