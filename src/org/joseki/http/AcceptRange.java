/*
 * (c) Copyright 2004, 2005, 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.http;
import java.util.* ;
import org.apache.commons.logging.* ;
import org.joseki.util.StringUtils ;

/** A class to handle HTTP accept items with parameters 
 * 
 * @author Andy Seaborne
 * @version $Id: AcceptRange.java,v 1.2 2005-12-26 19:19:54 andy_seaborne Exp $
 */

// Only used for parsing Accept: headers
// Extends AcceptItem with space for "q" and other parameters.
class AcceptRange extends AcceptItem
{
    Map params = new HashMap() ;
    double q = 1.0 ;
    int posn = 0 ;
    
 
  
    public AcceptRange(String s)
    {
        process1(s) ;
    }
    
    public AcceptItem getAcceptItem() { return this ; } 
    
    private void process1(String s)
    {
        String[] x = StringUtils.split(s, ";") ;
        parseAndSet(x[0]) ;
        
        for ( int i = 1 ; i < x.length ; i++ )
        {
            // Each a parameter
            String z[] = StringUtils.split(x[i], "=") ;
            if ( z.length == 2 )
            {
                this.params.put(z[0], z[1]) ;
                if ( z[0].equals("q") )
                    try {
                        q = Double.parseDouble(z[1]) ;
                    } catch (NumberFormatException ex)
                    {}
            }
            else
                LogFactory.getLog(AcceptRange.class).warn("Duff parameter: "+x[i]+" in "+s) ;
        }
    }
    
    /** Format for use in HTTP header
     */
    
    public String toHeaderString()
    {
        StringBuffer b = new StringBuffer() ;
        b.append(super.toString()) ;
        for ( Iterator iter = params.keySet().iterator() ; iter.hasNext() ; )
        {
            String k = (String)iter.next() ;
            String v = (String)params.get(k) ;
            b.append(";") ;
            b.append(k) ;
            b.append("=") ;
            b.append(v) ;
        }
        return b.toString() ;
    }
    
    /** Format to show structure - intentionally different from header
     *  form so you can tell parsing happned correctly
     */  
    
    public String toString()
    {
        StringBuffer b = new StringBuffer() ;
        b.append("[") ;
        b.append(super.toString()) ;
        for ( Iterator iter = params.keySet().iterator() ; iter.hasNext() ; )
        {
            String k = (String)iter.next() ;
            String v = (String)params.get(k) ;
            b.append(" ") ;
            b.append(k) ;
            b.append("=") ;
            b.append(v) ;
        }
        b.append("]") ;
        return b.toString() ;
    }
 

}

/*
 * (c) Copyright 2004, 2005, 2006 Hewlett-Packard Development Company, LP
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
