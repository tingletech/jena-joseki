/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.source;

import java.util.* ;
import org.apache.commons.logging.* ;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping.IllegalPrefixException ;
import org.joseki.server.* ;

/** Common code wrapper for source Jena models. 
 *
 * @author  Andy Seaborne
 * @version $Id: SourceModelCom.java,v 1.2 2005-01-03 20:26:30 andy_seaborne Exp $
 */

abstract class SourceModelCom implements SourceModelJena
{
    static Log logger = LogFactory.getLog(SourceModelCom.class.getName()) ;
    
    protected SourceController sourceController = null ;
    protected String serverURI;             // How it appears to the server 
    protected boolean isImmutable = false ;
    protected boolean isActive = false;
    
    private boolean inOperation = false ;
    private boolean isReadLocked = false ;
    private boolean isWriteLocked = false ;
    
    ProcessorRegistry processors = new ProcessorRegistry() ;

    protected SourceModelCom(SourceController ctl)
    {
        sourceController = ctl ;
    }
    
    protected SourceModelCom(SourceController ctl, String uri)
    {
        this(ctl) ;
        serverURI = uri;
    }

    abstract public Model getModel() ;

    // The convenience record of prefixes for this model.
    Map prefixes = null ;
    public Map getPrefixes()
    {
        if ( prefixes == null )
            prefixes = new HashMap() ;
        return prefixes ;
    }

    public void setPrefix(String prefix, String nsURI)
    {
        if ( prefixes == null )
            prefixes = new HashMap() ;
        // Set both here and in the underlying model.
        prefixes.put(prefix, nsURI) ;
        try { getModel().setNsPrefix(prefix, nsURI) ; } catch (IllegalPrefixException e ) {}
    }


    public synchronized void startOperation(boolean readOnly)
    {
        
        activate() ;
        if ( getModel().supportsTransactions() )
            getModel().begin() ;
        else
            getModel().enterCriticalSection(readOnly) ;
    }

    public synchronized void endOperation()
    {
        if ( getModel().supportsTransactions() )
            getModel().commit() ;
        else
            getModel().leaveCriticalSection() ;
        deactivate() ;
    }
    
    public synchronized void abortOperation()
    {
        if ( getModel().supportsTransactions() )
            getModel().abort() ;
        else
            getModel().leaveCriticalSection() ;
        deactivate() ;
    }

    public void flush() { return ; }
    
    public void release()
    {
        if (!isActive)
            return;
        isActive = false;
    }

    public boolean isImmutable()
    {
        return isImmutable;
    }

    public void setIsImmutable(boolean isFixed)
    {
        isImmutable = isFixed ;
    }

    public String getServerURI()
    {
        return serverURI ;
    }
    
    public ProcessorRegistry getProcessorRegistry()
    {
        return processors ;
    }
    
    public void activate()
    {
        if ( sourceController != null )
            sourceController.activate() ;
    }
    
    public void deactivate()
    {
        if ( sourceController != null )
            sourceController.deactivate() ;
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
 
