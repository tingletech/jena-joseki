@REM $Id: rdfqueryremote.bat,v 1.2 2003/09/15 12:58:20 andy_seaborne Exp $
@echo off

if not "%JOSEKIROOT%" == "" goto :ok

echo Environment variable JOSEKIROOT not set
goto theEnd

:ok
call bin\joseki_path
set CLASSPATH=%CP%
java joseki.rdfqueryremote %1 %2 %3 %4 %5 %6 %7 %8 %9

:theEnd
