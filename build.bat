@echo off
REM Z+ Management Platform Build Script for Windows

echo 🚀 Starting Z+ Management Platform Build...

REM Set variables
set PROJECT_DIR=%~dp0
set BACKEND_DIR=%PROJECT_DIR%admin-panel\admin-backend\backend
set BUILD_DIR=%PROJECT_DIR%build
set LOG_FILE=%PROJECT_DIR%build.log

REM Create build directory
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"

REM Function to log messages
echo %date% %time% - Starting build process > "%LOG_FILE%"

REM Check prerequisites
echo 🔍 Checking prerequisites...

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Java is not installed or not in PATH
    exit /b 1
)
echo ✅ Java is available
echo %date% %time% - Java check passed >> "%LOG_FILE%"

REM Check Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Maven is not installed or not in PATH
    exit /b 1
)
echo ✅ Maven is available
echo %date% %time% - Maven check passed >> "%LOG_FILE%"

REM Clean previous builds
echo 🧹 Cleaning previous builds...
if exist "%BUILD_DIR%\*" del /q "%BUILD_DIR%\*"
cd /d "%BACKEND_DIR%"
call mvnw.cmd clean >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
    echo ❌ Error: Failed to clean project
    exit /b 1
)

REM Run tests
echo 🧪 Running tests...
call mvnw.cmd test >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
    echo ❌ Error: Tests failed
    exit /b 1
)
echo ✅ All tests passed

REM Build application
echo 🔨 Building application...
call mvnw.cmd package -DskipTests >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
    echo ❌ Error: Build failed
    exit /b 1
)

REM Copy artifacts
echo 📦 Copying build artifacts...
copy target\*.jar "%BUILD_DIR%\" >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
    echo ❌ Error: Failed to copy JAR file
    exit /b 1
)

REM Copy configuration files
echo ⚙️ Copying configuration files...
copy "%PROJECT_DIR%.env.template" "%BUILD_DIR%\" >> "%LOG_FILE%" 2>&1
copy "%PROJECT_DIR%docker-compose.yml" "%BUILD_DIR%\" >> "%LOG_FILE%" 2>&1
copy "%PROJECT_DIR%Dockerfile" "%BUILD_DIR%\" >> "%LOG_FILE%" 2>&1

REM Generate build report
echo 📊 Generating build report...
(
    echo Z+ Management Platform Build Report
    echo ==================================
    echo Build Date: %date% %time%
    echo.
    echo Files Generated:
    dir "%BUILD_DIR%"
    echo.
    echo JAR File Info:
    dir "%BUILD_DIR%\*.jar"
) > "%BUILD_DIR%\build-report.txt"

echo ✅ Build completed successfully!
echo 📁 Build artifacts are in: %BUILD_DIR%
echo 📄 Build log: %LOG_FILE%
echo 🚀 Ready for deployment!

echo.
echo 🎉 Build Summary:
echo   ✅ Tests passed
echo   ✅ Application built
echo   ✅ Artifacts packaged
echo   ✅ Deployment ready
echo.
echo Next steps:
echo   1. Review build artifacts in: %BUILD_DIR%
echo   2. Configure environment variables
echo   3. Deploy using Docker or standalone

pause
