@echo off
setlocal
cd /d "%~dp0"
title DonorLog Spring Boot Backend (Port 8080)
echo ======================================================================
echo              DonorLog Spring Boot Backend (Port 8080)
echo ======================================================================
echo.

java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java 17+ is required but not found in PATH.
    pause
    exit /b 1
)

set "JAR_PATH=donorlog-backend\build\libs\donorlog-backend-1.0.0.jar"
if not exist "%JAR_PATH%" (
    set "JAR_PATH=build\libs\donorlog-backend-1.0.0.jar"
)

if exist "%JAR_PATH%" (
    echo Starting compiled JAR package: %JAR_PATH%
    java -jar "%JAR_PATH%"
) else (
    echo Building and running backend via Gradle...
    if exist "donorlog-backend\gradlew.bat" (
        cd donorlog-backend
        call gradlew.bat bootRun
    ) else (
        call gradlew.bat bootRun
    )
)

if errorlevel 1 (
    echo.
    echo [ERROR] Backend stopped or port 8080 is in use.
    pause
)
