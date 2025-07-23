@echo off
echo 🚀 Building Z+ Admin Backend...

REM Clean previous build
echo 🧹 Cleaning previous build...
call mvn clean

REM Compile
echo 🔨 Compiling...
call mvn compile

REM Run tests
echo 🧪 Running tests...
call mvn test

REM Package
echo 📦 Packaging...
call mvn package -DskipTests

echo ✅ Build completed successfully!
echo 📁 JAR file location: target/admin-panel-backend-1.0.0.jar
pause 