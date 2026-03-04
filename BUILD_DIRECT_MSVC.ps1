# ============================================
# DIRECT MSVC BUILD SCRIPT
# Bypass CMake issues and compile directly
# ============================================

Write-Host "=================================" -ForegroundColor Cyan
Write-Host "Kronus Rift - Direct MSVC Build" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

# Find Visual Studio
Write-Host "[1/2] Procurando Visual Studio..." -ForegroundColor Yellow

$vsPath = $null
$possibleVS = @(
    "C:\Program Files\Microsoft Visual Studio\2022\*\VC\Auxiliary\Build\vcvars64.bat",
    "C:\Program Files (x86)\Microsoft Visual Studio\2019\*\VC\Auxiliary\Build\vcvars64.bat",
    "C:\Program Files\Microsoft Visual Studio\2022\*\VC\Auxiliary\Build\vcvarsall.bat"
)

$vcvarsFile = $null
foreach ($pattern in $possibleVS) {
    $matches = Get-Item $pattern -ErrorAction SilentlyContinue
    if ($matches) {
        $vcvarsFile = $matches[0].FullName
        break
    }
}

if (-not $vcvarsFile) {
    Write-Host "❌ Visual Studio não encontrado!" -ForegroundColor Red
    Write-Host "Instale Visual Studio Community com suporte a C++" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Encontrado: $vcvarsFile" -ForegroundColor Green

Write-Host ""
Write-Host "[2/2] Compilando com MSVC..." -ForegroundColor Yellow
Write-Host ""

# Para esta abordagem, vamos criar um build simples
$projectRoot = "c:\Users\08422402173\Documents\xlogin-project-java\Project-xlogin-java"
$buildDir = Join-Path $projectRoot "c_game\build_msvc"

if (Test-Path $buildDir) {
    Remove-Item $buildDir -Recurse -Force
}

New-Item -ItemType Directory -Path $buildDir | Out-Null

# Usar CMake com geradores específicos
cd $buildDir

$cmakeExe = "c:\Users\08422402173\Documents\xlogin-project-java\Project-xlogin-java\tools\cmake-3.28.3-windows-x86_64\bin\cmake.exe"

Write-Host "Configurando CMake com Visual Studio 2022..." -ForegroundColor Yellow

& $cmakeExe -G "Visual Studio 17 2022" -A x64 "..\\"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ CMake configurado!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Compilando solução..." -ForegroundColor Yellow
    
    # Compilar usando MSBuild direto
    $msbuildPath = "C:\Program Files\Microsoft Visual Studio\2022\Community\MSBuild\Current\Bin\MSBuild.exe"
    
    if (Test-Path $msbuildPath) {
        & $msbuildPath "KronusRiftGame.sln" /p:Configuration=Release /m:4
    } else {
        & $cmakeExe --build . --config Release --parallel 4
    }
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "=================================" -ForegroundColor Cyan
        Write-Host "✅ COMPILAÇÃO CONCLUÍDA!" -ForegroundColor Green
        Write-Host "=================================" -ForegroundColor Cyan
        
        $exePath = Join-Path $buildDir "Release\KronusRiftGame.exe"
        if (Test-Path $exePath) {
            Write-Host "Executável: $exePath" -ForegroundColor Green
        }
    } else {
        Write-Host "⚠️  Compilação teve erros" -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ Erro ao configurar CMake" -ForegroundColor Red
}

cd $projectRoot
