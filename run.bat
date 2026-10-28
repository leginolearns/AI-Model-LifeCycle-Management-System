@echo off
setlocal
cd /d "%~dp0"
if not defined DB_USER set DB_USER=root
if not defined DB_PASSWORD set /p DB_PASSWORD=Enter your local MySQL password: 
where java >nul 2>nul || (echo Java JDK is required. & pause & exit /b 1)
where javac >nul 2>nul || (echo javac was not found. Install a JDK and add it to PATH. & pause & exit /b 1)
if not exist "lib\mysql-connector-j-26.7.0.jar" (echo MySQL Connector/J JAR is missing from lib. & pause & exit /b 1)
if not exist "target\classes" mkdir "target\classes"
javac -encoding UTF-8 -cp "lib\mysql-connector-j-26.7.0.jar" -d "target\classes" src\*.java
if errorlevel 1 (echo Compilation failed. & pause & exit /b 1)
java -cp "target\classes;lib\mysql-connector-j-26.7.0.jar" Main
pause
