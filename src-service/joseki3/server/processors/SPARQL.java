/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package joseki3.server.processors;

import com.hp.hpl.jena.rdf.model.*;

import org.apache.commons.logging.*;

import joseki3.server.*;
import joseki3.server.module.Loadable;


public class SPARQL extends QueryCom implements Loadable
{
    static Log log = LogFactory.getLog(SPARQL.class) ;
    static final Property allowDatasetDescP = ResourceFactory.createProperty(JosekiVocab.getURI(), "allowExplicitDataset") ;
    static final Property allowWebLoadingP = ResourceFactory.createProperty(JosekiVocab.getURI(), "allowWebLoading") ;

    static Model m = ModelFactory.createDefaultModel() ;
    
    static final Literal XSD_TRUE   = m.createTypedLiteral(true) ; 
    static final Literal XSD_FALSE  = m.createTypedLiteral(false) ;
    
    boolean allowDatasetDesc = false ;
    boolean allowWebLoading  = false ;
    
    public void init(Resource service, Resource implementation)
    {
        log.info("Init SPARQL processor") ;

        if ( service.hasProperty(allowDatasetDescP, XSD_TRUE) )
            allowDatasetDesc = true ;
        if ( service.hasProperty(allowWebLoadingP, XSD_TRUE) )
            allowWebLoading = true ;

        log.info("Dataset description: "+allowDatasetDesc+" // Web loading: "+allowWebLoading) ;
    }

    void execQuery(Request request, Response response) throws QueryExecutionException
    {
        log.info("Request: "+request.paramsAsString()) ;
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