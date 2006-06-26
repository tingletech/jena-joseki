/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.processors;


import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.shared.LockMRSW;

import org.joseki.*;


public abstract class QueryCom implements Processor
{
    public Lock lock = new LockMRSW() ;
    
    /** Execute a query without a lock */ 
    public void exec(Request request, Response response, DatasetDesc datasetDesc) throws ExecutionException
    {
        
        if ( datasetDesc != null && datasetDesc.getDataset() != null )
        {
            // Lock
            // Or move locking into SPARQL and do when the source of the dataset is decides.
        }
        // Dataset ds = getDataset(request) ;
        // Do locking on dataset
        // TODO Transactional lock
        
        lock.enterCriticalSection(Lock.READ) ;
        try {
            execQuery(request, response, datasetDesc) ;
        } finally { lock.leaveCriticalSection() ; }
    }
    
    /** Execute a query within an MRSW lock */ 
    public abstract void execQuery(Request request, Response response, DatasetDesc datasetDesc) throws QueryExecutionException ;
}

/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
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