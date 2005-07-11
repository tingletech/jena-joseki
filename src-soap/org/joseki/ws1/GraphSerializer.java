/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.ws1;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFWriter;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;


public class GraphSerializer implements Serializer
{
    private static Log log = LogFactory.getLog(GraphSerializer.class) ;
    
    //public GraphSerializer(Class javaType, QName xmlType)
    public GraphSerializer() {}
    
    
    public void serialize(QName qname, Attributes attributes,
                          Object value, SerializationContext cxt) throws IOException
    {
        log.info("serialize: qname="+qname) ;
        
        //cxt.writeDOMElement() ;
        // May need pipes
        //cxt.writeString() ;
        
        if ( ! ( value instanceof Model ) )
        {
            log.warn("Attempt to serialize a "+value.getClass().getName()) ;
            return ;
        }
            
        
        Model model = (Model)value ;
        // Pipe would be better.
        StringWriter sw = new StringWriter() ;
        RDFWriter w = model.getWriter("RDF/XML-ABBREV") ;
        w.setProperty("showXmlDeclaration", "false") ;
        w.write(model, sw, null) ;
        cxt.writeString(sw.toString()) ;
    }

    public Element writeSchema(Class javaType, Types types) throws Exception
    { return null ; }

    public String getMechanismType()
    { return Constants.AXIS_SAX ; }
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