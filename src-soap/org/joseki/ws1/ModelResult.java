/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.ws1;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.axis.Constants;
import org.apache.axis.encoding.*;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;



public class ModelResult implements Serializable
{
    private static Log log = LogFactory.getLog(ModelResult.class) ;
    private static final long serialVersionUID = 75264722L;
    
    // See org.apache.axis.encoding.ser.
    
    public ModelResult(/*Model model*/)
    {}
    
    // See UserGuide deploy: <typeMapping 
    
//    // Type metadata
//    private static org.apache.axis.description.TypeDesc typeDesc =
//        new org.apache.axis.description.TypeDesc(Variable.class, true);
    /**
     * Get Custom Serializer
     */
    public static Serializer getSerializer(
           String mechType, 
           Class _javaType,  
           QName _xmlType) {
        return 
          new  ModelResult.ModelSerializer() ;
    }

    /**
     * Get Custom Deserializer
     */
    public static Deserializer getDeserializer(
           String mechType, 
           Class _javaType,  
           QName _xmlType) {
        return 
          new ModelResult.ModelDeserializer() ;
    }

    // -------- Serializer
    
    static class ModelSerializer implements Serializer
    {
        //public ModelSerializer(Class javaType, QName xmlType)
        public ModelSerializer() {}
        
        
        public void serialize(QName qname, Attributes attributes, Object object,
                              SerializationContext cxt) throws IOException
        {
            //cxt.writeDOMElement() ;
            // May need pipes
            //cxt.writeString() ;
        }

        public Element writeSchema(Class javaType, Types types) throws Exception
        { return null ; }

        public String getMechanismType()
        { return Constants.AXIS_SAX ; }
    }
    
    // -------- Deserializer

    static class ModelDeserializer extends DeserializerImpl
    {
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