# ==============================================================================
# DonorLog - Скрипт одновременного запуска бэкенда (Spring Boot) и Android-клиента
# ==============================================================================
$ErrorActionPreference = "Continue"

Write-Host ""
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "            DONORLOG - СИСТЕМА ЕДИНОГО ЗАПУСКА ПРОЕКТА               " -ForegroundColor Yellow
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host ""

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$BackendDir = Join-Path $ScriptDir "donorlog-backend"
$AndroidDir = Join-Path $ScriptDir "donorlog-android"

# --- 1. ПРОВЕРКА И ЗАПУСК БЭКЕНДА (SPRING BOOT) ---
Write-Host "[1/4] Запуск серверного бэкенда Spring Boot..." -ForegroundColor Green
$BackendJar = Join-Path $BackendDir "build\libs\donorlog-backend-1.0.0.jar"

if (-not (Test-Path $BackendJar)) {
    Write-Host "      Сборка JAR-пакета бэкенда..." -ForegroundColor Gray
    Push-Location $BackendDir
    & .\gradlew.bat bootJar -x test
    Pop-Location
}

# Запуск бэкенда в отдельном информационном окне
$BackendCmd = "Write-Host '=== DonorLog Spring Boot Backend (Port 8080) ===' -ForegroundColor Cyan; cd '$BackendDir'; if (Test-Path '$BackendJar') { java -jar '$BackendJar' } else { .\gradlew.bat bootRun }"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $BackendCmd
Write-Host "      Бэкенд запущен в отдельном окне на http://localhost:8080" -ForegroundColor Green

# --- 2. ПРОВЕРКА И ПОДГОТОВКА ЭМУЛЯТОРА / УСТРОЙСТВА ---
Write-Host "[2/4] Проверка Android-устройства / эмулятора..." -ForegroundColor Green
$DeviceState = (& adb get-state 2>&1).Trim()

if ($DeviceState -ne "device") {
    Write-Host "      Подключенное устройство не найдено. Поиск доступных AVD..." -ForegroundColor Yellow
    $AvdList = & emulator -list-avds 2>&1
    if ($AvdList -and $AvdList.Count -gt 0 -and (-not $AvdList[0].StartsWith("error"))) {
        $TargetAvd = $AvdList[0].Trim()
        Write-Host "      Запуск эмулятора: $TargetAvd..." -ForegroundColor Cyan
        Start-Process emulator -ArgumentList "@$TargetAvd"
        Write-Host "      Ожидание загрузки Android..." -ForegroundColor Gray
        & adb wait-for-device
    } else {
        Write-Host "      [!] AVD не найден. Подключите физическое устройство по USB или запустите эмулятор." -ForegroundColor Yellow
    }
} else {
    Write-Host "      Устройство обнаружено: $DeviceState" -ForegroundColor Green
}

# Проброс порта для доступа эмулятора к локальному бэкенду
Write-Host "      Настройка reverse-прокси adb reverse tcp:8080 tcp:8080..." -ForegroundColor Gray
& adb reverse tcp:8080 tcp:8080

# --- 3. СБОРКА И УСТАНОВКА ANDROID-ПРИЛОЖЕНИЯ ---
Write-Host "[3/4] Сборка и установка Android-приложения..." -ForegroundColor Green
Push-Location $AndroidDir
& .\gradlew.bat installDebug
Pop-Location

# --- 4. ЗАПУСК ГЛАВНОГО ЭКРАНА ПРИЛОЖЕНИЯ ---
Write-Host "[4/4] Запуск приложения на устройстве..." -ForegroundColor Green
& adb shell am start -n ru.mospolytech.donorlog/.ui.MainActivity

Write-Host ""
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "   ПРОЕКТ УСПЕШНО ЗАПУЩЕН!                                            " -ForegroundColor Green
Write-Host "   - Бэкенд:       http://localhost:8080/api/v1/health               " -ForegroundColor White
Write-Host "   - Станции:      http://localhost:8080/api/v1/stations             " -ForegroundColor White
Write-Host "   - Клиент:       ru.mospolytech.donorlog на экране эмулятора       " -ForegroundColor White
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host ""
