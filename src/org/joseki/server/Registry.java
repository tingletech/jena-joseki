/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

/**  Possible superclass for DispatcherRegistry to give a single "registry"
 *   implementation.
 *   A Map wrapper (impleemntation inheriticance, not interface inheritance) 
 *   that restricts the keys to strings, and values to a declared object type. 
 * 
 * @author     Andy Seaborne
 * @version    $Id: Registry.java,v 1.1 2004-11-03 10:15:01 andy_seaborne Exp $
 */
 
package org.joseki.server;

import java.util.* ;

public class Registry
{
    static Registry registryRegistry = 
        createRegistry("Registry", Registry.class) ;
    
    public static Registry getRegistry(String name)
    {
        return (Registry)registryRegistry.get(name) ;
    }
    
    public static Registry createRegistry(String regName, Class objClass)
    {
        Registry reg = new Registry(regName, objClass) ;
        registryRegistry.add(regName, reg) ;
        return reg ;
    }
    
    Class objectClass ;
    Map registry = new HashMap() ; 
    
    Registry(String regName, Class objClass)
    {
        objectClass = objClass ;
    }

    public void add(String name, Object object)
    {
        if ( object.getClass().equals(objectClass) )
        {
            registry.put(name,object) ;
            return ;
        }
        throw new RuntimeException("Registry: wrong type: "+object.getClass().getName()+" [expected "+objectClass.getName()+"]") ;
    }
    
    // Generics, generics , ...
    public Object /* objectClass */ get(String name)
    {
        return registry.get(name) ;
    }
    
    public Iterator registeredNames()
    {
        return registry.keySet().iterator() ;
    }

    public Iterator registeredObjects()
    {
        return registry.values().iterator() ;
    }

    public void remove(String name)
    {
        registry.remove(name) ;
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
 
