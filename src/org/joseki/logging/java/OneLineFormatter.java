/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.logging.java;

import java.util.Date ;
import java.text.* ;
import java.io.* ;
import java.util.logging.*;

/** Java logging formatter that record logs on one line.
 * 
 * @author		Andy Seaborne
 * @version 	$Id: OneLineFormatter.java,v 1.1 2004-11-03 10:15:05 andy_seaborne Exp $
 */
public class OneLineFormatter extends Formatter
{
	// Derived from java.util.logging.SimpleFormatter
    // Looks sorta like the Log4j pattern 
	static public int nameColWidth = 25 ;
    
	Date date = new Date();
    
    // If the format isn't what you expect (e.g. wrong Locale)
    // please note some Java versions like to default to the wrong Locale.
    // Under Java 1.4.2_b28 the default, here in the UK, is back to en_US.
    // Humph. 
    
    //DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM) ;
    //DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSS z") ;

    // Use a "universal" format.  Set this else where if you want something different.
    public DateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss") ;

	// Line separator string.  This is the value of the line.separator
	// property at the moment that the SimpleFormatter was created.
	private String lineSeparator =
		(String) java.security.AccessController.doPrivileged(
			new sun.security.action.GetPropertyAction("line.separator"));

	/**
	 * Format the given LogRecord.
	 * @param record the log record to be formatted.
	 * @return a formatted log record
	 */
	public synchronized String format(LogRecord record)
	{
		StringBuffer sb = new StringBuffer();
		// Minimize memory allocations here.
		date.setTime(record.getMillis());
		StringBuffer text = new StringBuffer();

        sb.append(dateAndTimeFormat.format(date)) ;

		sb.append(" ") ;
        String tmp = record.getLevel().getLocalizedName() ;
		sb.append(tmp);
        // Pad out.
        for ( int i = tmp.length() ; i < 6 ; i++ )
            sb.append(' ') ;
        
		sb.append(" : ");

        int len1 = sb.length() ;

        // Get just the classname, no package name (my pref for small/medium systems)
        // May also be the replacement name (e.g. a servlet name)
        // when overridding the default logging style.
         
		if (record.getSourceClassName() != null)
		{
			if (!record.getSourceClassName().equals(""))
			{
                sb.append(" ");
				String str = record.getSourceClassName();
				int i = str.lastIndexOf('.');
				if (i >= 0)
                    sb.append(str.substring(i + 1));
                else
                    sb.append(str) ;
			}
		}
		
//		if ( record.getSourceMethodName() != null )
//		{
//            sb.append(".");
//			sb.append(record.getSourceMethodName());
//		}
		
        // Pad out.
        for ( int i = sb.length() ; i < len1+nameColWidth ; i++ )
            sb.append(' ') ;
        
        // The standard simple formatter inserts a line separator at this point
		//sb.append(lineSeparator);
		sb.append(" :: ") ;
		
        if ( record.getLevel().equals(Level.WARNING) ||
             record.getLevel().equals(Level.SEVERE) )
        {
            sb.append(record.getLevel().getLocalizedName()) ;
            sb.append(" :: ") ;
        }


		String message = formatMessage(record);
		sb.append(message);
		
		sb.append(lineSeparator);
		if (record.getThrown() != null)
		{
			try
			{
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			}
			catch (Exception ex)
			{
			}
		}
		return sb.toString();
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

