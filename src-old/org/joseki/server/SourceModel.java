/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;

import java.util.* ;

/** Wrapper for models used by the RDF server.

 *  A SourceModel incorporates the (operating system) resource management policy 
 *  for a model.  Models are created and released by the associated SourceController.
 *  It does <b>not</b> have to be backed by a Jena as the data source.   
 *
 * @author  Andy Seaborne
 * @version $Id: SourceModel.java,v 1.1 2005-06-23 09:55:57 andy_seaborne Exp $
 */

public interface SourceModel
{
    //public Model getModel() ;

    /** The way to reference this data from outside.
     *  This is the URL minus the protocol host part 
     *  e.g. "/dir/foobar" because we can have many access points
     *  on the web for this server.
     */ 
    public String getServerURI() ;
    
    //TODO Remove getPrefixes() as part of Result object
    /** Help write results neatly.
     */ 
    public Map getPrefixes() ;
    public void setPrefix(String prefix, String nsURI) ;
    
    // System resource control
    public void activate() ;
    public void deactivate() ;
    
    // Operation control.
    // Higher level concurrency control and resource locking.
    // Should at least provide MRSW locking (SourceModelCom does this)
    // A transactional model may use transactions to get isolation.
    
    public void startOperation(boolean readOnly) ;
    public void endOperation() ;
    public void abortOperation() ;
    
    public void flush() ;
    public boolean isImmutable() ;
    public void setIsImmutable(boolean isFixed) ;
    public void release() ;

    public ProcessorRegistry getProcessorRegistry() ;
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
