/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;

import java.util.* ;

/** The dispatcher registry exists so that connectors can dynamically find the appropiate
 *  dispatcher.  For example, servlets can find the dispatcher during initialization, before
 *  they are first called; when embedded, adding a servlet also creates and starts it
 *  making it inconvenient to bind the servlet to a dispatcher (e.g. the servlet may regsiter
 *  operation hamdlers descirbed in it configuration file).
 * 
 * @author      Andy Seaborne
 * @version     $Id: DispatcherRegistry.java,v 1.2 2005-01-03 20:26:34 andy_seaborne Exp $
 */
public class DispatcherRegistry
{
    // Singleton
    private DispatcherRegistry() {}
    
    static DispatcherRegistry instance  = new DispatcherRegistry() ;
    static public DispatcherRegistry getInstance() { return instance ; }
    
    Map registry = new HashMap() ;
    
    public void add(String name, Dispatcher dispatcher)
    {
        registry.put(name, dispatcher) ;
    }

    public void remove(String name)
    {
        registry.remove(name) ;
    }

    public Dispatcher find(String name)
    {
        return (Dispatcher)registry.get(name) ;
    }

    public boolean contains(String name)
    {
        return registry.containsKey(name) ;
    }

    public Iterator registeredNames()
    {
        return registry.keySet().iterator() ;
    }

    public Iterator registeredDispatchers()
    {
        return registry.values().iterator() ;
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
 
