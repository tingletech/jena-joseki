/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server.source;

import java.util.* ;
//import org.apache.commons.logging.* ;
import com.hp.hpl.jena.rdf.model.* ;
import org.joseki.server.SourceController ;
import org.joseki.util.cache.*;


/** An attached model that manages a file holding the model.
 *  Files are read in and managed in a cache.  Each file source
 *  has a system resource manager, the SourceManager  
 *
 * @author  Andy Seaborne
 * @version $Id: SourceModelFile.java,v 1.2 2005-01-03 20:26:30 andy_seaborne Exp $
 */

public class SourceModelFile extends SourceModelCom
{
    static FileModelFactory factory = new FileModelFactory() ;
    
    // TODO Make the cache size configurable. 
    static Cache cache = new Cache(25, factory, new CachePolicyLRU()) ;

    // For all items: map serverURI (external relative URI) to the source
    // controller so the static FileModelFactory can do its thing.
    
    // [Alternative: use an indirect cache : cache keeps record of which only N
    // are in use at any one time.]
     
    static Map controllers = new HashMap() ;
    
    
    public SourceModelFile(SourceController controller, String serverURI)
    {
        super(controller, serverURI) ;
        controllers.put(serverURI, controller) ;
    }
    
    public Model getModel()
    {
        return (Model)cache.get(getServerURI()) ;
    }

    static class FileModelFactory implements CacheItemFactory
    {
        //static Log logger = LogFactory.getLog(FileModelFactory.class) ;
        
        // CacheItemFactory interface
    
        /* (non-Javadoc)
         * @see org.joseki.util.cache.CacheItemFactory#create(java.lang.Object)
         */
        public Object create(Object key)
        {
            // Find the controller for this item by key
            return getSourceController(key).buildSource() ;
        }
    
        /* (non-Javadoc)
         * @see org.joseki.util.cache.CacheItemFactory#destroy(java.lang.Object, java.lang.Object)
         */
        public void destroy(Object key, Object obj)
        {
            getSourceController(key).releaseSource() ;
        }
        
        SourceController getSourceController(Object key)
        {
            return (SourceController)controllers.get(key) ;
        }
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
