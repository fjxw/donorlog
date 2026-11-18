# ==============================================================================
# DonorLog - Скрипт одновременного запуска бэкенда (Spring Boot) и Android-клиента
# ==============================================================================
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host ""
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "            DONORLOG - СИСТЕМА ЕДИНОГО ЗАПУСКА ПРОЕКТА               " -ForegroundColor Yellow
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host ""

$BaseDir = $PSScriptRoot
if (-not $BaseDir) {
    $BaseDir = (Get-Location).Path
}

# --- 1. АВТОМАТИЧЕСКИЙ ПОИСК ДИРЕКТОРИЙ ПРОЕКТА ---
$PossibleBackendDirs = @(
    (Join-Path $BaseDir "donorlog-backend"),
    (Join-Path $BaseDir "donorlog-repo\donorlog-backend"),
    (Join-Path $BaseDir "Исходные файлы\donorlog-backend"),
    (Join-Path $BaseDir "..\Исходные файлы\donorlog-backend"),
    (Join-Path $BaseDir "..\donorlog-repo\donorlog-backend")
)
$BackendDir = $PossibleBackendDirs | Where-Object { 
    (Test-Path -LiteralPath (Join-Path $_ "build.gradle")) -or 
    (Test-Path -LiteralPath (Join-Path $_ "build.gradle.kts")) 
} | Select-Object -First 1

$PossibleAndroidDirs = @(
    (Join-Path $BaseDir "donorlog-android"),
    (Join-Path $BaseDir "donorlog-repo\donorlog-android"),
    (Join-Path $BaseDir "Исходные файлы\donorlog-android"),
    (Join-Path $BaseDir "..\Исходные файлы\donorlog-android"),
    (Join-Path $BaseDir "..\donorlog-repo\donorlog-android")
)
$AndroidDir = $PossibleAndroidDirs | Where-Object { 
    (Test-Path -LiteralPath (Join-Path $_ "build.gradle")) -or 
    (Test-Path -LiteralPath (Join-Path $_ "build.gradle.kts")) 
} | Select-Object -First 1

if (-not $BackendDir -or -not $AndroidDir) {
    Write-Host "[!] Ошибка: Не удалось обнаружить модули donorlog-backend или donorlog-android!" -ForegroundColor Red
    Write-Host "    Проверьте, что скрипт находится в корне проекта или репозитория." -ForegroundColor Yellow
    exit 1
}

Write-Host "Путь к бэкенду: $BackendDir" -ForegroundColor Gray
Write-Host "Путь к клиенту: $AndroidDir" -ForegroundColor Gray
Write-Host ""

# --- 2. ПОИСК УТИЛИТ ANDROID SDK (ADB, EMULATOR) ---
$AdbCmd = Get-Command adb -ErrorAction SilentlyContinue
$AdbExe = if ($AdbCmd) { $AdbCmd.Source } else {
    @(
        "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
        "$env:ANDROID_HOME\platform-tools\adb.exe"
    ) | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1
}

$EmuCmd = Get-Command emulator -ErrorAction SilentlyContinue
$EmulatorExe = if ($EmuCmd) { $EmuCmd.Source } else {
    @(
        "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe",
        "$env:ANDROID_HOME\emulator\emulator.exe"
    ) | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1
}

if (-not $AdbExe) {
    Write-Host "[!] ADB не найден в системе. Убедитесь, что Android SDK установлен." -ForegroundColor Red
    exit 1
}

# --- 3. ЗАПУСК БЭКЕНДА (SPRING BOOT) ---
Write-Host "[1/4] Проверка и запуск серверного бэкенда Spring Boot..." -ForegroundColor Green
$BackendRunning = $false
try {
    $conn = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($conn) { $BackendRunning = $true }
} catch {}

