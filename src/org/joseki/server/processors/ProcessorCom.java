/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;

import org.joseki.server.*;
import org.joseki.server.module.* ;
import com.hp.hpl.jena.rdf.model.*;

/** General template 
 * @author      Andy Seaborne
 * @version     $Id: ProcessorCom.java,v 1.1 2004-11-03 10:15:03 andy_seaborne Exp $
 */
public abstract class ProcessorCom implements Processor, Loadable
{
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

    /** @see org.joseki.server.Processor#init(Resource, Resource)
     */
    public void init(Resource processor, Resource implementation) { }
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
