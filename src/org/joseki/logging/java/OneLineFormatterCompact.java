/*
 * (c) Copyright 2003, 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package org.joseki.logging.java;

import java.io.* ;
import java.util.logging.*;

/** Java logging formatter that record logs with an abbreviated output,
 *  suitable for servlet logging where timestamps will be
 *  added by the underlying system.
 * 
 * @author		Andy Seaborne
 * @version 	$Id: OneLineFormatterCompact.java,v 1.1 2004-11-03 10:15:05 andy_seaborne Exp $
 */

public class OneLineFormatterCompact extends Formatter
{
	// Derived from java.util.logging.SimpleFormatter
	static public int nameColWidth = 30 ;

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
        
        if ( ! record.getLevel().equals(Level.INFO))
        {
    		sb.append(record.getLevel().getLocalizedName());
            sb.append(": ");
        }

        // Get just the classname, no package name (my pref for small/medium systems)
        // May also be the replacement name (e.g. a servlet name)
        // when overridding the default logging style.
         
		if (record.getSourceClassName() != null)
		{
			if (!record.getSourceClassName().equals(""))
			{
				String str = record.getSourceClassName();
				int i = str.lastIndexOf('.');
				if (i >= 0)
                    sb.append(str.substring(i + 1));
                else
                    sb.append(str) ;
			}
		}
		
		if ( record.getSourceMethodName() != null )
		{
            sb.append(".");
			sb.append(record.getSourceMethodName());
		}
		
        sb.append(": ") ;
		
        if ( record.getLevel().equals(Level.WARNING) ||
             record.getLevel().equals(Level.SEVERE) )
        {
            sb.append(record.getLevel().getLocalizedName()) ;
            sb.append(" :: ") ;
        }


		String message = formatMessage(record);
		sb.append(message);
		
        // Does not end in a line separator.
        
		if (record.getThrown() != null)
		{
			try
			{
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
                sb.append(lineSeparator);
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

