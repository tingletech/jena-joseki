/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors.sparql;

import org.apache.commons.logging.*;
import com.hp.hpl.jena.rdf.model.*;

import org.joseki.server.ExecutionException;
import org.joseki.server.QueryExecutionException;
import org.joseki.server.Request;
import org.joseki.server.Response;
import org.joseki.server.QueryProcessor;
import org.joseki.server.SourceModel;
import org.joseki.vocabulary.JosekiVocab;

import com.hp.hpl.jena.query.* ;

/** SPARQL operations
 * 
 * @author  Andy Seaborne
 * @version $Id: SPARQL.java,v 1.4 2004-11-15 17:34:36 andy_seaborne Exp $
 */

public class SPARQL implements QueryProcessor
{
    static Log logger = LogFactory.getLog(SPARQL.class) ;
    
    

    /** QueryProcessor.execQuery */
    public void execQuery(SourceModel aModel, String queryString,
                          Request request, Response response)
    {
        try {
            throw new QueryExecutionException(Response.rcNotImplemented, "SPARQL.execQuery") ;
        } catch (QueryExecutionException qEx)
        {
            response.doException(qEx) ;
        }
    }


    
    
    public static void execQuery(SourceModel model, Query query, Response response)
        throws QueryExecutionException
    {
       if ( query.isSelectType() && response.getMimeType().equals("application/xml"))
       {
           execQueryXML(model, query, response) ;
           return ;
       }
       // Execute and get a model.
       throw new QueryExecutionException(Response.rcNotImplemented, "SPARQL.execQuery") ;
       
       // Serialize model to response.getOutput()
       
    }
    
    public static void execQueryXML(SourceModel model, Query query, Response response)
        throws QueryExecutionException
    {
        throw new QueryExecutionException(Response.rcNotImplemented, "SPARQL.execQueryXML") ;
    }

    public void exec(Request request, Response response) throws ExecutionException
    {
        throw new QueryExecutionException(Response.rcNotImplemented, "SPARQL.execQuery") ;
    }

    public String getInterfaceURI() { return JosekiVocab.queryOperationSPARQL ; }

    public void init(Resource binding, Resource implementation) {}
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
