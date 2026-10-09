@echo off
chcp 65001 >nul
title CodeAlpha - Task 4: Hotel Reservation System
echo ============================================================
echo   CodeAlpha - Task 4: Hotel Reservation System
echo ============================================================
echo.

if not exist "bin" mkdir bin

set "JAVAC_CMD=javac"
set "JAVA_CMD=java"

if exist "C:\Program Files\Java\jdk-21.0.12\bin\javac.exe" (
    set "JAVAC_CMD=C:\Program Files\Java\jdk-21.0.12\bin\javac.exe"
    set "JAVA_CMD=C:\Program Files\Java\jdk-21.0.12\bin\java.exe"
)

echo Compiling Java source files...
"%JAVAC_CMD%" -encoding UTF-8 -d bin src\*.java
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo Starting Hotel Reservation System...
"%JAVA_CMD%" -Dfile.encoding=UTF-8 -cp bin Main
pause
