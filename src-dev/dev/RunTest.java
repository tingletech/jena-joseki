/*
 * (c) Copyright 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;

import junit.framework.TestSuite;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.joseki.RDFServer;
import org.joseki.junit.ProtocolTestSuiteFactory;
import org.joseki.util.RunUtils;

import com.hp.hpl.jena.sparql.junit.SimpleTestRunner;
import com.hp.hpl.jena.util.LocationMapper;

public class RunTest
{
    public static void main(String[] argv)
    {
        System.setProperty(LocationMapper.GlobalMapperSystemProperty1, "testing/location-mapping.ttl") ;
        RunUtils.setLog4j() ;
        LogManager.getLogger("org.joseki.Configuration").setLevel(Level.WARN) ;
        LogManager.getLogger("org.mortbay").setLevel(Level.WARN) ;
        //LogManager.getLogger("com.hp.hpl.jena.util").setLevel(Level.ALL) ;
        
        RDFServer s = new RDFServer() ; 
        s.start() ;
        s.waitUntilStarted() ;
        TestSuite ts = new TestSuite("DAWG Protocol") ;
        
        ts.addTest(ProtocolTestSuiteFactory.make("testing/DAWG/select/manifest.ttl")) ;
        ts.addTest(ProtocolTestSuiteFactory.make("testing/DAWG/construct/manifest.ttl")) ;
        ts.addTest(ProtocolTestSuiteFactory.make("testing/DAWG/ask/manifest.ttl")) ;
        ts.addTest(ProtocolTestSuiteFactory.make("testing/DAWG/describe/manifest.ttl")) ;
        SimpleTestRunner.runAndReport(ts) ;
        s.stop() ;
    }
    
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