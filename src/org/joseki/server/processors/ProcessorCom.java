/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;

import org.apache.commons.logging.* ;
import org.joseki.server.*;
import org.joseki.server.module.* ;
import com.hp.hpl.jena.rdf.model.*;

/** General template 
 * @author      Andy Seaborne
 * @version     $Id: ProcessorCom.java,v 1.3 2004-11-04 15:44:52 andy_seaborne Exp $
 */
public abstract class ProcessorCom implements ProcessorModel, Processor, Interface, Loadable
{
    Log logger = LogFactory.getLog(ProcessorCom.class) ;
    
    // Useful constant
    static protected Model emptyModel = ModelFactory.createDefaultModel() ;
    
    static protected final int ReadOperation    = 100 ;
    static protected final int WriteOperation   = 101 ;

    static protected final int MutatesModel     = 102 ;
    static protected final int NoChangeToModel  = 103 ;
    
    protected String opName = null ;
    protected boolean readOnlyLock = false ;
    protected boolean mutatingOp = true ;

    public ProcessorCom(String n, int lockNeeded, int mutating)
    {
        opName = n ;
        readOnlyLock = (lockNeeded == ReadOperation) ;
        mutatingOp = (mutating == MutatesModel) ;
    }

    
    /** @see org.joseki.server.Processor */
    public void exec(Request request, Response response) throws ExecutionException
    {
        logger.fatal("Not implemented") ;
        throw new ExecutionException(ExecutionError.rcNotImplemented, 
                                     "ProcessorCom.exec(Request, Response)") ;
    }
    
    
    /** @see org.joseki.module.Loadable#init(Resource, Resource)
     */
    public void init(Resource processor, Resource implementation) { }
    
    
    public Model exec(Request request) throws ExecutionException
    {
        SourceModel src = request.getSourceModel() ;
        if ( mutatingOp && src.isImmutable() )
            throw new ExecutionException(ExecutionError.rcImmutableModel, "Immutable Model") ;
        
        boolean needsAbortOperation = false ; 
        try {
            src.startOperation(readOnlyLock) ;
            needsAbortOperation = true ;
            Model resultModel = exec(src, request) ;
            src.endOperation() ;
            needsAbortOperation = false ;
            return resultModel ;
        }
        catch (ExecutionException ex)
        {
            needsAbortOperation = true ;
            logger.trace("RDFException: "+ex.getMessage() ) ;
            throw ex ;
        }
        catch (Exception ex)
        {
            needsAbortOperation = false ;
            src.abortOperation() ;
            logger.trace("Exception: "+ex.getMessage() ) ;
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
        //return emptyModel ;
    }

    /** Execute the operation - inside a transaction/MRSW lock when this is called */  
    public abstract Model exec(SourceModel src, Request request)  throws ExecutionException ;
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
