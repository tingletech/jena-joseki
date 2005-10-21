/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.junit;

import org.joseki.vocabulary.TestProtocolVocab; 
import com.hp.hpl.jena.rdf.model.Resource;


public class ProtocolTestGenerator implements ManifestItemHandler
{
    /*
     *         [
            ptest:name "select-svcsupplied" ;
            ptest:comment "SELECT with service-supplied RDF dataset" ;
            ptest:serviceDataSet [
                ptest:defaultGraph [
                    ptest:graphName example:books;
                    ptest:graphData <http://www.w3.org/2001/sw/DataAccess/proto-tests/data/select/svcsupplied-data.ttl>
                ]
            ] ;
            ptest:query <svcsupplied-query.rq> ;
            ptest:acceptType "application/sparql-results+xml" ;
            ptest:preferredResult [
                ptest:result <svcsupplied-results.xml> ;
                ptest:resultCode "200" ;
                ptest:resultContentType "application/sparql-results+xml"
            ]
        ]

     */

    public boolean processManifestItem(Resource manifest,
                                       Resource entry, 
                                       String testName) 
//                                       Resource action,
//                                       Resource result)
    {
        String name = TestUtils.getLiteral(entry, TestProtocolVocab.name) ;
        String comment = TestUtils.getLiteral(entry, TestProtocolVocab.comment) ;
        Resource dataset = TestUtils.getResource(entry, TestProtocolVocab.serviceDataSet) ;
        String query = TestUtils.getLiteralOrURI(entry, TestProtocolVocab.query) ;
        String acceptType = TestUtils.getLiteral(entry, TestProtocolVocab.acceptType) ;
        Resource result = TestUtils.getResource(entry, TestProtocolVocab.preferredResult) ;
        System.out.println("Test: "+name) ;
        return true; 
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