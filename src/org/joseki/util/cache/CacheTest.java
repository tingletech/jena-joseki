/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */


/**
 * @author     Andy Seaborne
 * @version    $Id: CacheTest.java,v 1.1 2004-11-03 10:15:04 andy_seaborne Exp $
 */
 
package org.joseki.util.cache;

//import org.apache.commons.logging.* ;

public class CacheTest
{

    public static void main(String[] args)
    {
        if (System.getProperty("java.util.logging.config.file") == null)
            System.setProperty("java.util.logging.config.file", "etc/logging.properties");

        Cache c = new Cache(4, new ItemFactoryTest(), new CachePolicyLRU() ) ;
        
        for ( int i = 0 ; i < 5 ; i++)
        {
            Integer val = new Integer(i) ;
            String key = val.toString() ;
            c.put(key, val) ;
        }
        
        
        get(c, new Integer(1).toString()) ;
        get(c, new Integer(2).toString()) ;
        get(c, new Integer(3).toString()) ;
        get(c, new Integer(9).toString()) ;
    }
    
    static void get(Cache c, Object k)
    {
        Object obj = c.get(k) ;
        System.out.println(k + " => "+obj) ;
    }
    
    
    static class ItemFactoryTest implements CacheItemFactory
    {
        
        public Object create(Object key)
        {
            return key ;
        }

        public void destroy(Object key, Object obj)
        {
        }
        
    }
}


/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
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