/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */


/** Everyone's favourite cache policy - Least Recently Used
 *  
 * @author     Andy Seaborne
 * @version    $Id: CachePolicyLRU.java,v 1.2 2005-01-03 20:26:36 andy_seaborne Exp $
 */
 
package org.joseki.util.cache;

import java.util.* ;
import org.apache.commons.logging.*;

public class CachePolicyLRU implements CachePolicy
{
    static Log log = LogFactory.getLog(CachePolicyLRU.class.getName()) ;
    
    // This implementation simply keeps a linked list.
    // When an object is accessed, the item is moved to the front and
    // the LRU object is the last in the list.
    
    // Requires O(n) update.
    
    // Alternative implmentation:
    // Use a set to entries which record the access count.
    // Scan to expel. Update O(set access).  
    // This would extend to weighted LRU
    // but is more space costly. 
    
    // The low (first) end is the most recently used. 
    LinkedList list = new LinkedList() ;
    
    public Object[] expel(int count)
    {
        if ( log.isTraceEnabled() )
            log.trace("expel("+count+")") ;        
        
        Object[] keys = new Object[count] ;
        for ( int i = 0 ; i < count ; i++ )
        {
            keys[i] = list.removeLast() ;
            if ( log.isTraceEnabled() )
                log.trace("expel "+keys[i]) ;
        }
        return keys ;
    }
    
    
    public void update(Object key)
    {
        if ( log.isTraceEnabled() )
            log.trace("update("+key+")") ;
        if ( ! list.getFirst().equals(key) )
        {
            list.remove(key) ;
            list.addFirst(key) ;
        }

        if ( log.isTraceEnabled() )
            dump() ;
    }
    

    public void newItem(Object key, Object value)
    {
        if ( log.isTraceEnabled() )
           log.trace("newItem("+key+")") ;
        list.addFirst(key) ;
    }
   
    private void dump()
    {
        String $state = "" ;
        for ( Iterator iter = list.listIterator() ; iter.hasNext() ; )
        {
            $state = $state+iter.next()+" " ;
        }
        log.trace($state) ;
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
