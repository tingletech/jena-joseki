/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;


import java.util.* ;

/** Abstaction of an operation request on a model.
 *  The work is done by a processor that accepts the request.
 * @author      Andy Seaborne
 * @version     $Id: Request.java,v 1.1 2004-11-03 10:15:01 andy_seaborne Exp $
 */
public interface Request
{
    public String getName() ;
    public SourceModel getSourceModel() ;
    public Processor getProcessor() ;
    public void setProcessor(Processor proc) ;
    
    /** Get the URI for the source for the operation - the request URI local to the webapp 
     * 
     * @return String
     */
    public String getModelURI() ;
    
    /** The URL used in the request
     * 
     * @return String The URL used in the request
     */
    public String getRequestURL() ;
    public Dispatcher getDispatcher() ;

    // The named parameters to the operation
    // Map is String => String
    public Map getParams() ;
    public String getParam(String param) ;
    
    public boolean takesArg() ;
    public void addArg(Object m) ;
    public boolean containsParam(String name) ;
    public void setParam(String name, String value)  ;
    
    // Data args : Jena models.  Usually at most one.
    public List getDataArgs() ;
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
 
