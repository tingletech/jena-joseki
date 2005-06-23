/*
 * (c) Copyright 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.query.engineHTTP;

import org.joseki.*;
import org.joseki.server.http.HttpContentType;
import org.joseki.util.Convert ;
import org.joseki.vocabulary.*;

import java.net.* ;
import java.io.* ;
import java.util.* ;
import org.apache.commons.logging.* ;

import com.hp.hpl.jena.rdf.model.* ;
import com.hp.hpl.jena.shared.* ;

/** Create an execution object for performing a query on a model
 *  over HTTP.  This is the main protocol engine for HTTP query.
 *  There are higher level classes for doing a query in a particular
 *  langauge, such as RDQL.
 * 
 *  Normally, HTTP GET? is used for the query.  If the queryStringLang is null or "",
 *  do a plain GET on the model. 
 * 
 *  If the query string is large, then HTTP POST?query is used.

 * @author  Andy Seaborne
 * @version $Id: HttpQuery.java,v 1.1 2005-06-23 09:55:58 andy_seaborne Exp $
 */
public class HttpQuery
{
    static final Log log = LogFactory.getLog(HttpQuery.class.getName()) ;
    
    /** The definition of "large" queries */
    // Not final so that other code can change it for testing.
    static public /*final*/ int urlLimit = 2*1024 ;
    
    // PSetting for this query
    String modelURI ;
    String queryLang ;
    Map params = new HashMap() ;
    
    String queryString = null ;
    
    // An object indicate no value associated with parameter name 
    final static Object noValue = new Object() ;
    
    int responseCode = 0;
    String responseMessage = null ;
    boolean forcePOST = false ;
    
    static final String ENC_UTF8 = "UTF-8" ;
    
    /** Create a execution object for a whole model GET
     * @param urlString     The model
     */
    
    public HttpQuery(String urlString)
    {
        init(urlString, null) ;
    }
        

    /** Create a execution object for a whole model GET
     * @param url           The model
     */
    
    public HttpQuery(URL url)
    {
        init(url.toString(), null) ;
    }
        

    /** Create a execution object for a query.
     * @param url           The model
     * @param queryLang     The query language
     */
    
    public HttpQuery(URL url, String queryLang)
    {
        // Strangely, we unpack the URL because we munge it with a query string.
        init(url.toString(), queryLang) ;
    }
    

    /** Create a execution object for a query.
     * @param urlString     The model
     * @param queryLang     The query language
     */
    
    public HttpQuery(String urlString, String queryLang)
    {
        init(urlString, queryLang) ;
    }
    
    private void init(String urlStr, String qLang)
    {
        if ( log.isTraceEnabled())
            log.trace("URL: "+urlStr+" Lang="+qLang) ;

        if ( urlStr.indexOf('?') >= 0 )
            throw new HttpException(-1, "URL already has a query string ("+urlStr+")") ;
        
        modelURI = urlStr ;
        queryLang = qLang ;
        queryString = null ;
    }

    
    private void makeQueryString()
    {
        if (queryString != null)
            // Cached
            return;
            
        // If lang is null, then we should be doing a plain GET
        if (queryLang != null)
            queryString = "?"+HttpParams.pQueryLang+"=" + Convert.encWWWForm(queryLang);

        // but for generality we'll still encode the string correctly
        boolean first = (queryString == null);
        for (Iterator iter = params.keySet().iterator(); iter.hasNext();)
        {
            String name = (String) iter.next();
            Object obj = params.get(name);
            String tmp = name ;
            if ( obj != noValue )
                tmp = name + "=" + Convert.encWWWForm((String)obj);

            if (first)
                queryString = "?" + tmp;
            else
                queryString = queryString + "&" + tmp;
            first = false ;
        }
        
        if ( queryString == null )
            queryString = "" ;
    }


    /** Set a parameter to the operation
     * @param name  Name of the parameter
     * @param value Value - May be null to indicate none - the name still goes.
     */

