/*
 * (c) Copyright 2004, Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.server.http;

import junit.framework.TestCase;

/** org.joseki.server.http.TestContentNegotiation
 * 
 * @author Andy Seaborne
 * @version $Id: TestContentNegotiation.java,v 1.2 2004-11-27 19:35:28 andy_seaborne Exp $
 */

public class TestContentNegotiation extends TestCase
{
    static final String ctApplicationXML     =  "application/xml" ;
    static final String ctApplicationRDFXML  =  "application/rdf+xml" ;
    static final String ctApplicationStar    =  "application/*" ;
    // Legal?? */xml
    
    static final String ctTextPlain          =  "text/plain" ;
    static final String ctTextXML            =  "text/xml" ;
    static final String ctTextStar           =  "text/*" ;
    
    static final String ctStarStar           = "*/*" ;
    
    // It's easier to just write the content types in strings. 
    public void testSimpleNeg1() { testMatch("text/plain", "text/plain", "text/plain") ; }
    public void testSimpleNeg2() { testMatch("application/xml", "text/plain", null) ; }
    public void testSimpleNeg3() { testMatch("text/*", "text/*", "text/*") ; }
    
    public void testSimpleNeg4() { testMatch("text/xml", "text/*", "text/xml") ; }
    public void testSimpleNeg5() { testMatch("text/*", "text/xml", "text/xml") ; }
    
    
    
    public void testListItemNeg1() { testMatch("text/xml,text/*", "text/*", "text/xml") ; }
    
    public void testListListNeg1()
    { testMatch("text/xml,text/*", "text/plain,text/*", "text/plain") ; }
    
    public void testListListNeg2()
    { testMatch("text/xml,text/*", "text/*,text/plain", "text/xml") ; }

    
    public void testQualNeg1() { testMatch("text/xml;q=0.5,text/plain", "text/*", "text/plain") ; }
    
    
    // Worker.  Does request 'header' match server 'offer' with 'result'?
    private void testMatch(String header, String offer, String result)
    {
        AcceptList list1 = new AcceptList(header) ;
        AcceptList list2 = new AcceptList(offer) ;
        AcceptItem matchItem = AcceptList.match(list1, list2) ;
        
        
        if ( result == null )
        {
            assertNull("Match not null: from "+q(header)+" :: "+q(offer),
                       matchItem) ;
            return ;
        }
        
        
        assertNotNull("Match is null: expected "+q(result), matchItem) ;
        
        assertEquals("Match different", result, matchItem.getAcceptType()) ;
    }
    
    private String q(Object obj)
    {
        if ( obj == null )
            return "<null>" ;
        return "'"+obj.toString()+"'" ;
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