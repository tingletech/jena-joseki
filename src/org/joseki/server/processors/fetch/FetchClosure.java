/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.processors.fetch;

import org.joseki.util.Closure ;
import org.joseki.server.module.Loadable ;
import com.hp.hpl.jena.rdf.model.*;
import java.util.Set ;

/** Fetch handler to find the bNode-closure from a resource.
 *  The resource itself can be a bNode or a labelled node.
 *  This calculates the set of all properties, and following all the
 *  properties on bNodes (recursively, with loop detection).
 *  
 * @author     Andy Seaborne
 * @version    $Id: FetchClosure.java,v 1.1 2004-11-03 10:14:57 andy_seaborne Exp $
 * 
 */ 

public class FetchClosure implements FetchHandler, Loadable
{
    public String getInterfaceURI() { return org.joseki.vocabulary.JosekiVocab.fetchClosure ; }

    public void init(Resource binding, Resource implementation) {}


    public boolean handles(Resource r, Set types){ return true ;}
    public void fetch(Resource r, Set types, Model acc)
    {
        Closure.closure(r, false, acc) ;
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

