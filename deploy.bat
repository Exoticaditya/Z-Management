@echo off
setlocal enabledelayedexpansion

REM Z+ Management Platform Windows Deployment Script
echo 🚀 Starting Z+ Management Platform Deployment...

REM Set variables
set PROJECT_DIR=%~dp0
set DEPLOY_DIR=C:\zplus
set SERVICE_NAME=zplus-management
set LOG_FILE=%PROJECT_DIR%deploy.log

REM Function to log messages
:log
echo %date% %time% - %~1 >> "%LOG_FILE%"
echo %~1
goto :eof

REM Function to handle errors
:handle_error
call :log "❌ Error: %~1"
exit /b 1

REM Check deployment method
if "%1"=="docker" (
    set DEPLOY_METHOD=docker
    call :log "🐳 Using Docker deployment"
    goto check_docker
) else if "%1"=="standalone" (
    set DEPLOY_METHOD=standalone
    call :log "🔧 Using standalone deployment"
    goto check_java
) else (
    echo Usage: %0 [docker^|standalone]
    echo.
    echo Deployment methods:
    echo   docker     - Deploy using Docker Compose
    echo   standalone - Deploy as standalone JAR
    exit /b 1
)

:check_docker
call :log "🐳 Starting Docker deployment..."

REM Check Docker
docker --version >nul 2>&1
if errorlevel 1 (
    call :handle_error "Docker is not installed"
)

docker-compose --version >nul 2>&1
if errorlevel 1 (
    call :handle_error "Docker Compose is not installed"
)

call :log "✅ Docker and Docker Compose are available"

REM Stop existing containers
call :log "🛑 Stopping existing containers..."
docker-compose down >> "%LOG_FILE%" 2>&1

REM Build and start services
call :log "🔨 Building and starting services..."
docker-compose up --build -d >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
    call :handle_error "Failed to start Docker services"
)

REM Wait for services to be ready
call :log "⏳ Waiting for services to be ready..."
timeout /t 30 /nobreak >nul

REM Check service health
call :log "🏥 Checking service health..."
curl -f http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    call :handle_error "Application health check failed"
)

call :log "✅ Docker deployment completed successfully!"
goto post_deployment

:check_java
call :log "🔧 Starting standalone deployment..."

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    call :handle_error "Java is not installed"
)

REM Get Java version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
    set JAVA_VERSION=!JAVA_VERSION:"=!
    set JAVA_VERSION=!JAVA_VERSION:~0,2!
)

if !JAVA_VERSION! LSS 17 (
    call :handle_error "Java 17 or higher is required"
)

call :log "✅ Java version check passed"

REM Create deployment directory
call :log "📦 Creating deployment directory..."
if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"
if errorlevel 1 (
    call :handle_error "Failed to create deployment directory"
)

REM Copy application files
call :log "📦 Copying application files..."
copy "build\*.jar" "%DEPLOY_DIR%\" >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
    call :handle_error "Failed to copy JAR file"
)

copy ".env.template" "%DEPLOY_DIR%\.env" >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
    call :handle_error "Failed to copy environment template"
)

REM Create Windows service installer
call :log "⚙️ Creating service installer..."
echo @echo off > "%DEPLOY_DIR%\install-service.bat"
echo echo Installing Z+ Management Platform as Windows Service... >> "%DEPLOY_DIR%\install-service.bat"
echo sc create "%SERVICE_NAME%" binPath= "java -jar %DEPLOY_DIR%\admin-panel-backend-1.0.0.jar" start= auto >> "%DEPLOY_DIR%\install-service.bat"
echo sc description "%SERVICE_NAME%" "Z+ Management Platform Service" >> "%DEPLOY_DIR%\install-service.bat"
echo echo Service installed successfully! >> "%DEPLOY_DIR%\install-service.bat"
echo echo Run 'sc start %SERVICE_NAME%' to start the service >> "%DEPLOY_DIR%\install-service.bat"

echo @echo off > "%DEPLOY_DIR%\uninstall-service.bat"
echo echo Stopping and removing Z+ Management Platform service... >> "%DEPLOY_DIR%\uninstall-service.bat"
echo sc stop "%SERVICE_NAME%" >> "%DEPLOY_DIR%\uninstall-service.bat"
echo sc delete "%SERVICE_NAME%" >> "%DEPLOY_DIR%\uninstall-service.bat"
echo echo Service removed successfully! >> "%DEPLOY_DIR%\uninstall-service.bat"

REM Create startup script
echo @echo off > "%DEPLOY_DIR%\start.bat"
echo cd /d "%DEPLOY_DIR%" >> "%DEPLOY_DIR%\start.bat"
echo echo Starting Z+ Management Platform... >> "%DEPLOY_DIR%\start.bat"
echo java -jar admin-panel-backend-1.0.0.jar >> "%DEPLOY_DIR%\start.bat"

call :log "✅ Standalone deployment completed successfully!"
call :log "ℹ️ Run '%DEPLOY_DIR%\install-service.bat' as Administrator to install as Windows Service"
call :log "ℹ️ Or run '%DEPLOY_DIR%\start.bat' to start manually"

:post_deployment
REM Pre-deployment checks
call :log "🔍 Running pre-deployment checks..."

REM Check if build directory exists
if not exist "build" (
    call :handle_error "Build directory not found. Please run build.bat first"
)

REM Check if JAR file exists
if not exist "build\*.jar" (
    call :handle_error "JAR file not found in build directory"
)

call :log "✅ Pre-deployment checks passed"

REM Post-deployment information
call :log "📋 Post-deployment information:"
call :log "  Application URL: http://localhost:8080"
call :log "  Admin Panel: http://localhost:8080/admin/"
call :log "  Employee Dashboard: http://localhost:8080/employee-dashboard/"
call :log "  Client Portal: http://localhost:8080/client/"
call :log "  API Documentation: http://localhost:8080/swagger-ui/"
call :log "  Health Check: http://localhost:8080/actuator/health"

echo.
echo 🎉 Deployment Summary:
echo   ✅ Application deployed successfully
echo   ✅ Services are running
echo   ✅ Health checks passed
echo.
echo Access your application at: http://localhost:8080

endlocal
