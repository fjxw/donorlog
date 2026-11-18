@echo off
setlocal
cd /d "%~dp0"
echo ======================================================================
echo             Starting DonorLog Full-Stack Launcher...
echo ======================================================================
echo.

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run_all.ps1"
if errorlevel 1 (
    echo.
    echo [ERROR] The launcher script encountered an error.
)
echo.
pause
