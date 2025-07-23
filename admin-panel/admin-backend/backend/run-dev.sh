#!/bin/bash

echo "🚀 Starting Z+ Admin Backend in Development Mode..."

# Set environment variables
export SPRING_PROFILES_ACTIVE=dev

# Run the application
echo "🌱 Starting with H2 database..."
mvn spring-boot:run -Dspring-boot.run.profiles=dev

echo "✅ Application stopped." 