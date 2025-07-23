#!/bin/bash

echo "========================================"
echo "Z+ Admin Panel Database Setup Script"
echo "========================================"
echo

# Check if PostgreSQL is installed
echo "Checking PostgreSQL installation..."
if ! command -v psql &> /dev/null; then
    echo "ERROR: PostgreSQL is not installed or not in PATH"
    echo "Please install PostgreSQL:"
    echo "  Ubuntu/Debian: sudo apt-get install postgresql postgresql-contrib"
    echo "  CentOS/RHEL: sudo yum install postgresql postgresql-server"
    echo "  macOS: brew install postgresql"
    exit 1
fi

echo "PostgreSQL found!"
echo

# Get PostgreSQL connection details
read -p "Enter PostgreSQL host (default: localhost): " PG_HOST
PG_HOST=${PG_HOST:-localhost}

read -p "Enter PostgreSQL port (default: 5432): " PG_PORT
PG_PORT=${PG_PORT:-5432}

read -p "Enter PostgreSQL superuser (default: postgres): " PG_USER
PG_USER=${PG_USER:-postgres}

echo
echo "Connecting to PostgreSQL as $PG_USER..."

# Test connection
if ! psql -h "$PG_HOST" -p "$PG_PORT" -U "$PG_USER" -c "SELECT version();" > /dev/null 2>&1; then
    echo "ERROR: Cannot connect to PostgreSQL"
    echo "Please check your connection details and try again"
    exit 1
fi

echo "Connection successful!"
echo

# Run the database setup script
echo "Creating database and tables..."
if psql -h "$PG_HOST" -p "$PG_PORT" -U "$PG_USER" -f setup-database.sql; then
    echo
    echo "========================================"
    echo "Database setup completed successfully!"
    echo "========================================"
    echo
    echo "Database Details:"
    echo "- Database Name: zplus_admin_panel"
    echo "- Database User: zplus_user"
    echo "- Database Password: zplus_password"
    echo
    echo "Default Admin Account:"
echo "- Email: adityamalik5763@gmail.com"
    echo "- Password: admin123"
    echo
    echo "Next Steps:"
    echo "1. Update your application.yml with the database credentials"
    echo "2. Run your Spring Boot application"
    echo "3. Access the admin panel at: http://localhost:8080/api"
    echo
else
    echo
    echo "ERROR: Database setup failed!"
    echo "Please check the error messages above and try again."
    echo
    exit 1
fi 