if ($BackendRunning) {
    Write-Host "      Серверный бэкенд уже запущен и слушает порт 8080." -ForegroundColor Green
} else {
    $BackendJar = Join-Path $BackendDir "build\libs\donorlog-backend-1.0.0.jar"
    if (-not (Test-Path -LiteralPath $BackendJar)) {
        Write-Host "      Сборка JAR-пакета бэкенда..." -ForegroundColor Gray
        Push-Location $BackendDir
        & .\gradlew.bat bootJar -x test
        Pop-Location
    }

    # Запуск бэкенда через start_backend.bat без проблем с кодировкой путей
    $StartBackendScript = Join-Path $BaseDir "start_backend.bat"
    if (-not (Test-Path -LiteralPath $StartBackendScript)) {
        $StartBackendScript = Join-Path $BackendDir "..\start_backend.bat"
    }

    Write-Host "      Запуск бэкенда в отдельном окне..." -ForegroundColor Cyan
    Start-Process cmd.exe -ArgumentList "/c", "start", "`"DonorLog Backend (Port 8080)`"", "`"$StartBackendScript`""

    Write-Host "      Ожидание инициализации REST API (http://localhost:8080)..." -ForegroundColor Gray
    for ($i = 0; $i -lt 25; $i++) {
        try {
            $resp = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/health" -TimeoutSec 2 -ErrorAction SilentlyContinue
            if ($resp -and $resp.success) {
                Write-Host "      Бэкенд успешно готов к приему запросов!" -ForegroundColor Green
                break
            }
        } catch {}
        Start-Sleep -Seconds 1
    }
}

# --- 4. ПРОВЕРКА И ПОДГОТОВКА ЭМУЛЯТОРА / УСТРОЙСТВА ---
Write-Host "[2/4] Проверка Android-устройства / эмулятора..." -ForegroundColor Green
$ActiveDevices = & $AdbExe devices | Select-String "\bdevice\b"

if (-not $ActiveDevices) {
    Write-Host "      Активное устройство не обнаружено. Поиск доступных AVD..." -ForegroundColor Yellow
    if ($EmulatorExe) {
        $AvdRaw = & $EmulatorExe -list-avds 2>$null
        $AvdList = ($AvdRaw -split "\r?\n") | Where-Object { $_ -and -not $_.StartsWith("INFO") -and -not $_.StartsWith("WARN") } | ForEach-Object { $_.Trim() }
        $TargetAvd = if ($AvdList.Count -gt 0 -and $AvdList[0].Length -gt 1) { $AvdList[0] } else { "Medium_Phone" }
        
        Write-Host "      Запуск оптимизированного эмулятора: $TargetAvd (GPU: host)..." -ForegroundColor Cyan
        Start-Process $EmulatorExe -ArgumentList "-avd", $TargetAvd, "-gpu", "host"
        Write-Host "      Ожидание подключения эмулятора..." -ForegroundColor Gray
        & $AdbExe wait-for-device
        
        # Ожидание полной загрузки системы
        $Booted = $false
        for ($i = 0; $i -lt 40; $i++) {
            $BootState = (& $AdbExe shell getprop sys.boot_completed 2>$null)
            if ($BootState -and "$BootState".Trim() -eq "1") {
                $Booted = $true
                break
            }
            Start-Sleep -Seconds 2
        }
    } else {
        Write-Host "      [!] Утилита emulator.exe не найдена. Ожидание внешнего устройства..." -ForegroundColor Yellow
        & $AdbExe wait-for-device
    }
} else {
    Write-Host "      Устройство найдено и готово к работе." -ForegroundColor Green
}

# Настройка проброса порта (reverse proxy) для REST API
Write-Host "      Настройка сети (adb reverse tcp:8080 tcp:8080)..." -ForegroundColor Gray
& $AdbExe reverse tcp:8080 tcp:8080

# --- 5. СБОРКА И УСТАНОВКА ANDROID-КЛИЕНТА ---
Write-Host "[3/4] Сборка и установка приложения на устройство..." -ForegroundColor Green
Push-Location $AndroidDir
& .\gradlew.bat installDebug
Pop-Location

# --- 6. ЗАПУСК ГЛАВНОГО ЭКРАНА ---
Write-Host "[4/4] Запуск DonorLog на экране устройства..." -ForegroundColor Green
& $AdbExe shell am start -n ru.mospolytech.donorlog/.ui.MainActivity

Write-Host ""
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "   ПРОЕКТ УСПЕШНО ЗАПУЩЕН!                                            " -ForegroundColor Green
Write-Host "   - Бэкенд:       http://localhost:8080/api/v1/health               " -ForegroundColor White
Write-Host "   - Станции:      http://localhost:8080/api/v1/stations             " -ForegroundColor White
Write-Host "   - Клиент:       ru.mospolytech.donorlog на экране эмулятора       " -ForegroundColor White
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host ""
