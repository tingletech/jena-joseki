@echo off
@REM $Id: rdffetch.bat,v 1.3 2004/01/07 15:43:59 andy_seaborne Exp $

if not "%JOSEKIROOT%" == "" goto :ok

echo Environment variable JOSEKIROOT not set
goto theEnd

:ok
call bin\joseki_path
java -cp %CP% joseki.rdffetch %1 %2 %3 %4 %5 %6 %7 %8 %9

:theEnd
