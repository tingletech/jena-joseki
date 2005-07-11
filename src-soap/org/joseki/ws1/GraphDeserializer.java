/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.ws1;

import com.hp.hpl.jena.rdf.arp.SAX2Model;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class GraphDeserializer
    extends DeserializerImpl
    implements Deserializer
{
    private static Log _log = LogFactory.getLog(GraphDeserializer.class) ;
    Model m = ModelFactory.createDefaultModel() ;
    SAX2Model handler = null ;
    
    public GraphDeserializer()
    {
    }
    
    public void startElement(String namespace,
                             String localName,
                             String prefix,
                             org.xml.sax.Attributes attributes,
                             DeserializationContext cxt)
    throws SAXException
    {
        XMLReader saxParser = new SAXParser();
        SAX2Model handler = null ;
        try {
            handler = SAX2Model.newInstance(null, m);
        } catch (Exception ex)
        {
            _log.warn("SAX2Model", ex) ;
            return ;
        }
        //SAX2RDF.installHandlers(cxt, handler);
        super.setValue(m) ;
    }
    
//    public void onStartElement(String namespace, String localName,
//                        String prefix, org.xml.sax.Attributes attributes,
//                        DeserializationContext cxt) throws SAXException
//    {
//        _log.info("onStartElement") ;
//        
//    }
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