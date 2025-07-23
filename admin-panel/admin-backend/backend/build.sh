#!/bin/bash

echo "🚀 Building Z+ Admin Backend..."

# Clean previous build
echo "🧹 Cleaning previous build..."
mvn clean

# Compile
echo "🔨 Compiling..."
mvn compile

# Run tests
echo "🧪 Running tests..."
mvn test

# Package
echo "📦 Packaging..."
mvn package -DskipTests

echo "✅ Build completed successfully!"
echo "📁 JAR file location: target/admin-panel-backend-1.0.0.jar" 