/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.soap;

import java.util.Iterator;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.Serializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class GraphSerializerFactory implements SerializerFactory
{
    private static Log log = LogFactory.getLog(GraphSerializerFactory.class) ;
    
    // Third way
    public GraphSerializerFactory() { }
//    
//    // Second way to call
    public GraphSerializerFactory(Class javaType, QName xmlType)
    { } 

    // First way to call
    public static SerializerFactory create(Class javaType, QName xmlType)
    { return new GraphSerializerFactory(javaType, xmlType) ; } 
    
    public Serializer getSerializerAs(String mechanismType)
    {
        //log.info("getSerializerAs("+mechanismType+")") ;
        return new GraphSerializer() ;
    }
    
    public Iterator getSupportedMechanismTypes()
    {
        Vector v = new Vector() ;
        v.add(Constants.AXIS_SAX);
        return v.iterator();
    }
}
/*
 * (c) Copyright 2005, 2006 Hewlett-Packard Development Company, LP
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