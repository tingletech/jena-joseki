/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.test;

import java.io.* ;
import org.apache.commons.logging.*;

import junit.framework.*;
import org.joseki.server.* ;
import org.joseki.server.processors.*;
import org.joseki.server.source.*;


import com.hp.hpl.jena.rdf.model.*;

/** Tests of the mechanisms (attach, dispatch) of the server side.
 * 
 * @author      Andy Seaborne
 * @version     $Id: JosekiServerInternalTests.java,v 1.2 2004-11-15 15:28:03 andy_seaborne Exp $
 */
public class JosekiServerInternalTests extends TestSuite
{
    static Log log = LogFactory.getLog(ClientLibraryTest.class) ;
    
    static public TestSuite suite() {
        return new JosekiServerInternalTests() ;
    }
    
    
    static final String context = "Joseki Internal Tests" ;
    static final String host = "http://test.org/" ;
    static final String baseName = host+"namespace#" ;
    int testCounter = 1 ;
    
    public JosekiServerInternalTests()
    {
        super("Joseki Internal Tests") ;
        init() ;
        PrintWriter writer = null ;
        try {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"))) ;
        } catch (java.io.UnsupportedEncodingException ex) {}
        addTest(new AddTest(writer, "HttpAdd-"+(testCounter++))) ;
        addTest(new RemoveTest(writer, "Remove-"+(testCounter++))) ;
        addTest(new QueryTest(writer, "Query-"+(testCounter++))) ;
    }
    
    private void init()
    {
    }
    
    abstract class Test extends TestCase
    {
        String modelURI = "/test" ;
        String requestURL = "http://example.org/test" ;
        Dispatcher dispatcher ;
        Model targetModel = null ;  // The server side model
        PrintWriter out ;
            
        Test(PrintWriter w, String testName)
        {
            super("Joseki Internal test: "+testName) ;
            out = w ;
        }
    
        protected void setUp()
        {
            dispatcher = new Dispatcher() ;
            DispatcherRegistry.getInstance().add(context, dispatcher) ;
            dispatcher = DispatcherRegistry.getInstance().find(context) ;
            targetModel = ModelFactory.createDefaultModel() ;
            SourceModel aModel = new SourceModelPermanent(null, targetModel, modelURI) ;
            dispatcher.addSourceModel(aModel, aModel.getServerURI()) ;
            dispatcher.addQueryProcessor(aModel.getServerURI(), "RDQL", new QueryProcessorRDQL()) ; 
            dispatcher.addQueryProcessor(aModel.getServerURI(), "GET", new QueryProcessorGET()) ; 
            dispatcher.addProcessor(aModel.getServerURI(), "add", new AddProcessor()) ; 
            dispatcher.addProcessor(aModel.getServerURI(), "remove", new RemoveProcessor()) ; 
        }
        
        abstract protected void runTest() throws Throwable ;
        
        protected void tearDown()
        {
            dispatcher.removeSourceModel(modelURI) ;
            DispatcherRegistry.getInstance().remove(context) ;
        }
    
        // Building block operations
        
        protected Model perform(String opName, Model m) throws ExecutionException
        {
            log.info(getName()+": "+opName+" :: "+modelURI) ;
            Request request = //dispatcher.createOperation(modelURI, requestURL, opName) ;
                new RequestImpl(modelURI, requestURL, opName, null) ;
            SourceModel src = dispatcher.findModel(modelURI) ;
            request.setSourceModel(dispatcher.findModel(modelURI)) ;
            request.setProcessor(dispatcher.findProcessor(src, opName)) ;

            if ( m != null )
                request.addArg(m) ;
            Model resultModel = dispatcher.exec(request) ;
            return resultModel ;
        }
        
        protected void dumpModel(Model m) throws Exception
        {
            m.write(out, "N3") ;
            out.flush() ;
        }

    }   

    
    class AddTest extends Test
    {
        AddTest(PrintWriter w, String testName) { super(w, testName) ; }
        
        protected void runTest() throws Throwable
        {
            Model m = ModelFactory.createDefaultModel() ;
            Property p = m.createProperty(baseName+"p") ;
            Resource r = m.createResource(host+"res") ;
            String object = "value" ;
            m.add(r,p ,object) ;
            Model resultModel = perform("add", m) ;
            assertNotNull(resultModel) ;
            assertTrue(targetModel.contains(r, p, object)) ;
        }
    }
    
    
    class RemoveTest extends Test
    {
        RemoveTest(PrintWriter w, String testName) { super(w, testName) ; }
        protected void runTest() throws Throwable
        {
            // HttpAdd one statement
            {
                Model m = ModelFactory.createDefaultModel() ;
                Property p = m.createProperty(baseName+"p") ;
                Resource r = m.createResource(host+"res") ;
                String object = "value" ;
                m.add(r, p, object) ;
                Model resultModel = perform("add", m) ;

                assertNotNull(resultModel) ;
                assertTrue(targetModel.contains(r, p, object)) ;
                assertTrue(targetModel.isIsomorphicWith(m)) ;
            }
            // Now remove it.
            {
                Model m = ModelFactory.createDefaultModel() ;
                Property p = m.createProperty(baseName+"p") ;
                Resource r = m.createResource(host+"res") ;
                String object = "value" ;
                m.add(r, p, object) ;
                Model resultModel = perform("remove", m) ;

                assertNotNull(resultModel) ;
                assertTrue(! targetModel.contains(r, p, object)) ;
                boolean passesTest = targetModel.isIsomorphicWith(ModelFactory.createDefaultModel()) ;
                
                if ( ! passesTest )
                {
                    out.println(this.getName()+" Expected --------------------------") ;
                    dumpModel(targetModel) ;
                    out.println(this.getName()+" -----------------------------------") ;
                }
                    
                assertTrue(passesTest) ;
                
            }
        }
    }
    
    class QueryTest extends Test
    {
        QueryTest(PrintWriter w, String testName) { super(w, testName) ; }
        protected void runTest() throws Throwable
        {
            // HttpAdd one statement
            {
                Model m = ModelFactory.createDefaultModel() ;
                Property p = m.createProperty(baseName+"p") ;
                Resource r = m.createResource(host+"res") ;
                String object = "value" ;
                m.add(r, p, object) ;
                Model resultModel = perform("add", m) ;

                assertNotNull(resultModel) ;
                assertTrue(targetModel.contains(r, p, object)) ;
                assertTrue(targetModel.isIsomorphicWith(m)) ;
            }
            // Query it
            {
                log.info(getName()+": query :: "+modelURI);
                String queryLang = "RDQL" ;
                Request request = new RequestImpl(modelURI, requestURL, "query", queryLang) ;
                SourceModel src = dispatcher.findModel(modelURI) ;
                request.setSourceModel(src) ;
                request.setProcessor(dispatcher.findQueryProcessor(src, queryLang)) ;
                request.setParam("lang", queryLang) ;
                request.setParam("query", "SELECT * WHERE (?x, ?y, ?z)") ;
                Model resultModel = dispatcher.exec(request) ;
                assertNotNull(resultModel) ;
                boolean passesTest = targetModel.isIsomorphicWith(resultModel) ;
                if ( ! passesTest )
                {
                    out.println(this.getName()+" Expected --------------------------") ;
                    dumpModel(targetModel) ;
                    out.println(this.getName()+" Got -------------------------------") ;
                    dumpModel(resultModel) ;
                    out.println(this.getName()+" -----------------------------------") ;
                }
                
                assertTrue(passesTest) ;
            }
        }
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
