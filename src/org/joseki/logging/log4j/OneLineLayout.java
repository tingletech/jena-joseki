/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.logging.log4j;

import org.apache.log4j.* ;
import org.apache.log4j.spi.*;

import java.util.* ;
import java.text.* ;
/**
 * There are a few things the standard log4j PatternLayout does not do:
 * 
 * 1/ I prefer log messages as "class.method", and column aligned.
 * There is no way to align a compound item like "%C{1}.%M"
 * Could hack the Log4j code to do %30{%C.%M}-like patterns
 * 
 * 2/ I prefer message to add WARNING/ERROR/FATAL to make them standout
 *    but not INFO.  This I find helpful for verbose logging situations.
 *    Could do this by adding a second appender if the first only output
 *    INFO and below level output.
 *    Can be done with filters, except are they possible in a log4j.properties file?
 * 
 * Either I put up with the standard output, find out how to modifiy it (and there
 * are some package local accesses so extending is impossible), or write my own.
 * 
 * Hence this - a directly done layout.
 *  
 * @author Andy Seaborne
 * @version $Id: OneLineLayout.java,v 1.1 2004-11-03 10:15:06 andy_seaborne Exp $
 */
public class OneLineLayout extends Layout
{
    Date date = new Date();
    public DateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss") ;

    // Line separator string.  This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.
    private String lineSeparator =
        (String) java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));

    
    static public int nameColWidth = 30 ;

    
    public OneLineLayout()
    {
    }

    public String format(LoggingEvent ev)
    {
        StringBuffer sb = new StringBuffer();
        // Minimize memory allocations here.
        date.setTime(System.currentTimeMillis());
        StringBuffer text = new StringBuffer();

        sb.append(dateAndTimeFormat.format(date)) ;
        sb.append(" - ") ;

        boolean nameClassMethod = true ;
      
        if ( nameClassMethod )
        {    
            LocationInfo info = ev.getLocationInformation() ;
            String clsStr = info.getClassName() ;
            
            int ind = clsStr.lastIndexOf('.') ;
            if ( ind > -1 )
                clsStr = clsStr.substring(ind+1) ;
            
            String methStr = info.getMethodName() ;
            int len = 0 ;
            
            sb.append(clsStr) ; len += clsStr.length() ;
            sb.append(".") ;    len += 1 ;
            sb.append(methStr) ; len += methStr.length() ;
            
            for ( int i = len ; i <= nameColWidth ; i++ )
                sb.append(' ') ;
        }
        else
        {
            
            String loggerName = ev.getLoggerName() ;
            sb.append(loggerName) ;
            for ( int i = loggerName.length() ; i <= 20 ; i++ )
                sb.append(' ') ;
        }
        
        sb.append(" :: ") ;
        if ( ev.getLevel() == Level.WARN )
            sb.append("WARN: ") ;
        else if ( ev.getLevel() == Level.ERROR )
            sb.append("ERROR: ") ;
        else if ( ev.getLevel() == Level.FATAL )
            sb.append("FATAL: ") ;
        sb.append(ev.getMessage().toString()) ;
        sb.append(lineSeparator) ;
        return sb.toString() ;
    }

    public boolean ignoresThrowable()
    {
        return true;
    }

    public void activateOptions()
    {
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
