/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.server;

/**
 * @version     $Id: ExecutionError.java,v 1.3 2004-11-12 20:00:35 andy_seaborne Exp $
 * @author      Andy Seaborne
 */

public class ExecutionError
{
    static public final int rcOK                     = 0 ;
    static public final int rcNoSuchQueryLanguage    = 3 ;
    static public final int rcInternalError          = 4 ;
    static public final int rcRDFException           = 5 ;
    static public final int rcNoSuchURI              = 6 ;
    static public final int rcSecurityError          = 7 ;
    static public final int rcOperationNotSupported  = 8 ;
    static public final int rcArgumentUnreadable     = 9 ;
    static public final int rcImmutableModel         = 10 ;
    static public final int rcConfigurationError     = 11 ;
    static public final int rcArgumentError          = 12 ;
    static public final int rcNotImplemented         = 13 ;
    
    static public final int rcQueryParseFailure      = 100 ;
    static public final int rcQueryExecutionFailure  = 101 ;
    static public final int rcQueryUnknownFormat     = 102 ;

    static public String messages[] =
        { "OK",
          "Query parsing failed",
          "Query excution failed",
          "No such query language",
          "Internal Error",
          "RDFException" ,
          "No such URI" ,
          "Access control failure",
          "Operation not supported on URI", 
          "Argument to operation is unreadable" ,
          "Immutable model" ,
          "Incorrect configuration" ,
          "Error in arguments", 
          "Not implemented" ,
          null // Terminator
    };

    static public String errorString(int rc)
    {
        // Note: there is a terminator on the message strings list
        if ( rc < 0 || rc >= messages.length-1 )
            return "Unknown error" ;
            
        return messages[rc] ;
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
