/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.test;

import java.io.* ;
import org.apache.commons.logging.* ;
import junit.framework.*;
import com.hp.hpl.jena.joseki.* ;

import com.hp.hpl.jena.rdf.model.* ;

/** Harness for execution client library tests.
 * Abstraction is that an operation should be performed
 * and result in either a result model (all operations are
 * operation: model->model) or an HttpExpection.
 * 
 * @author      Andy Seaborne
 * @version     $Id: ClientLibraryTest.java,v 1.2 2005-01-03 20:26:35 andy_seaborne Exp $
 */

abstract class ClientLibraryTest extends TestCase
{
    final Model emptyModel = ModelFactory.createDefaultModel() ;
    PrintWriter out ;               // Reporting channel

    String modelURI ;               // Target For operation
    boolean expectedToSucceed ;     // Expected outcome
    Model expectedResult = null ;
    HttpException expectedException = null ;
    
    static Log log = LogFactory.getLog(ClientLibraryTest.class) ;
    
    private ClientLibraryTest(PrintWriter w, String testName, String targetURI)
    {
        super("Joseki HTTP test: "+testName) ;
        out = w ;
        modelURI = targetURI ;
    }
    
    /**  Test an operation that should succed with
     * a specific result.
     * @param w
     * @param testName
     * @param targetURI
     * @param resultModel
     */
    
    ClientLibraryTest(PrintWriter w, String testName, String targetURI, Model resultModel)
    {
        this(w, testName, targetURI) ;
        expectedToSucceed = true ;
        expectedResult = resultModel ;
    }

    /**  Test an operation that should fail with a specific exception
     * @param w
     * @param testName
     * @param targetURI
     * @param httpEx
     */    
    ClientLibraryTest(PrintWriter w, String testName, String targetURI, HttpException httpEx)
    {
        this(w, testName, targetURI) ;
        expectedToSucceed = false ;
        expectedException = httpEx ;
    }

    /**  Test an operation that should fail with a specific response code
     * 
     * @param w
     * @param testName
     * @param targetURI
     * @param responseCode
     */
    ClientLibraryTest(PrintWriter w, String testName, String targetURI, int responseCode)
    {
        this(w, testName, targetURI) ;
        expectedToSucceed = false ;
        expectedException = new HttpException(responseCode) ;
    }

    /** ClientLibraryTest an operation but don't know the outcome expcept
     *  whether it sould work or not.
     * @param w
     * @param testName
     * @param shouldWork
     */
    
    ClientLibraryTest(PrintWriter w, String testName, String targetURI, boolean shouldWork)
    {
        this(w, testName, targetURI) ;
        expectedToSucceed = shouldWork ;
    }            


    final protected void runTest() throws Throwable
    {
        try {
            setUpTest() ;
        } catch (HttpException httpEx)
        {
            assertTrue("Setting up failed", false) ;
        }
        
        try {
            log.info(getName()+" :: "+modelURI) ;
            Model model = performTest() ;
            assertTrue(this.getName()+" Test succeed but should not have", expectedToSucceed) ;
            assertNull(this.getName()+" Test succeed but expection registered", expectedException) ;
            if ( expectedResult != null )
            {
                boolean passesTest = model.isIsomorphicWith(expectedResult) ;
                 
                if ( ! passesTest )
                {
                    out.println(this.getName()+" Expected --------------------------") ;
                    dumpModel(expectedResult) ;
                    out.println(this.getName()+" Got -------------------------------") ;
                    dumpModel(model) ;
                    out.println(this.getName()+" -----------------------------------") ;
                }
                assertTrue(this.getName()+" Result models not as expected", passesTest) ;
            }
        } catch (HttpException httpEx)
        {
            if ( expectedToSucceed )
                System.err.println(getName()+":: HttpException: "+org.joseki.util.Convert.decWWWForm(httpEx.getMessage())) ;
            assertTrue(getName()+" was supposed to work! "+httpEx, !expectedToSucceed) ;
            if ( expectedException != null )
            {
                if ( expectedException.getResponseCode() != httpEx.getResponseCode() )
                {
                    out.println(
                        this.getName()+" Mismatch in response codes: Expected: "
                            + expectedException.getResponseCode()
                            + " Got:"
                            + httpEx.getResponseCode());
                    out.flush() ;
                }
                assertTrue(
                    this.getName()
                        + " [E:"
                        + expectedException.getResponseCode()
                        + "/G:"
                        + httpEx.getResponseCode()
                        + "]",
                    expectedException.getResponseCode() == httpEx.getResponseCode());
            }
        }
        
        try {
            takeDownTest() ;
        } catch (HttpException httpEx)
        {
            assertTrue("Clearing up failed", false) ;
        }
        
    }

    abstract protected Model performTest() throws Throwable ;
    
    /** setUpTest is like JUnit setUp expect it is inside the test
     * @throws Throwable
     */
    protected void setUpTest() throws Throwable
    { }
    
    /** takeDownTest is like Junit tearDown expect it is inside the test
     * @throws Throwable
     */
    protected void takeDownTest() throws Throwable
    { }
    
    
    
    protected void dumpModel(Model m) throws Exception
    {
        m.write(out, "N3") ;
        out.flush() ;
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
 

