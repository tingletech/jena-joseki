/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;

import com.hp.hpl.jena.query.junit.SimpleTestRunner;

import junit.framework.TestSuite;
import org.joseki.RDFServer;
import org.joseki.junit.ProtocolTest;
import org.joseki.junit.ProtocolTestSuiteFactory;

import org.apache.log4j.* ;

public class RunTest
{
    public static void main(String[] argv)
    {
        RunUtils.setLog4j() ;
        LogManager.getLogger("org.joseki").setLevel(Level.WARN) ;
        LogManager.getLogger("org.mortbay").setLevel(Level.WARN) ;

        RDFServer s = new RDFServer() ; 
        s.start() ;
        
        TestSuite ts = ProtocolTestSuiteFactory.make("testing/DAWG/select/manifest.ttl",
                                                     "http://localhost:2020/sparql") ;

        if ( false )
        {
            for ( int i = 0 ; i < ts.testCount() ; i++ )
            {
                ProtocolTest t = (ProtocolTest)ts.testAt(i) ;
                System.out.println(t.getHttpQuery().toString()) ;
            }
        }
        
        SimpleTestRunner.runAndReport(ts) ;
        s.stop() ;
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