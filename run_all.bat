@echo off
chcp 65001 > nul
title DonorLog Launcher
echo ======================================================================
echo             DONORLOG - СИСТЕМА ЕДИНОГО ЗАПУСКА ПРОЕКТА
echo ======================================================================
echo.

set "SCRIPT_DIR=%~dp0"
set "BACKEND_DIR=%SCRIPT_DIR%donorlog-backend"
set "ANDROID_DIR=%SCRIPT_DIR%donorlog-android"
set "BACKEND_JAR=%BACKEND_DIR%\build\libs\donorlog-backend-1.0.0.jar"

echo [1/4] Запуск серверного бэкенда Spring Boot...
if not exist "%BACKEND_JAR%" (
    echo       Сборка JAR-пакета бэкенда...
    cd /d "%BACKEND_DIR%"
    call gradlew.bat bootJar -x test
)

start "DonorLog Backend (Port 8080)" cmd /k "cd /d "%BACKEND_DIR%" && if exist "%BACKEND_JAR%" ( java -jar "%BACKEND_JAR%" ) else ( call gradlew.bat bootRun )"
echo       Бэкенд запущен в отдельном окне (http://localhost:8080).

echo [2/4] Проверка Android-устройства / эмулятора...
adb get-state > nul 2>&1
if errorlevel 1 (
    echo       Устройство не обнаружено, ожидание подключения...
    adb wait-for-device
) else (
    echo       Устройство обнаружено.
)

echo       Настройка проброса порта adb reverse tcp:8080 tcp:8080...
adb reverse tcp:8080 tcp:8080

echo [3/4] Сборка и установка Android-приложения...
cd /d "%ANDROID_DIR%"
call gradlew.bat installDebug

echo [4/4] Запуск приложения на устройстве...
adb shell am start -n ru.mospolytech.donorlog/.ui.MainActivity

echo.
echo ======================================================================
echo    ПРОЕКТ УСПЕШНО ЗАПУЩЕН!
echo    - Бэкенд:       http://localhost:8080/api/v1/health
echo    - Станции:      http://localhost:8080/api/v1/stations
echo    - Клиент:       ru.mospolytech.donorlog на экране эмулятора
echo ======================================================================
echo.
pause
