/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.test;

import junit.framework.* ;

/**
 * @author      Andy Seaborne
 * @version     $Id: JosekiTests.java,v 1.2 2005-01-03 20:26:35 andy_seaborne Exp $
 */
public class JosekiTests extends TestSuite
{

    /* JUnit swingUI needed this */
    static public TestSuite suite() {
        return new JosekiTests() ;
    }
    
    JosekiClientLibraryTests clientLibraryTests = null ;
    
    public JosekiTests()
    {
        super("Joseki test suite") ;
        
        if ( System.getProperty("log4j.configuration") == null )
            System.setProperty("log4j.configuration", "file:etc/log4j-test.properties") ;
        
//        if (System.getProperty("java.util.logging.config.file") == null)
//            System.setProperty("java.util.logging.config.file", "etc/logging-test.properties");
        
        addTest(new JosekiServerInternalTests()) ;
        TestServer s = new TestServer()  ;
        s.createServer() ;
        addTest(new JosekiClientLibraryTests(s)) ;
        //addTest()
    }

    public static void main(String[] args)
    {
        boolean verboseTests = false ;
        junit.textui.TestRunner.run(new JosekiTests()) ;
    }
}


/*
 *  (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
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
