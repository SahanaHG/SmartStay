@echo off
title SmartStay Luxury Hotel - Web Server
echo ============================================================
echo   SmartStay - Luxury Hotel & Reservation System Web Portal
echo ============================================================
echo.
echo Starting Web Server on http://localhost:8080 ...
echo.

cd /d "%~dp0"
start http://localhost:8080
"C:\Program Files\Java\jdk-21.0.12\bin\java.exe" -cp bin WebServer

pause
