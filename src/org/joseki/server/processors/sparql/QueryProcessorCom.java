/*
 * (c) Copyright 2004, Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.server.processors.sparql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joseki.server.*;
import org.joseki.server.processors.ProcessorCom;

/** QueryProcessorCom - the root of query processors.
 * 
 * @author Andy Seaborne
 * @version $Id: QueryProcessorCom.java,v 1.1 2004-11-11 17:03:36 andy_seaborne Exp $
 */

public abstract class QueryProcessorCom extends ProcessorCom implements QueryProcessor
{
    Log log = LogFactory.getLog(QueryProcessorCom.class) ;
    
    public QueryProcessorCom(String name)
    {
        super(name, ProcessorCom.ReadOperation, ProcessorCom.NoChangeToModel) ;
    }
    
    public void execute(Request request, Response response) throws ExecutionException
    {
        SourceModel aModel = request.getSourceModel() ;
        // Inside lock.
        // Convert from Processor.exec to QueryProcessor.execQuery
        String queryLangName = request.getParam("lang") ;
        String queryString = request.getParam("query") ;
        
        if ( queryLangName == null )
        {
            log.warn("No query language name or URI found") ;
             throw new QueryExecutionException(Response.rcQueryExecutionFailure,
                                               "No query language name or URI found");
        }
        
        QueryProcessor qProc = null ;
        log.fatal("NOT IMPLEMENTED: Dispatcher holding QueryProcessor") ;
        if ( true )
            throw new QueryExecutionException(Response.rcNotImplemented,
                                              "Dispatcher holding QueryProcessor") ;
        
        // Or has the WebAPI code done all this?
        
        //qProc = aModel.getProcessorRegistry().findQueryProcessor(queryLangName);
        if (qProc == null )
            throw new QueryExecutionException(ExecutionError.rcOperationNotSupported,
                                         "No query language found") ;
        qProc.execQuery(aModel, queryString, request, response) ;
    }
}

/*
 * (c) Copyright 2004 Hewlett-Packard Development Company, LP
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