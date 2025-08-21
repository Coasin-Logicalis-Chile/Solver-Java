@echo off
echo.
echo =====================================================
echo   Concurrent Exception Reproduction Tests
echo =====================================================
echo.

REM Set JAVA_HOME for this session
set "JAVA_HOME=C:\Program Files\Java\jdk-21"

echo Setting JAVA_HOME to: %JAVA_HOME%
echo.

echo Running concurrent exception reproduction tests...
echo.

REM Run the test class directly
java -cp "target/classes;target/test-classes;%USERPROFILE%\.m2\repository\org\springframework\boot\spring-boot-starter-test\2.3.4.RELEASE\*;%USERPROFILE%\.m2\repository\junit\junit\4.13\*" com.logicalis.apisolver.ConcurrentExceptionReproducer

REM Alternative: Run with Maven
echo.
echo ========================================
echo Alternative: Running with Maven Test
echo ========================================
echo.

mvn test -Dtest=ConcurrentExceptionReproducer

echo.
echo =====================================================
echo   Tests completed!
echo =====================================================
pause