    public void setParam(String name, String value)
    {
        if ( value == null )
            params.put(name, noValue) ;
        else
            params.put(name, value) ;
        // Clear cached calculation
        queryString = null ;
    }

    /** Add a parameter to the operation
     * @param name  Name of the parameter
     * @param value Value - May be null to indicate none - the name still goes.
     */

    public void addParam(String name, String value)
    {
        setParam(name, value) ;
    }

    /** Add a parameter to operation (no value associated with the name)
     * @param name  Name of the parameter
     */

    public void addParam(String name) { addParam(name, null) ; }

//    /** Return the that will, or has been, used.
//     *  @return URL
//     */
//    public URL getQueryOperationURL() { return modelURI ; }
    
    
    /** Return whether this request will go by GET or POST
     *  @return boolean
     */
    public boolean usesPOST()
    {
        if ( forcePOST )
            return true ;
        makeQueryString() ;
        
        return queryString != null && +modelURI.length()+queryString.length() >= urlLimit ;
    }

    /** Force the use of HTTP POST for the query operation
     */

    public void setForcePOST()
    {
        forcePOST = true ;
    }

    /** Return the results from the last execution */    
    public int getResponseCode() { return responseCode ; }
    
    /** Return the results from the last execution */    
    public String getResponseMessage() { return responseMessage ; }
    

    /** Execute the operation
     * @return Model    The resulting model
     * @throws HttpException
     */
    public Model exec() throws HttpException
    {
        makeQueryString() ;

        try {
            if (usesPOST())
                return execPost();
            else
                return execGet();
        } catch (HttpException httpEx)
        {
            log.trace("Exception in exec", httpEx);
            throw httpEx;
        }
        catch (JenaException jEx)
        {
            log.trace("JenaException in exec", jEx);
            throw jEx ;
        }
    }
     

    private Model execGet() throws HttpException
    {
        URL target = null ;
        try {
            if ( queryString.equals("")) 
            {
                //assert queryLang == null ;
                target = new URL(modelURI) ; 
            }
            else
                target = new URL(modelURI+queryString) ;
        }
        catch (MalformedURLException malEx)
        { throw new HttpException(0, "Malformed URL: "+malEx) ; }
        log.trace("GET "+target.toExternalForm()) ;
        
        try
        {
            HttpURLConnection conn = (HttpURLConnection) target.openConnection();
            conn.setRequestProperty("Accept", Joseki.contentTypeRDFXML+", "+
                                              Joseki.contentTypeAppN3) ;
            // By default, following 3xx redirects is true
            //conn.setFollowRedirects(true) ;

            conn.setDoInput(true);
            conn.connect();
            try
            {
                return execCommon(conn);
            }
            catch (HttpException qEx)
            {
                // Back-off and try POST if something complain about long URIs
                // Broken 
                if (qEx.getResponseCode() == 414 /*HttpServletResponse.SC_REQUEST_URI_TOO_LONG*/ )
                    return execPost();
                throw qEx;
            }
        }
        catch (java.net.ConnectException connEx)
        {
            throw new HttpException(HttpException.NoServer, "Failed to connect to remote server");
        }

        catch (IOException ioEx)
        {
            throw new HttpException(ioEx);
        }
    }
    
    // Better (now) - turn into an HttpExec and use that engine  
    
