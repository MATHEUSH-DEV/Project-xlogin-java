@echo off
REM Script para baixar JARs necessarios para MySQL
REM Execute este script na pasta do projeto para instalar as dependencias

echo.
echo ========================================
echo Baixando JARs para MySQL e HikariCP...
echo ========================================
echo.

cd lib

REM MySQL Connector
echo [1/2] Baixando mysql-connector-java-8.1.0.jar...
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.1.0/mysql-connector-java-8.1.0.jar', 'mysql-connector-java-8.1.0.jar')"

REM HikariCP
echo [2/2] Baixando HikariCP-5.0.1.jar...
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://repo1.maven.org/maven2/com/zaxxer/HikariCP/5.0.1/HikariCP-5.0.1.jar', 'HikariCP-5.0.1.jar')"

echo.
echo ========================================
echo Verificando JARs baixados...
echo ========================================
ls *.jar

echo.
echo ✓ Dependencias instaladas com sucesso!
pause
