/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joseki.ExecutionError;
import org.joseki.ExecutionException;
import org.joseki.QueryExecutionException;
import org.joseki.Request;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;


abstract public class NResponse
{
    private static Log log = LogFactory.getLog(NResponse.class) ;

    private ResponseCallback callback = null ;
    private Model responseModel = null ;
    private ResultSet responseResultSet = null ;
    private boolean done = false ;
    protected Request request ;
    
    protected NResponse(Request request)
    { this.request = request ; }
    
    public void setCallback(ResponseCallback callback)
    { this.callback = callback ; }
    
    public void setModel(Model model) throws QueryExecutionException
    { checkState() ; this.responseModel = model ; }
    
    public void setResultSet(ResultSet rs) throws QueryExecutionException
    { checkState() ; this.responseResultSet = rs ; }
    
    public void sendException(ExecutionException execEx)
    {
        if ( done )
        {
            log.fatal("doException: Response already sent: "+request.getRequestURL()) ;
            return ;
        }
        doException(execEx) ;
    }
    
    public void sendResponse() throws QueryExecutionException
    {
        if ( done )
        {
            log.warn("Already sent response") ;
            return ;
        }
        
        if ( responseModel == null && responseResultSet == null)
        {
            log.warn("Nothing to send as a response") ;
            throw new QueryExecutionException(ExecutionError.rcInternalError,
                                              "Nothing to send as response") ;
        }
        
        done = true ;
        if ( responseModel != null )
            doResponseModel(responseModel) ;
        if ( responseResultSet != null )
            doResponseResultSet(responseResultSet) ;
        
        responseModel = null ;
        responseResultSet = null ;
        if( callback != null )
            callback.exec() ;
        return ;
    }
    
    abstract protected void doResponseModel(Model model)  throws QueryExecutionException ;
    
    abstract protected void doResponseResultSet(ResultSet resultSet)  throws QueryExecutionException ;
    
    abstract protected void doException(ExecutionException execEx) ;
    
    private void checkState() throws QueryExecutionException
    {
       if ( done )
       {
           log.warn("State error: already done") ;
           throw new QueryExecutionException(ExecutionError.rcInternalError,
                                             "State error: already done") ;
       }
       if ( responseModel != null )
       {
           log.warn("State error: model already set") ;
           throw new QueryExecutionException(ExecutionError.rcInternalError,
                                             "State error: model already set") ;
       }
       if ( responseResultSet != null )
       {
           log.warn("State error: result set already set") ;
           throw new QueryExecutionException(ExecutionError.rcInternalError,
                                             "State error: result set already set") ;
       }
    }

}

/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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