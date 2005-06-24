/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki;

import java.util.* ;

/** Constants and other definitions.
 * @author      Andy Seaborne
 * @version     $Id: Joseki.java,v 1.15 2005-06-24 19:48:36 andy_seaborne Exp $
 */
public class Joseki
{
    public static final int defaultPort = 2020 ; 
    // TODO automate version number (read from file?) 
    public static String version = "3.0-dev" ;
    public static String httpHeaderField = "X-Joseki-Server" ;
    public static String httpHeaderValue = "Joseki-"+version ;
    
    // TODO Split constants into client-side and server-side constants.
    
    //public static final String baseURI = "http://joseki.org/" ;

    public static final String contentTypeAppN3        = "application/n3" ;
    public static final String contentTypeTurtle       = "application/turtle" ;
    public static final String contentTypeRDFXML       = "application/rdf+xml" ;
    public static final String contentTypeNTriples     = "application/n-triples" ;
    public static final String contentTypeXML          = "application/xml" ;

    public static final String contentTypeTextPlain    = "text/plain" ;
    public static final String contentTypeTextN3       = "text/n3" ;
    public static final String contentTypeForText      = contentTypeTextPlain ;
    
    public static final String charsetUTF8             = "utf-8" ;
    
    public static String serverContentType             = contentTypeRDFXML ;
    public static String clientContentType             = contentTypeRDFXML ;
    
    // Various control falgs and settings.

    public static boolean serverHttpExplicitNoCache = true ;
    //public static boolean serverDebug = false ;
    //public static boolean clientDebug = false ;
    
    public static String getReaderType(String contentType)
    {
        return (String)jenaReaders.get(contentType) ;
    }

    public static String getWriterType(String contentType)
    {
        return (String)jenaWriters.get(contentType) ;
    }

    public static String setReaderType(String contentType, String writerName)
    {
        return (String)jenaReaders.put(contentType, writerName) ;
    }

    public static String setWriterType(String contentType, String writerName)
    {
        return (String)jenaWriters.put(contentType, writerName) ;
    }
    
    static Map jenaReaders = new HashMap() ;
    static {
        setReaderType(contentTypeAppN3, "N3") ;
        setReaderType(contentTypeTextN3, "N3") ;
        setReaderType(contentTypeRDFXML, "RDF/XML") ;
        setReaderType(contentTypeNTriples, "N-TRIPLE") ;
        setReaderType(contentTypeXML, "RDF/XML") ;
        setReaderType(contentTypeTurtle, "TURTLE") ;
        
    }
    
    static Map jenaWriters = new HashMap() ;
    static {
        setWriterType(contentTypeXML, "RDF/XML-ABBREV") ;
        setWriterType(contentTypeAppN3, "N3") ;
        setWriterType(contentTypeRDFXML, "RDF/XML-ABBREV") ;
        setWriterType(contentTypeNTriples, "N-TRIPLE") ;
        setWriterType(contentTypeTurtle, "TURTLE") ;
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
 
