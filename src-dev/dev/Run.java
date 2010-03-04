/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;

import org.joseki.http.AcceptItem;
import org.joseki.http.HttpUtils;
import org.joseki.util.RunUtils;

public class Run
{
    public static void main(String[] argv)
    {
        /*
         * 
         * http://www.sparql.org/sparql?query=PREFIX+foaf%3A+++%3Chttp%3A%2F%2Fxmlns.com%2Ffoaf%2F0.1%2F%3E%0D%0APREFIX+dc%3A++++++%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0ASELECT+*%0D%0AFROM++%3Chttp%3A%2F%2Fmmt.me.uk%2Ffoaf.rdf%3E%0D%0AWHERE+%0D%0A++{+%3Fs+%3Fp+%3Fo+}&default-graph-uri=http://mmt.me.uk/foaf.rdf&stylesheet=%2Fxml-to-html.xsl&output=text 
         */
        if ( false )
        {
            AcceptItem a = new AcceptItem("foo/bar;charset=bar") ;
            System.out.println(a.getType()) ;
            System.out.println(a.getSubType()) ;
            System.out.println(a.getAcceptType()) ;
            System.out.println("***") ;
            System.exit(0) ;
        }
        RunUtils.setLog4j() ;
        joseki.rdfserver.main(new String[]{"joseki-config-tdb.ttl"}) ;
    }
    
    public static void misc()
    {
        //String f = httpRequest.getHeader(headerAccept) ;
        String x ;
        x = HttpUtils.match("*/*", "text/*") ;
        x = HttpUtils.match("text/html", "text/*") ;
        x = HttpUtils.match("application/xml", "text/*") ;
        
        String ff = "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5" ;
        x = HttpUtils.match(ff, "text/*") ;
    }
}

/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
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