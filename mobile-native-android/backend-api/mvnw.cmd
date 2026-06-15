@echo off
setlocal

REM Set JAVA_HOME directly (Microsoft JDK 17)
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot

REM Set Maven home to downloaded distribution
set MAVEN_HOME=%~dp0apache-maven-3.9.5

REM Execute Maven
"%MAVEN_HOME%\bin\mvn.cmd" %*

endlocal
