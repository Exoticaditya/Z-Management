@echo off
echo ========================================
echo Z+ Admin Panel Database Setup Script
echo ========================================
echo.

REM Check if PostgreSQL is installed
echo Checking PostgreSQL installation...
psql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: PostgreSQL is not installed or not in PATH
    echo Please install PostgreSQL from: https://www.postgresql.org/download/
    pause
    exit /b 1
)

echo PostgreSQL found!
echo.

REM Get PostgreSQL connection details
set /p PG_HOST="Enter PostgreSQL host (default: localhost): "
if "%PG_HOST%"=="" set PG_HOST=localhost

set /p PG_PORT="Enter PostgreSQL port (default: 5432): "
if "%PG_PORT%"=="" set PG_PORT=5432

set /p PG_USER="Enter PostgreSQL superuser (default: postgres): "
if "%PG_USER%"=="" set PG_USER=postgres

echo.
echo Connecting to PostgreSQL as %PG_USER%...

REM Test connection
psql -h %PG_HOST% -p %PG_PORT% -U %PG_USER% -c "SELECT version();" >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Cannot connect to PostgreSQL
    echo Please check your connection details and try again
    pause
    exit /b 1
)

echo Connection successful!
echo.

REM Run the database setup script
echo Creating database and tables...
psql -h %PG_HOST% -p %PG_PORT% -U %PG_USER% -f setup-database.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo Database setup completed successfully!
    echo ========================================
    echo.
    echo Database Details:
    echo - Database Name: zplus_admin_panel
    echo - Database User: zplus_user
    echo - Database Password: zplus_password
    echo.
    echo Default Admin Account:
    echo - Email: admin@zplus.com
    echo - Password: admin123
    echo.
    echo Next Steps:
    echo 1. Update your application.yml with the database credentials
    echo 2. Run your Spring Boot application
    echo 3. Access the admin panel at: http://localhost:8080/api
    echo.
) else (
    echo.
    echo ERROR: Database setup failed!
    echo Please check the error messages above and try again.
    echo.
)

pause 