/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */


package dev;
import joseki.rdfserver;
import org.apache.commons.logging.*;
import java.io.File;

/** Run a Joseki server inside an IDE (Eclipse) for development purposes 
 *  provide a place to configure things before calling the real main() 
 * 
 * @author Andy Seaborne
 * @version $Id: RunJoseki.java,v 1.1 2005-01-10 11:48:19 andy_seaborne Exp $
 */ 

public class RunJoseki
{
    public static void main(String[] args)
    {
        setLog4j() ;
        rdfserver.main(args) ;
        
        // Threads under Eclipse seem to be daemons and so the server exits 
        for ( ; ; )
        {
            Object obj = new Object() ;
            synchronized(obj)
            {
                // Remember to own the lock first.
                try { obj.wait() ; } catch (Exception ex) {}
            }
        }
        
        //System.exit(0) ;
        //return ;
    }
    
    
    private static void setLog4j()
    {
        setPropertyDefault("org.apache.commons.logging.Log",
                           "org.apache.commons.logging.impl.Log4JLogger");

        if ( System.getProperty("log4j.configuration") == null )
        {
            if ( tryLog4jConfig("log4j.properties") )
                return ;
            if ( tryLog4jConfig("etc/log4j.properties") )
                return ;
        }
    }
    
    private static boolean tryLog4jConfig(String fn)
    {
        File f = new File(fn) ;
        if ( f.exists() )
        {
            fn = "file:"+fn ;
            System.setProperty("log4j.configuration", fn) ;
            LogFactory.getLog(RunJoseki.class).info("Setting log4j.configuration: "+fn) ;
            return true ;
        }
        return false ;
    }
    
    
    static void setPropertyDefault(String name, String value)
    {
        if ( System.getProperty(name) == null )
            System.setProperty(name, value) ;
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
