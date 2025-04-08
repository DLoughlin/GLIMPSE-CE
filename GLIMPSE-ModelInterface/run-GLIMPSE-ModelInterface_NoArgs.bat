rem If the following command does not start the ScenarioBuilder, correct the JAVA_HOME path

rem Set these variables
set JAVA_HOME=..\amazon-corretto-8.442.06.1-windows-x64-jre

rem Checking JAVA_HOME setup
if not exist "%JAVA_HOME%"\bin\java.exe (
  echo JAVA_HOME setting needs to be fixed
  pause
  GOTO END
) 

set JAVA_JVM_PATH=%JAVA_HOME%\bin\server

set PATH=.;%JAVA_JVM_PATH%;%JAVA_HOME%;%JAVA_HOME%\bin;%PATH%

start java -jar ./GLIMPSE-ModelInterface.jar 