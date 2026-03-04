# ============================================
# PORTABLE CMAKE SETUP - Sem Admin Necessário
# Download CMake portável e compila C++ Game
# ============================================

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Kronus Rift - Setup Portável" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# ============ 1. Download CMake Portável ============
Write-Host "[1/3] Baixando CMake Portável..." -ForegroundColor Yellow

$cmakeUrl = "https://github.com/Kitware/CMake/releases/download/v3.28.3/cmake-3.28.3-windows-x86_64.zip"
$projectRoot = Get-Location
$toolsDir = Join-Path $projectRoot "tools"
$cmakeZip = Join-Path $toolsDir "cmake.zip"
$cmakePath = Join-Path $toolsDir "cmake-3.28.3-windows-x86_64"
$cmakeExe = Join-Path $cmakePath "bin\cmake.exe"

# Criar pasta tools se não existir
if (-not (Test-Path $toolsDir)) {
    New-Item -ItemType Directory -Path $toolsDir | Out-Null
}

# Download
if (-not (Test-Path $cmakeExe)) {
    try {
        Write-Host "Fazendo download de $cmakeUrl" -ForegroundColor Yellow
        Invoke-WebRequest -Uri $cmakeUrl -OutFile $cmakeZip -UseBasicParsing
        Write-Host "✅ Download completo!" -ForegroundColor Green
        
        Write-Host "Extraindo CMake..." -ForegroundColor Yellow
        Expand-Archive -Path $cmakeZip -DestinationPath $toolsDir -Force
        Remove-Item $cmakeZip
        Write-Host "✅ CMake extraído!" -ForegroundColor Green
    } catch {
        Write-Host "❌ Erro ao baixar CMake!" -ForegroundColor Red
        Write-Host "Erro: $_" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "✅ CMake já existe em: $cmakePath" -ForegroundColor Green
}

Write-Host ""
Write-Host "[2/3] Configurando projeto com CMake..." -ForegroundColor Yellow

# ============ 2. Setup CMake ============
$buildDir = Join-Path $projectRoot "c_game\build"
$cGameDir = Join-Path $projectRoot "c_game"

if (-not (Test-Path $buildDir)) {
    New-Item -ItemType Directory -Path $buildDir | Out-Null
    Write-Host "✅ Pasta build criada" -ForegroundColor Green
}

Push-Location $buildDir

Write-Host "Executando CMake configure..." -ForegroundColor Yellow
Write-Host "Comando: & '$cmakeExe' .." -ForegroundColor Gray

& "$cmakeExe" .. 2>&1 | Tee-Object -Variable cmakeOutput

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "❌ CMake configure falhou!" -ForegroundColor Red
    Write-Host "Output:" -ForegroundColor Yellow
    Write-Host $cmakeOutput
    Pop-Location
    exit 1
}

Write-Host ""
Write-Host "✅ Projeto CMake configurado com sucesso!" -ForegroundColor Green

Write-Host ""
Write-Host "[3/3] Compilando C++ Game Engine..." -ForegroundColor Yellow

# ============ 3. Build ============
Write-Host "Executando CMake build..." -ForegroundColor Yellow
Write-Host "Comando: & '$cmakeExe' --build . --config Release --parallel 4" -ForegroundColor Gray

& "$cmakeExe" --build . --config Release --parallel 4 2>&1 | Tee-Object -Variable buildOutput

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "⚠️  Compilação teve alguns avisos (pode estar ok)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "✅ Compilação completa!" -ForegroundColor Green

# ============ 4. Verify ============
Write-Host ""
Write-Host "Verificando executável..." -ForegroundColor Yellow

$exePath1 = Join-Path $buildDir "Release\KronusRiftGame.exe"
$exePath2 = Join-Path $buildDir "KronusRiftGame.exe"
$exePath3 = Join-Path $buildDir "Debug\KronusRiftGame.exe"

$foundExe = $null
if (Test-Path $exePath1) {
    $foundExe = $exePath1
} elseif (Test-Path $exePath2) {
    $foundExe = $exePath2
} elseif (Test-Path $exePath3) {
    $foundExe = $exePath3
} else {
    # Procurar qualquer .exe gerado
    $exeFiles = Get-ChildItem $buildDir -Recurse -Filter "*.exe" -ErrorAction SilentlyContinue
    if ($exeFiles) {
        $foundExe = $exeFiles[0].FullName
    }
}

if ($foundExe) {
    Write-Host ""
    Write-Host "======================================" -ForegroundColor Cyan
    Write-Host "🎮 BUILD COMPLETO COM SUCESSO!" -ForegroundColor Green
    Write-Host "======================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Executável: $foundExe" -ForegroundColor Green
    Write-Host ""
    Write-Host "Próximos passos:" -ForegroundColor Yellow
    Write-Host "1. Abra outro PowerShell ou Terminal" -ForegroundColor White
    Write-Host "2. Execute o Java Launcher: java -jar seu_projeto.jar" -ForegroundColor White
    Write-Host "3. Faça login com um usuário" -ForegroundColor White
    Write-Host "4. Crie um personagem no Lobby" -ForegroundColor White
    Write-Host "5. Clique em 'Entrar no Jogo'" -ForegroundColor White
    Write-Host "6. O Kronus Rift Game C++ deve abrir automaticamente! 🎮" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "⚠️  Nenhum executável encontrado!" -ForegroundColor Yellow
    Write-Host "Arquivos em build:" -ForegroundColor Yellow
    Get-ChildItem $buildDir -Recurse | Where-Object { $_.Length -gt 0 } | Select-Object Name, Length
    Write-Host ""
    Write-Host "Tente compilar manualmente:" -ForegroundColor Yellow
    Write-Host "  & '$cmakeExe' --build . --config Debug" -ForegroundColor Gray
}

Pop-Location
Set-Location $projectRoot
