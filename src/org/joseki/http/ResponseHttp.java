/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.http;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.NotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joseki.*;


public class ResponseHttp
{
    static Log log = LogFactory.getLog(ResponseHttp.class) ;
    // TEMPORARY
    private Response response ;
    private Request request ;
    static final String paramStyleSheet = "stylesheet" ;

    ResponseHttp(Request request, Response response)
    { 
        this.request = request ; 
        this.response = response ;
        }
    
    public void doResponse(Query query, Model model)
    {
        // Refactor
        response.doResponse(model) ;
    }
    
    public void doResponse(Query query, ResultSet resultSet) throws QueryExecutionException
    {
        boolean wantsAppXML = response.accepts("Accept", "application/xml") ;
        
        if ( false )
        {
            // As model
            ResultSetFormatter rsFmt = new  ResultSetFormatter(resultSet, query.getPrefixMapping()) ;
            Model m = rsFmt.toModel() ;
            doResponse(query, m) ;
            return ;
        }
        
        String stylesheetURL = null ;
        if ( request.containsParam(paramStyleSheet) )
        {
            stylesheetURL = request.getParam(paramStyleSheet) ;
            if ( stylesheetURL != null )
            {
                stylesheetURL = stylesheetURL.trim() ;
                if ( stylesheetURL.length() == 0 )
                    stylesheetURL = null ;
            }
        }
        
        try {
            ResultSetFormatter fmt = new ResultSetFormatter(resultSet) ;
            
            response.setMimeType(Joseki.contentTypeXML) ;
            response.setResponseCode(Response.rcOK) ;
            response.startResponse() ;
            fmt.outputAsXML(response.getOutputStream(), stylesheetURL) ;
            response.finishResponse() ;
        }
        //throw new QueryExecutionException(Response.rcNotImplemented, "SPARQL.execQueryXML") ;
        catch (QueryException qEx)
        {
            log.info("Query execution error (SELECT/XML): "+qEx) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, qEx.getMessage()) ;
        }
        catch (NotFoundException ex)
        {
            log.info("Query execution error (SELECT/XML): "+ex) ;
            throw new QueryExecutionException(ExecutionError.rcQueryExecutionFailure, ex.getMessage()) ;
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