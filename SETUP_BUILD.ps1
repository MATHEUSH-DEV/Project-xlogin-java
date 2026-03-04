# ============================================
# SETUP BUILD SCRIPT - Kronus Rift Game
# Instala CMake, Compila C++ Game Engine
# ============================================

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Kronus Rift - Build Setup" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# ============ 1. Check CMake ============
Write-Host "[1/4] Verificando CMake..." -ForegroundColor Yellow
$cmakePath = Get-Command cmake -ErrorAction SilentlyContinue

if ($cmakePath) {
    Write-Host "✅ CMake já instalado: $($cmakePath.Source)" -ForegroundColor Green
} else {
    Write-Host "❌ CMake não encontrado. Instalando..." -ForegroundColor Red
    
    # Download CMake
    $cmakeUrl = "https://github.com/Kitware/CMake/releases/download/v3.28.3/cmake-3.28.3-windows-x86_64.msi"
    $cmakeInstaller = "$env:TEMP\cmake-installer.msi"
    
    Write-Host "Baixando CMake..." -ForegroundColor Yellow
    try {
        Invoke-WebRequest -Uri $cmakeUrl -OutFile $cmakeInstaller -UseBasicParsing
        Write-Host "Instalando CMake..." -ForegroundColor Yellow
        Start-Process msiexec.exe -ArgumentList "/i `"$cmakeInstaller`" /quiet /norestart" -Wait
        Remove-Item $cmakeInstaller
        
        Write-Host "✅ CMake instalado com sucesso!" -ForegroundColor Green
    } catch {
        Write-Host "⚠️  Falha ao instalar CMake automaticamente" -ForegroundColor Yellow
        Write-Host "Baixe manualmente de: https://cmake.org/download/" -ForegroundColor Yellow
        Write-Host "Depois execute este script novamente" -ForegroundColor Yellow
        exit 1
    }
}

# Refreshar PATH
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

Write-Host ""
Write-Host "[2/4] Configurando projeto CMake..." -ForegroundColor Yellow

# ============ 2. Setup CMake ============
$projectRoot = Get-Location
$buildDir = Join-Path $projectRoot "c_game\build"

if (-not (Test-Path $buildDir)) {
    New-Item -ItemType Directory -Path $buildDir | Out-Null
    Write-Host "✅ Pasta build criada" -ForegroundColor Green
}

Set-Location $buildDir

Write-Host "Executando CMake configure..." -ForegroundColor Yellow
cmake ..\

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ CMake configure falhou!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Projeto CMake configurado" -ForegroundColor Green

Write-Host ""
Write-Host "[3/4] Compilando C++ Game Engine..." -ForegroundColor Yellow

# ============ 3. Build ============
cmake --build . --config Release --parallel 4

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Compilação falhou!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Compilação bem-sucedida!" -ForegroundColor Green

Write-Host ""
Write-Host "[4/4] Verificando executável..." -ForegroundColor Yellow

# ============ 4. Verify ============
$exePath = Join-Path $buildDir "Release\KronusRiftGame.exe"
$exePath2 = Join-Path $buildDir "KronusRiftGame.exe"

if (Test-Path $exePath) {
    Write-Host "✅ Executável encontrado: $exePath" -ForegroundColor Green
    Write-Host ""
    Write-Host "======================================" -ForegroundColor Cyan
    Write-Host "🎮 BUILD COMPLETO!" -ForegroundColor Green
    Write-Host "======================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Próximos passos:" -ForegroundColor Yellow
    Write-Host "1. Execute o Java Launcher: java -jar seu_projeto.jar" -ForegroundColor White
    Write-Host "2. Faça login com um usuário" -ForegroundColor White
    Write-Host "3. Crie um personagem no Lobby" -ForegroundColor White
    Write-Host "4. Clique em 'Entrar no Jogo'" -ForegroundColor White
    Write-Host "5. O Kronus Rift Game C++ deve abrir automaticamente!" -ForegroundColor White
    Write-Host ""
} elseif (Test-Path $exePath2) {
    Write-Host "✅ Executável encontrado: $exePath2" -ForegroundColor Green
    Write-Host ""
    Write-Host "======================================" -ForegroundColor Cyan
    Write-Host "🎮 BUILD COMPLETO!" -ForegroundColor Green
    Write-Host "======================================" -ForegroundColor Cyan
} else {
    Write-Host "⚠️  Executável não encontrado!" -ForegroundColor Yellow
    Write-Host "Verifique: $buildDir" -ForegroundColor Yellow
    exit 1
}

Set-Location $projectRoot
Write-Host "Voltando para: $projectRoot" -ForegroundColor Cyan
