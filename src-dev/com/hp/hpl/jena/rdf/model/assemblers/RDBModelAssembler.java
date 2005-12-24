/*
 	(c) Copyright 2005 Hewlett-Packard Development Company, LP
 	All rights reserved - see end of file.
 	$Id: RDBModelAssembler.java,v 1.1 2005-12-24 22:02:55 andy_seaborne Exp $
*/

package com.hp.hpl.jena.rdf.model.assemblers;

import com.hp.hpl.jena.db.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;

public class RDBModelAssembler extends NamedModelAssembler implements Assembler
    {
    protected Model createModel( Assembler a, Resource root )
        {
        // System.err.println( ">> RDBModelAssembler: createModel from " + root  + " [" + type + "]" );
        String name = getModelName( root );
        ConnectionDescription c = getConnection( a, root );
        return createModel( c, name );
        }
    
    protected ConnectionDescription getConnection( Assembler a, Resource root )
        {
        Resource C = getUniqueResource( root, JA.connection );
        if (C == null) throw new JenaException( "must have connection" );
        // System.err.println( ">> root " + root + " has connection " + C );
        return (ConnectionDescription) a.create( C );        
        }
    
    protected Model createModel( ConnectionDescription c, String name )
        {
        IDBConnection ic = c.getConnection();
        return ic.containsModel( name ) ? ModelRDB.open( ic, name ) : ModelRDB.createModel( ic, name ); 
        }
    }


/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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