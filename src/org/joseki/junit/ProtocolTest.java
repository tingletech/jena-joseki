/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.junit;

import java.io.InputStream;

import junit.framework.TestCase;

import com.hp.hpl.jena.query.engineHTTP.HttpQuery;
import com.hp.hpl.jena.query.engineHTTP.Params;
import com.hp.hpl.jena.query.engineHTTP.QueryExceptionHTTP;

public class ProtocolTest extends TestCase
{
    HttpQuery httpQuery = null ;
    int responseCode = -1 ;
    String acceptType ;
    String responseType ;
    
    public ProtocolTest(String name, String target, String acceptType, int response,  String responseType)
    { 
        super(name) ;
        httpQuery = new HttpQuery(target) ;
        responseCode = response ;
        this.acceptType = acceptType ;
        this.responseType = responseType ;
    }
        
    public Params getParams() { return httpQuery ; }
    public HttpQuery getHttpQuery() { return httpQuery ; }

    
    
    protected void runTest() throws Exception
    {
        if ( acceptType != null )
            httpQuery.setAccept(acceptType) ;
        try {
            InputStream in = httpQuery.exec() ;
            assertEquals(responseCode, httpQuery.getConnection().getResponseCode() ) ;
        } catch (QueryExceptionHTTP ex)
        {
            int rc = ex.getResponseCode() ;
            if ( responseCode == 200 )
                fail("Got an error response ("+rc+") when expecting OK") ;
            assertEquals(responseCode, rc) ;
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