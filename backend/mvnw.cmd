@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script, version 3.2.0
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set MAVEN_WRAPPER_DIR=%~dp0\.mvn\wrapper
set MAVEN_WRAPPER_JAR=%MAVEN_WRAPPER_DIR%\maven-wrapper.jar
set MAVEN_WRAPPER_PROPERTIES=%MAVEN_WRAPPER_DIR%\maven-wrapper.properties

if not exist "%MAVEN_WRAPPER_PROPERTIES%" (
  echo [ERROR] Could not find "%MAVEN_WRAPPER_PROPERTIES%".
  exit /b 1
)

for /f "usebackq tokens=1,* delims==" %%A in ("%MAVEN_WRAPPER_PROPERTIES%") do (
  if /I "%%A"=="wrapperUrl" set WRAPPER_URL=%%B
  if /I "%%A"=="distributionUrl" set DISTRIBUTION_URL=%%B
)

if "%WRAPPER_URL%"=="" (
  set WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
)

if not exist "%MAVEN_WRAPPER_JAR%" (
  echo Downloading Maven Wrapper jar...
  if not exist "%MAVEN_WRAPPER_DIR%" mkdir "%MAVEN_WRAPPER_DIR%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ProgressPreference='SilentlyContinue';" ^
    "Invoke-WebRequest -UseBasicParsing -Uri '%WRAPPER_URL%' -OutFile '%MAVEN_WRAPPER_JAR%'" || (
      echo [ERROR] Failed to download Maven Wrapper jar from %WRAPPER_URL%
      exit /b 1
    )
)

if "%JAVA_HOME%"=="" (
  set JAVA_EXE=java
) else (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

if not exist "%JAVA_EXE%" (
  echo [ERROR] Java not found. Set JAVA_HOME to a JDK 17 installation.
  exit /b 1
)

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" ^
  -classpath "%MAVEN_WRAPPER_JAR%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*

endlocal


