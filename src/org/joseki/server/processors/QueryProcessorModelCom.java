/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;

import org.apache.commons.logging.*;
import org.joseki.server.*;
import org.joseki.server.module.Loadable ; 
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.RDFException;

/** Query processor framework that returns the whole model.
 *  Queries can be supplied by string (e.g. HTTP GET) or
 *  ad a model (e.g. HTTP POST).
 * 
 * @author      Andy Seaborne
 * @version     $Id: QueryProcessorModelCom.java,v 1.2 2004-11-15 16:21:50 andy_seaborne Exp $
 */
public abstract class QueryProcessorModelCom implements QueryProcessorModel, Loadable
{
    // FIXME No lock!
    static final Log logger = LogFactory.getLog(QueryProcessorModelCom.class.getName()) ; 

    boolean readOnlyLock = true ;

    public QueryProcessorModelCom() { }

    /** @see org.joseki.server.module.Loadable#init(Resource, Resource)
     */
    public void init(Resource processor, Resource implementation) { }

    /**
     * @see org.joseki.server.ProcessorModel#exec(Request)
     */
    public Model exec(Request request) throws ExecutionException
    {
        SourceModel aModel = request.getSourceModel() ;
        // May be null (no string supplied) in which case the query may be a model.
        String queryString = request.getParam("query");
        // Actually this will be "" for a POSTed query - fix.
        if (queryString != null && queryString.equals(""))
            queryString = null;

        if (queryString != null && request.getDataArgs().size() > 0)
            throw new ExecutionException(
                ExecutionError.rcQueryExecutionFailure,
                "Query has string and model arguments");

        // Removed from Joseki3
//        if (request.getDataArgs().size() > 0)
//            // Execute via model - removed.
//            return execQuery(aModel, (Model) request.getDataArgs().get(0), request);

        // Execute via query string
        return execQuery(aModel, queryString, request);
    }

    /** Query processors supply this, rather than the <code>exec</code> method.
     *  The queryString may be null (no string supplied).
     *  @see QueryProcessorModel#execQuery
     */

    abstract public Model execQuery(SourceModel target, String queryString, Request request) throws RDFException, QueryExecutionException ;
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
