@echo off
setlocal
cd /d "%~dp0"
echo ======================================================================
echo            Starting Android Emulator (Medium_Phone)...
echo ======================================================================
echo.

set "EMU_PATH=%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"
if not exist "%EMU_PATH%" (
    if defined ANDROID_HOME (
        set "EMU_PATH=%ANDROID_HOME%\emulator\emulator.exe"
    )
)

if not exist "%EMU_PATH%" (
    echo [ERROR] emulator.exe not found in standard Android SDK paths.
    pause
    exit /b 1
)

start "" "%EMU_PATH%" -avd Medium_Phone -gpu host
echo Emulator launched in standalone window!
echo You can now interact with it directly on your desktop.
timeout /t 3 >nul
