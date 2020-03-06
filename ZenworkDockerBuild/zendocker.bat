echo off

java -cp "%~dp0zenWorksBuild.jar;%~dp0jna-3.2.5.jar;%~dp0jsch-0.1.54.jar;%~dp0classindex-3.1.jar;" com.novell.zenwoks.command.process.ZenDockerCommandProcessor %*