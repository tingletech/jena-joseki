/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */


/** The Cache class is a framework object: it requires an 
 *  object to build instances and a policy for replacement.
 *  Currently, caches are fixed size.
 *  
 * @author     Andy Seaborne
 * @version    $Id: Cache.java,v 1.2 2005-01-03 20:26:36 andy_seaborne Exp $
 */
 
package org.joseki.util.cache;

import java.util.* ;
import org.apache.commons.logging.*; 

public class Cache
{
    static Log log = LogFactory.getLog(Cache.class.getName()) ;

    Map objects = new HashMap() ;
    CacheItemFactory factory = null ;
    CachePolicy policy = null ;
    int size ;
    
    public Cache(int _size, CacheItemFactory _factory, CachePolicy _policy)
    {
        factory = _factory ;
        policy = _policy ;
        size = _size ;
    }
    
    public synchronized void remove(Object key)
    {
        objects.remove(key) ;
    }
    
    public synchronized void put(Object key, Object obj)
    {
        if ( objects.size() >= size )
            expel(1) ;

        policy.newItem(key, obj) ;
        objects.put(key, obj) ;
    }
    
    public synchronized Object get(Object key)
    {
        log.trace("get("+key+")") ;
        Object obj = objects.get(key) ;
        if ( obj == null )
        {
            if ( objects.size() >= size )
                expel(1) ;
            
            obj = factory.create(key) ;
            if ( obj == null )
                log.warn("factory.create("+key+") returned null") ;

            put(key, obj) ;
        }
        policy.update(key) ;
        return obj ;
    }

    private void expel(int count)
    {
        Object[] keys = policy.expel(count) ;
        for ( int i = 0 ; i < keys.length ; i++ )
        {
            Object obj = objects.remove(keys[i]) ;
            factory.destroy(keys[i], obj) ;
        }
    }
}


/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
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
