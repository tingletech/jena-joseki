package dev ;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.util.FileManager;


public class RunConf
{
    public static void main(String argv[])
    {
        Model spec = FileManager.get().loadModel( "assem1.ttl" );
        one(spec, "ex:a1" ) ;
        //one(spec, "ex:a2" ) ;
        //one(spec, "ex:a3" ) ;
        //one(spec, "ex:a4" ) ;
        //one(spec, "ex:a5" ) ;
    }
    
    static void one(Model spec, String qname)
    {
        System.out.println("Test: "+qname) ;
        System.out.flush() ;
        try {
            Resource root = spec.createResource( spec.expandPrefix( qname ) );
            
            boolean b = spec.containsResource(root) ;
            if ( !b ) 
                System.err.println("**** Not found: "+root) ;
            
            Model m = Assembler.general.openModel( root );
            
            m.write(System.out, "N3") ;
        } catch (Exception ex)
        {
            ex.printStackTrace() ;
        }
        System.out.println("---------------------------------------") ;
        System.out.flush() ;
    }
}

/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
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