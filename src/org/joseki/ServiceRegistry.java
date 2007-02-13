/*
 * (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki;

import java.util.* ;

/** 
 * @author Andy Seaborne
 * @version $Id: ServiceRegistry.java,v 1.6 2007-02-13 10:00:28 andy_seaborne Exp $
 */

public class ServiceRegistry
{
    // Singleton
    static private ServiceRegistry globalRegistry = null ;
    static { init() ; }
    
    
    private Map registry = new HashMap() ; 
    
    //private ServiceRegistry() { }
    
    //public static ServiceRegistry get() { return globalRegistry ; }
    
    private static void init()
    {
//        if ( globalRegistry == null )
//        {
//            globalRegistry = new ServiceRegistry() ;
//        }
    }

    public Service find(String name)
    {
        return (Service)registry.get(name) ;
    }
    
    public void add(String name, Service handler)
    {
        registry.put(name, handler) ; 
    }
    
    public void remove(String name)
    {
        registry.remove(name) ; 
    }
    
    public void clear()
    {
        registry.clear() ;
    }
    
    public Iterator names()
    {
        return registry.keySet().iterator() ; 
    }
    
}

/*
 * (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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