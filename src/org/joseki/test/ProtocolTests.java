/*
 * (c) Copyright 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.LocationMapper;


import junit.extensions.TestSetup;
import junit.framework.*;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.joseki.RDFServer;
import org.joseki.junit.ProtocolTestSuiteFactory;
import org.joseki.util.RunUtils;


public class ProtocolTests extends TestCase
{
    public static TestSuite suite()
    {
        RunUtils.setLog4j() ;
        
        TestSuite ts = new TestSuite("DAWG Protocol") ;
        ts.addTest(ProtocolTestSuiteFactory.make("testing/DAWG/select/manifest.ttl")) ;
        ts.addTest(ProtocolTestSuiteFactory.make("testing/DAWG/construct/manifest.ttl")) ;
        ts.addTest(ProtocolTestSuiteFactory.make("testing/DAWG/ask/manifest.ttl")) ;
        ts.addTest(ProtocolTestSuiteFactory.make("testing/DAWG/describe/manifest.ttl")) ;
        TestSuite tsWrapped = new TestSuite() ;
        tsWrapped.addTest(new ServerSetup(ts)) ;
        return tsWrapped ;
    }
    
}

class ServerSetup extends TestSetup
{
    RDFServer server = null ;
    
    public ServerSetup(Test test)
    {
        super(test) ;
    }
    
    @Override
    protected void setUp()
    {
        Model m = FileManager.get().loadModel("testing/location-mapping.ttl") ;
        LocationMapper.get().processConfig(m);
        //dev.RunUtils.setLog4j() ;
        //LogManager.getLogger("org.joseki.Configuration").setLevel(Level.INFO) ;
        LogManager.getLogger("org.mortbay").setLevel(Level.WARN) ;
        server = new RDFServer("testing/config-dawg-tests.ttl") ; 
        server.start() ;
        server.waitUntilStarted() ;
    }
    
    @Override
    protected void tearDown()
    { server.stop() ; }
}

/*
 * (c) Copyright 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
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