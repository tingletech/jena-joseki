/*
 * (c) Copyright 2004, Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.server.http;

/** A class to handle HTTP media types
 * 
 * @author Andy Seaborne
 * @version $Id: MediaType.java,v 1.4 2004-11-19 18:48:39 andy_seaborne Exp $
 */

public class MediaType
{
    String mediaType  = null;
    
    String type = null ;
    String subType = null ;
    
    
    public MediaType(String s)
    {
        mediaType = s ;
        parse() ;
    }
    
    public MediaType(String type, String subType)
    {
        this.type = type ;
        this.subType = subType ;
        mediaType = type ;
        if ( subType != null )
            mediaType = type+"/"+subType ;
    }
    
    private void parse()
    {
        String[] t = MediaRange.split(mediaType, "/") ;
        
        type = t[0] ;
        if ( t.length > 1 )
            subType = t[1] ;
    }
    
    public String asString()
    {
        return mediaType ;
    }
    
    public String toString()
    {
        return mediaType ;
    }
}

/*
 * (c) Copyright 2004 Hewlett-Packard Development Company, LP
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
