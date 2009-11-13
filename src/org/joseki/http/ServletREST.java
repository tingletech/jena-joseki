/*
 * (c) Copyright 2009 Talis Information Ltd
 * All rights reserved.
 * [See end of file]
 */

package org.joseki.http;

import javax.servlet.ServletConfig ;
import javax.servlet.ServletException ;
import javax.servlet.http.HttpServlet ;
import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpServletResponse ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

public class ServletREST extends HttpServlet
{
    static private Logger log = LoggerFactory.getLogger(ServletREST.class) ;

    @Override
    public void init() throws ServletException
    {
        super.init() ;
        return ;
    }

    @Override
    public void init(ServletConfig config) throws ServletException
    {
    }

    @Override
    public void destroy()
    {}

    @Override
    protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        String uri = httpRequest.getRequestURI() ;
    }

    @Override
    protected void doPut(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {

    }

    @Override
    protected void doDelete(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {}

    @Override
    protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    {
        // Complicated.
        // Either: SPARQL/UIpdate language
        // Or:     triples to add.
        
        String target = decideTarget(httpRequest) ;
        
    }

//    @Override
//    protected void doHead(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
//    {}

//    @Override
//    protected  void     doOptions(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
//    { }

//    @Override
//    protected  void     doTrace(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
//    { }
    
    private String decideTarget(HttpServletRequest httpRequest)
    {
        try {

            // Cases:
            // 1 -- http://server/graphs/graph1
            // 2 -- http://server/graphs?graph=http://H/graph1
            // 3 -- http://server/graphs/http://H/graph1

            String graphArg = httpRequest.getParameter(getServletInfo()) ;
            if ( graphArg != null )
                // Decode?
                //return URLDecoder.decode(graphArg, "UTF-8") ; }
                return graphArg ;
            String uri = httpRequest.getRequestURI() ;
            if ( uri.contains(":") )
            {
                // http://server/graphs/http://H/graph1
                
            }
            
            return uri ;
        
        }
//        catch (UnsupportedEncodingException ex)
//        {
//            log.error("No UTF-8!") ;
//            throw new JosekiServerException("No UTF-8!") ;
//        }
        finally{}
    }
}

/*
 * (c) Copyright 2009 Talis Information Ltd
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