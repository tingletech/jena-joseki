/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors;

import java.util.* ;

import org.apache.commons.logging.*;

import org.joseki.server.*;
import org.joseki.vocabulary.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.RDFException;

/** Query processor that returns the whole model.
 * 
 * @author      Andy Seaborne
 * @version     $Id: QueryProcessorGET.java,v 1.1 2004-11-03 10:15:03 andy_seaborne Exp $
 */
public class QueryProcessorGET extends QueryProcessorCom
{
    static Log log = LogFactory.getLog(QueryProcessorGET.class) ;
    
    public QueryProcessorGET()
    {   
        super() ;
    }

    public String getInterfaceURI() { return JosekiVocab.queryOperationGET ; }

    public Model execQuery(SourceModel src, String queryString, Request request)
        throws RDFException, QueryExecutionException
    {
        if (!(src instanceof SourceModelJena))
            throw new QueryExecutionException(
                ExecutionError.rcOperationNotSupported,
                "Wrong implementation - this GET processor works with Jena models");         
        Model model = ((SourceModelJena)src).getModel() ;

        if ( src.isImmutable() )
            return model ;

        // Mutable model - need to take a copy as it may change
        // or be chaning when the reply is sent.
        Model resultModel = ModelFactory.createDefaultModel() ;
        resultModel.add(model) ;
        resultModel.setNsPrefixes(model) ;
        Map m = src.getPrefixes() ;
        if ( m!= null )
            resultModel.setNsPrefixes(src.getPrefixes()) ;
        return resultModel ;
    }

    public Model execQuery(SourceModel aModel, Model queryModel, Request request)
        throws RDFException, QueryExecutionException
    {
        throw new QueryExecutionException(ExecutionError.rcOperationNotSupported,
                                     "Can't GET a model this way") ;
    }

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
