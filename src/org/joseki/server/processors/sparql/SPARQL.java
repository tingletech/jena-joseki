/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors.sparql;

import org.apache.commons.logging.*;
import com.hp.hpl.jena.rdf.model.*;
//import org.joseki.server.Request;
import org.joseki.server.Response;
import com.hp.hpl.jena.query.* ;

/** SPARQL operations
 * 
 * @author  Andy Seaborne
 * @version $Id: SPARQL.java,v 1.1 2004-11-09 21:54:15 andy_seaborne Exp $
 */

public class SPARQL
{
    static Log logger = LogFactory.getLog(SPARQL.class) ;
    
    
    /** Execute a query, output via response
     * 
     * @param model    Data to operate on
     * @param response
     */
    
    public static void execQuery(Model model, Query query, Response response)
    {
       if ( query.isSelectType() && response.getMimeType().equals("application/xml"))
       {
           execQueryXML(model, query, response) ;
           return ;
       }
       // Execute and get a model.
       
       
       // Serialize model to response.getOutput()
       
    }
    
    public static void execQueryXML(Model model, Query query, Response response)
    {
        
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
