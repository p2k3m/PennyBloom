@ECHO OFF
SETLOCAL

SET DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

SET DIR=%~dp0
SET APP_HOME=%DIR%
SET CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

IF NOT "%JAVA_HOME%"=="" (
    SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
) ELSE (
    SET JAVA_EXE=java
)

"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
