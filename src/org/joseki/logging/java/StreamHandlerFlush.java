/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */


package org.joseki.logging.java;

import java.io.* ;
import java.util.logging.*;

/** A stream handler for java logging that flushes after every write.
 * 
 * @author		Andy Seaborne
 * @version 	$Id: StreamHandlerFlush.java,v 1.1 2004-11-03 10:15:05 andy_seaborne Exp $
 */

public class StreamHandlerFlush extends StreamHandler
{
    OutputStream output = null ;
    
    public StreamHandlerFlush()
    {
        super() ;
        configure2() ;
        super.setOutputStream(System.out);
        output = System.out ;
    }
     
    public StreamHandlerFlush(OutputStream out, Formatter formatter)
    {
        super(out, formatter) ;
        output = out ;
        configure2() ;
    }

    // Unfortunately, much of class StreamHandler internals are not 
    // accessible.  This code "fixes" it so that properties based on
    // this class's name will be in effect.
    // This method is derived from StreamHandler.configure
    
    private void configure2()
    {
        String cname = StreamHandlerFlush.class.getName();

        LogManager manager = LogManager.getLogManager();

        // Level
        String tmp = manager.getProperty(cname + ".level");
        if (tmp == null)
            tmp = "INFO";
        setLevel(Level.parse(tmp));

        // Filter
        tmp = manager.getProperty(cname + ".filter");
        try
        {
            if (tmp != null)
            {
                Class clz = ClassLoader.getSystemClassLoader().loadClass(tmp);
                setFilter((Filter) clz.newInstance());
            }
        }
        catch (Exception ex)
        {
            // We got one of a variety of exceptions in creating the
            // class or creating an instance.
            // Drop through.
        }

        // Formatter
        tmp = manager.getProperty(cname + ".formatter");
        if (tmp == null)
            tmp = OneLineFormatter.class.getName();
        try
        {
            Class clz = ClassLoader.getSystemClassLoader().loadClass(tmp);
            setFormatter((Formatter) clz.newInstance());
        }
        catch (Exception ex)
        {
            // We got one of a variety of exceptions in creating the
            // class or creating an instance.
            // Drop through.
        }

        /// Encoding
        try
        {
            setEncoding(manager.getProperty(cname + ".encoding"));
        }
        catch (Exception ex)
        {
            try
            {
                setEncoding(null);
            }
            catch (Exception ex2)
            {
                // doing a setEncoding with null should always work.
                // assert false;
            }
        }
    }
    public void publish(LogRecord record)
    {
        super.publish(record) ;
        super.flush() ;
        //try { output.flush() ; } catch (IOException ioEx) {}
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
