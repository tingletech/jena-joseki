/*
 * (c) Copyright 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki;

public class Service
{
    String serviceRef ;
    Processor processor ;
    boolean available ;
    DatasetDesc dataset ;
    
    public Service(Processor proc, String ref, DatasetDesc dataset)
    {
        this.serviceRef = canonical(ref) ; 
        this.processor = proc ;
        this.dataset = dataset ;
        this.available = true ;
    }
    
    public void exec(Request request, Response response) throws ExecutionException
    {
        if ( ! isAvailable() )
            throw new ExecutionException(ReturnCodes.rcServiceUnavailable, "Service is not currently available") ;
        processor.exec(request, response, dataset) ;
    }
    
    public boolean isAvailable() { return available ; } 
    public void setAvailability(boolean availability) { available = availability ; }
    
    public String getRef() { return "<"+serviceRef+">" ; }
    public DatasetDesc getDatasetDesc() { return dataset ; }
    
    public String toString()
    {
        return "Service: "+serviceRef ;
    }
    
    public static String canonical(String ref)
    {
        while ( ref.startsWith("/") )
            ref = ref.substring(1) ;
        return ref ;
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