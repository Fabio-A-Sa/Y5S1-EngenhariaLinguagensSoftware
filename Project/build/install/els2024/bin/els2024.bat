@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  els2024 startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and ELS2024_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\els2024.jar;%APP_HOME%\lib\utils-sources.jar;%APP_HOME%\lib\domainmodel.jar;%APP_HOME%\lib\utils.jar;%APP_HOME%\lib\preql.jar;%APP_HOME%\lib\jackson-dataformat-xml-2.15.0.jar;%APP_HOME%\lib\jackson-core-2.15.0.jar;%APP_HOME%\lib\jackson-annotations-2.15.0.jar;%APP_HOME%\lib\jackson-dataformat-yaml-2.15.0.jar;%APP_HOME%\lib\jackson-databind-2.15.0.jar;%APP_HOME%\lib\gson-2.8.8.jar;%APP_HOME%\lib\org.eclipse.xtext-2.37.0.jar;%APP_HOME%\lib\fastdoubleparser-0.8.0.jar;%APP_HOME%\lib\snakeyaml-2.0.jar;%APP_HOME%\lib\woodstox-core-6.5.1.jar;%APP_HOME%\lib\stax2-api-4.2.1.jar;%APP_HOME%\lib\org.eclipse.xtext.util-2.37.0.jar;%APP_HOME%\lib\org.eclipse.xtend.lib-2.37.0.jar;%APP_HOME%\lib\reload4j-1.2.25.jar;%APP_HOME%\lib\org.eclipse.equinox.common-3.17.100.jar;%APP_HOME%\lib\org.eclipse.osgi-3.18.300.jar;%APP_HOME%\lib\org.eclipse.emf.ecore.xmi-2.16.0.jar;%APP_HOME%\lib\org.eclipse.emf.ecore-2.26.0.jar;%APP_HOME%\lib\org.eclipse.emf.common-2.24.0.jar;%APP_HOME%\lib\guice-7.0.0.jar;%APP_HOME%\lib\antlr-runtime-3.2.jar;%APP_HOME%\lib\org.eclipse.xtend.lib.macro-2.37.0.jar;%APP_HOME%\lib\org.eclipse.xtext.xbase.lib-2.37.0.jar;%APP_HOME%\lib\jakarta.inject-api-2.0.1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\guava-33.3.1-jre.jar;%APP_HOME%\lib\failureaccess-1.0.2.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-3.43.0.jar;%APP_HOME%\lib\error_prone_annotations-2.28.0.jar;%APP_HOME%\lib\j2objc-annotations-3.0.0.jar


@rem Execute els2024
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %ELS2024_OPTS%  -classpath "%CLASSPATH%" pt.up.fe.els2024.Main %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable ELS2024_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%ELS2024_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
