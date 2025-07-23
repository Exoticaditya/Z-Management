#!/bin/bash

echo "🚀 Starting Z+ Admin Backend in Production Mode..."

# Check if PostgreSQL is running
echo "🔍 Checking PostgreSQL connection..."
if ! pg_isready -h localhost -p 5432 -U zplus_user; then
    echo "❌ PostgreSQL is not running or not accessible"
    echo "Please start PostgreSQL and ensure the database is created"
    exit 1
fi

# Set environment variables
export SPRING_PROFILES_ACTIVE=prod

# Run the application
echo "🐘 Starting with PostgreSQL database..."
java -jar target/admin-panel-backend-1.0.0.jar --spring.profiles.active=prod

echo "✅ Application stopped." 