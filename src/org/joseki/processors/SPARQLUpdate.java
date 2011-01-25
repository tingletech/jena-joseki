/*
 * (c) Copyright 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.processors;

import java.io.Reader ;
import java.io.StringReader ;

import org.joseki.ExecutionException ;
import org.joseki.Joseki ;
import org.joseki.QueryExecutionException ;
import org.joseki.Request ;
import org.joseki.Response ;
import org.joseki.ReturnCodes ;
import org.joseki.module.Loadable ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.rdf.model.Resource ;
import com.hp.hpl.jena.sparql.lang.ParserARQUpdate ;
import com.hp.hpl.jena.update.GraphStore ;
import com.hp.hpl.jena.update.GraphStoreFactory ;
import com.hp.hpl.jena.update.UpdateExecutionFactory ;
import com.hp.hpl.jena.update.UpdateProcessor ;
import com.hp.hpl.jena.update.UpdateRequest ;

public class SPARQLUpdate extends ProcessorBase implements Loadable
{
    private static Logger log = LoggerFactory.getLogger(SPARQLUpdate.class) ;
    
    public void init(Resource service, Resource implementation)
    {}

    @Override
    public void execOperation(Request request, Response response, Dataset dataset) throws ExecutionException
    {
        if ( ! request.getParam(Joseki.VERB).equals("POST") )
            // Because nasty things happen otherwise.
            throw new QueryExecutionException(ReturnCodes.rcBadRequest, "Updates must use POST") ;
        log.info("SPARQL/Update Operation") ;
        // Is it a form or is it a plain SPARQL/Update?
        // Touching the form causes the body to be processed. 
        
//        for ( Iterator iter= request.parameterNames() ; iter.hasNext(); )
//        {
//            System.out.println(iter.next()) ;
//        }

        // Find the request 
        // 1/ request= in the query string
        // 2/ as the body.
        
        String x = request.getParam("request") ;
        if ( x == null )
            request.getParam("update") ;

        Reader in = null ;
        if ( x == null )
            in = request.getStream() ;
        else
            in = new StringReader(x) ;
        
        if ( in == null )
        {
            ExecutionException execEx = new ExecutionException(ReturnCodes.rcArgumentError, "Can't find request from 'request' parameter or POST body") ;
            throw execEx ;
        }

        // Parsing with a Reader.  Normally discouraged because of charset issues 
        // Hence no UpdateFactory operations and a need to go direct.

        ParserARQUpdate p = new ParserARQUpdate() ;
        UpdateRequest updateRequest = new UpdateRequest() ;
        p.parse(updateRequest, in) ;
        GraphStore graphStore = GraphStoreFactory.create(dataset) ;
        UpdateProcessor uProc = UpdateExecutionFactory.create(updateRequest, graphStore) ;
        try {
            uProc.execute() ;
            response.setOK() ;
        }
        catch (Exception ex)
        {
            log.warn("Update failed", ex) ;
            ExecutionException execEx = new ExecutionException(ReturnCodes.rcUpdateExecutionFailure,"Update failed ("+ex.getMessage()+")") ;
            throw execEx ;
        }
    }
}

/*
 * (c) Copyright 2008, 2009 Hewlett-Packard Development Company, LP
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