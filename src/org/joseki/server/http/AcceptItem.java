/*
 * (c) Copyright 2004, Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.server.http;

/** A class to handle HTTP Accept types
 * 
 * @author Andy Seaborne
 * @version $Id: AcceptItem.java,v 1.1 2004-11-25 18:21:49 andy_seaborne Exp $
 */

public class AcceptItem
{
    private String acceptType  = null;
    
    private String type = null ;
    private String subType = null ;
    
    
    public AcceptItem(String s)
    {
        acceptType = s ;
        parse() ;
    }
    
    public AcceptItem(String type, String subType)
    {
        this.type = type ;
        this.subType = subType ;
        acceptType = type ;
        if ( subType != null )
            acceptType = type+"/"+subType ;
    }
    
    private void parse()
    {
        String[] t = AcceptRange.split(acceptType, "/") ;
        
        type = t[0] ;
        if ( t.length > 1 )
            subType = t[1] ;
    }
    
    public String asString()
    {
        return acceptType ;
    }
    
    public String toString()
    {
        return acceptType ;
    }
    /**
     * @return Returns the acceptType.
     */
    public String getAcceptType()
    {
        return acceptType;
    }
    /**
     * @param acceptType The acceptType to set.
     */
    public void setAcceptType(String acceptType)
    {
        this.acceptType = acceptType;
    }
    /**
     * @return Returns the subType.
     */
    public String getSubType()
    {
        return subType;
    }
    /**
     * @param subType The subType to set.
     */
    public void setSubType(String subType)
    {
        this.subType = subType;
    }
    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(String type)
    {
        this.type = type;
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
