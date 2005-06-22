
/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */


package joseki3.server;

import java.util.*;

/** General Request 
 * @author      Andy Seaborne
 * @version     $Id$
 */
public class Request
{
    // How the operation was described.
    String serviceURI = null ;
    String requestURL = null ;

    final static Object noValue = new Object() ; 
    // Parameters :: key => List of values pairs
    
    Map params = new HashMap();

    public Request(String uri, String url)
    {
        serviceURI = uri ;
        requestURL = url ;
    }
    
    // ---- Parameters
    
    public String getParam(String param)
    { 
        List x = getParamsOrNull(param) ;
        if ( x == null )
            return null ;
        return (String)x.get(0);
    }
    
    public List getParams(String param)
    {
            if ( ! params.containsKey(param) ) 
                return new ArrayList() ;
            List x = (List)params.get(param) ;
            return x ;
    }
    
    private List getParamsOrNull(String param)
    {
            if ( ! params.containsKey(param) ) 
                return null ;
            List x = (List)params.get(param) ;
            if ( x.size() == 0 )
                return null ;
            return x ;
    }
    
    
    
    
    public void setParam(String name, String value)
    {
        if ( ! params.containsKey(name) )
            params.put(name, new ArrayList()) ;
        List x = (List)params.get(name) ;
        x.add(value) ;
    }

    public boolean containsParam(String param)
    {
        return getParams(param) != null ;
    }
    
    public Iterator parameterNames()
    {
        List x = new ArrayList() ;
        for ( Iterator iter = params.keySet().iterator() ; iter.hasNext() ; )
        {
            String k = (String)iter.next() ;
            List z = (List)params.get(k) ;
            if ( z.size() != 0 )
                x.add(k) ;
        }
        return x.iterator() ;
    }
    
    /** @return Returns the requestURL. */
    public String getRequestURL()
    {
        return requestURL ;
    }

    /** @return Returns the serviceURI. */
    public String getServiceURI()
    {
        return serviceURI ;
    }
    
    public String paramsAsString()
    {
        StringBuffer sBuff = new StringBuffer() ;
        boolean first = true ;
        for ( Iterator iter = parameterNames() ; iter.hasNext(); )
        {
            String k = (String)iter.next() ;
            List x = getParamsOrNull(k) ;
            if ( x != null )
                for ( Iterator iter2 = x.iterator() ; iter2.hasNext(); )
                {
                    String v = (String)iter2.next() ;
                    if ( ! first )
                        sBuff.append(" ") ;
                    first = false ;
                    sBuff.append(k) ;
                    sBuff.append("=") ;
                    sBuff.append(v) ;
                }
        }
        return sBuff.toString() ; 
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
