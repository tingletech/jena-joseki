/*
 * (c) Copyright 2004, Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.test;

import org.joseki.server.*;

/** org.joseki.test.TestServer
 * 
 * @author Andy Seaborne
 * @version $Id: TestServer.java,v 1.1 2004-11-03 10:15:03 andy_seaborne Exp $
 */

public class TestServer 
{
    public int port         = 9990 ;
    String baseURI          = "http://localhost:"+port+"/" ;
    String testdata_URI     = baseURI+"testdata" ;
    String testdata2_URI    = baseURI+"testdata2" ;

    String noSuchModelURI   = baseURI+"noSuchModel" ;
    String scratchModelURI  = baseURI+"scratch" ;
    String emptyModelURI    = baseURI+"empty" ;
    String infRDFSModelURI  = baseURI+"inf-rdfs" ;
    String infOWLModelURI   = baseURI+"inf-owl" ;
    String fetchModelURI    = baseURI+"fetch-1" ;
    
    RDFServer server = null ;

    void createServer()
    {
        try {
            server = new RDFServer("etc/joseki-junit.n3", port) ;
            server.start() ;
        } catch (ConfigurationErrorException confEx)
        {
            System.err.println("Configuration error: "+confEx.getMessage()) ;
            //confEx.printStackTrace(System.err) ;
            server = null ;
            return ;
        }
    }
    
    void destroyServer()
    {
        server.stop() ;
        server = null ;
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