    private Model execPost() throws HttpException
    {
        URL target = null;
        try { target = new URL(modelURI + "?op=query"); }
        catch (MalformedURLException malEx)
        { throw new HttpException(0, "Malformed URL: " + malEx); }
        log.trace("POST "+target.toExternalForm()) ;
        
        try
        {
            HttpURLConnection conn = (HttpURLConnection) target.openConnection();
            conn.setRequestProperty("Accept", Joseki.contentTypeRDFXML+", "+
                                              Joseki.contentTypeAppN3) ;
            conn.setRequestProperty("Accept-Charset", ENC_UTF8) ;
            conn.setRequestProperty("Content-Type", //Joseki.clientContentType) ;
                                    Joseki.clientContentType+ "; charset="+ENC_UTF8) ;
            conn.setDoOutput(true) ;
            conn.setDoInput(true) ;
            
            //Writer w = new OutputStreamWriter(conn.getOutputStream(), encodingUTF8) ;
            
            Model model = ModelFactory.createDefaultModel() ;
            Resource r = model.createResource() ;
            
            // Old name - a bug.
            // r.addProperty(JosekiVocab.queryOperationName, queryLang) ;
            r.addProperty(JosekiVocab.requestQueryLanguage, queryLang) ;

            for ( Iterator iter = params.keySet().iterator() ; iter.hasNext() ; )
            {
                String name = (String)iter.next() ;
                if ( name.equals(HttpParams.pQuery))
                    r.addProperty(JosekiVocab.queryScript, (String)params.get(HttpParams.pQuery)) ;
                else
                    log.warn("execPost: Skipping parameter: "+name) ;
                log.trace("Post: "+name+" = "+(String)params.get(name)) ;
            }
                 
            String rdfSyntax = Joseki.getWriterType(Joseki.clientContentType) ;
            RDFWriter rdfw = model.getWriter() ;
            if ( rdfSyntax.startsWith("RDF/XML") )
                rdfw.setProperty("showXmlDeclaration", "true") ;
            rdfw.write(model, conn.getOutputStream(), null) ;

            conn.getOutputStream().flush() ;
            conn.connect() ;
            return execCommon(conn) ;
        }
        catch (RDFException rdfEx)
        {
            throw new HttpException(-1, "Failed to create RDF request");
        }
        catch (java.net.ConnectException connEx)
        {
            throw new HttpException(-1, "Failed to connect to remote server");
        }

        catch (IOException ioEx)
        {
            throw new HttpException(ioEx);
        }
    }
    
    private Model execCommon(HttpURLConnection conn) throws HttpException
    {
        try {        
            responseCode = conn.getResponseCode() ;
            responseMessage = conn.getResponseMessage() ;
            
            // 1xx: Informational 
            // 2xx: Success 
            // 3xx: Redirection 
            // 4xx: Client Error 
            // 5xx: Server Error 
            
            if ( 300 <= responseCode && responseCode < 400 )
            {
                throw new HttpException(responseCode, responseMessage) ;
            }
            
            // Other 400 and 500 - errors 
            
            if ( responseCode >= 400 )
            {
                throw new HttpException(responseCode, responseMessage) ;
            }
  
            // Request suceeded
            // Result coming back is a model
            InputStream in = conn.getInputStream() ;
            
            // Get the content-type and character encoding of the response.
            // RDF systems are required to support UTF-8
            
            HttpContentType ct = new HttpContentType(conn.getContentType(), Joseki.contentTypeRDFXML, ENC_UTF8) ;
            
            if ( ! ct.getCharset().equalsIgnoreCase(ENC_UTF8) )
                log.warn("Charset is not UTF-8 : danger of mismatch with XML body") ;
            
            Reader r = null ;
            try { r = new InputStreamReader(in, ct.getCharset()) ; }
            catch ( UnsupportedEncodingException ex)
            {
                log.warn("Unsupported encoding '"+ct.getCharset()+"' : trying with UTF-8") ;
                r = new InputStreamReader(in, ENC_UTF8) ;
            }
            
            Model resultModel = ModelFactory.createDefaultModel() ;
                
            resultModel.read(r, "http://somewhere/", Joseki.getReaderType(ct.getMediaType())) ;
            return resultModel ;
        }
        catch (IOException ioEx)
        {
            throw new HttpException(ioEx) ;
        } 
        catch (RDFException rdfEx)
        {
            throw new HttpException(rdfEx) ;
        }
    }
    
    public String toString()
    {
        makeQueryString() ;

        if ( queryString.equals(""))
            return modelURI ; 
        else 
            return modelURI+queryString ;
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
