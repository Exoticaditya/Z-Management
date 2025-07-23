#!/bin/bash

# Z+ Admin Panel Deployment Script
# This script sets up and deploys the complete admin panel

set -e  # Exit on any error

echo "🚀 Starting Z+ Admin Panel Deployment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="zplus-admin-panel"
DOCKER_IMAGE_NAME="zplus-admin-panel"
DOCKER_TAG="latest"
CONTAINER_NAME="zplus-admin-panel"
NETWORK_NAME="zplus-network"
DB_CONTAINER_NAME="zplus-postgres"
DB_VOLUME_NAME="zplus-postgres-data"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    if ! command_exists docker; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command_exists docker-compose; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    if ! command_exists java; then
        print_warning "Java is not installed. Will use Docker for Java application."
    fi
    
    print_success "Prerequisites check completed"
}

# Create necessary directories
create_directories() {
    print_status "Creating necessary directories..."
    
    mkdir -p logs
    mkdir -p uploads
    mkdir -p config
    mkdir -p backups
    
    print_success "Directories created"
}

# Setup environment variables
setup_environment() {
    print_status "Setting up environment variables..."
    
    if [ ! -f .env ]; then
        cat > .env << EOF
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=zplus_admin_panel
DB_USERNAME=zplus_user
DB_PASSWORD=zplus_password

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
JWT_SECRET=ZplusAdminPanelSecretKeyForJWTTokenGeneration2024

# Email Configuration
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_FROM=your-email@gmail.com
ADMIN_EMAIL=adityamalik5763@gmail.com

# File Upload Configuration
UPLOAD_DIR=./uploads
FRONTEND_URL=http://localhost:3000

# Docker Configuration
DOCKER_IMAGE_NAME=$DOCKER_IMAGE_NAME
DOCKER_TAG=$DOCKER_TAG
CONTAINER_NAME=$CONTAINER_NAME
EOF
        print_success "Environment file created"
    else
        print_warning "Environment file already exists"
    fi
}

# Build the application
build_application() {
    print_status "Building the application..."
    
    if command_exists mvn; then
        print_status "Using Maven to build..."
        mvn clean package -DskipTests
        print_success "Application built successfully"
    else
        print_warning "Maven not found, will build using Docker"
    fi
}

# Build Docker image
build_docker_image() {
    print_status "Building Docker image..."
    
    docker build -t $DOCKER_IMAGE_NAME:$DOCKER_TAG .
    print_success "Docker image built successfully"
}

# Setup database
setup_database() {
    print_status "Setting up database..."
    
    # Create Docker network if it doesn't exist
    if ! docker network ls | grep -q $NETWORK_NAME; then
        docker network create $NETWORK_NAME
        print_success "Docker network created"
    fi
    
    # Start PostgreSQL container
    if ! docker ps -a | grep -q $DB_CONTAINER_NAME; then
        docker run -d \
            --name $DB_CONTAINER_NAME \
            --network $NETWORK_NAME \
            -e POSTGRES_DB=zplus_admin_panel \
            -e POSTGRES_USER=zplus_user \
            -e POSTGRES_PASSWORD=zplus_password \
            -v $DB_VOLUME_NAME:/var/lib/postgresql/data \
            -p 5432:5432 \
            postgres:15
        print_success "PostgreSQL container started"
        
        # Wait for database to be ready
        print_status "Waiting for database to be ready..."
        sleep 30
    else
        print_status "Starting existing PostgreSQL container..."
        docker start $DB_CONTAINER_NAME
    fi
    
    # Run database initialization scripts
    print_status "Running database initialization scripts..."
    
    # Wait for database to be fully ready
    until docker exec $DB_CONTAINER_NAME pg_isready -U zplus_user -d zplus_admin_panel; do
        print_status "Waiting for database connection..."
        sleep 5
    done
    
    # Run initialization scripts
    docker exec -i $DB_CONTAINER_NAME psql -U zplus_user -d zplus_admin_panel < init-scripts/01-init-database.sql
    docker exec -i $DB_CONTAINER_NAME psql -U zplus_user -d zplus_admin_panel < init-scripts/02-init-projects.sql
    
    print_success "Database setup completed"
}

# Start the application
start_application() {
    print_status "Starting the application..."
    
    # Stop existing container if running
    if docker ps | grep -q $CONTAINER_NAME; then
        print_status "Stopping existing container..."
        docker stop $CONTAINER_NAME
        docker rm $CONTAINER_NAME
    fi
    
    # Start the application container
    docker run -d \
        --name $CONTAINER_NAME \
        --network $NETWORK_NAME \
        -p 8080:8080 \
        -v $(pwd)/uploads:/app/uploads \
        -v $(pwd)/logs:/app/logs \
        --env-file .env \
        $DOCKER_IMAGE_NAME:$DOCKER_TAG
    
    print_success "Application started successfully"
}

# Health check
health_check() {
    print_status "Performing health check..."
    
    # Wait for application to start
    sleep 30
    
    # Check if application is responding
    if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
        print_success "Application is healthy and responding"
    else
        print_error "Application health check failed"
        print_status "Checking container logs..."
        docker logs $CONTAINER_NAME
        exit 1
    fi
}

# Setup monitoring
setup_monitoring() {
    print_status "Setting up monitoring..."
    
    # Create monitoring script
    cat > monitor.sh << 'EOF'
#!/bin/bash

# Simple monitoring script for Z+ Admin Panel

echo "=== Z+ Admin Panel Status ==="
echo "Timestamp: $(date)"

# Check if containers are running
echo -e "\n--- Container Status ---"
if docker ps | grep -q zplus-admin-panel; then
    echo "✅ Application container: RUNNING"
else
    echo "❌ Application container: STOPPED"
fi

if docker ps | grep -q zplus-postgres; then
    echo "✅ Database container: RUNNING"
else
    echo "❌ Database container: STOPPED"
fi

# Check application health
echo -e "\n--- Application Health ---"
if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "✅ Application: HEALTHY"
else
    echo "❌ Application: UNHEALTHY"
fi

# Check database connection
echo -e "\n--- Database Status ---"
if docker exec zplus-postgres pg_isready -U zplus_user -d zplus_admin_panel > /dev/null 2>&1; then
    echo "✅ Database: CONNECTED"
else
    echo "❌ Database: DISCONNECTED"
fi

# Resource usage
echo -e "\n--- Resource Usage ---"
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"
EOF
    
    chmod +x monitor.sh
    print_success "Monitoring script created"
}

# Create backup script
create_backup_script() {
    print_status "Creating backup script..."
    
    cat > backup.sh << 'EOF'
#!/bin/bash

# Backup script for Z+ Admin Panel

BACKUP_DIR="./backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_NAME="zplus_backup_$TIMESTAMP"

mkdir -p $BACKUP_DIR

echo "Creating backup: $BACKUP_NAME"

# Backup database
docker exec zplus-postgres pg_dump -U zplus_user zplus_admin_panel > $BACKUP_DIR/${BACKUP_NAME}_database.sql

# Backup uploads
tar -czf $BACKUP_DIR/${BACKUP_NAME}_uploads.tar.gz uploads/

# Backup logs
tar -czf $BACKUP_DIR/${BACKUP_NAME}_logs.tar.gz logs/

# Create backup manifest
cat > $BACKUP_DIR/${BACKUP_NAME}_manifest.txt << MANIFEST
Z+ Admin Panel Backup
Timestamp: $(date)
Backup Name: $BACKUP_NAME

Contents:
- Database: ${BACKUP_NAME}_database.sql
- Uploads: ${BACKUP_NAME}_uploads.tar.gz
- Logs: ${BACKUP_NAME}_logs.tar.gz

To restore:
1. Stop the application
2. Restore database: docker exec -i zplus-postgres psql -U zplus_user zplus_admin_panel < ${BACKUP_NAME}_database.sql
3. Restore uploads: tar -xzf ${BACKUP_NAME}_uploads.tar.gz
4. Restore logs: tar -xzf ${BACKUP_NAME}_logs.tar.gz
5. Start the application
MANIFEST

echo "Backup completed: $BACKUP_NAME"
echo "Backup location: $BACKUP_DIR"
EOF
    
    chmod +x backup.sh
    print_success "Backup script created"
}

# Main deployment function
deploy() {
    print_status "Starting deployment process..."
    
    check_prerequisites
    create_directories
    setup_environment
    build_application
    build_docker_image
    setup_database
    start_application
    health_check
    setup_monitoring
    create_backup_script
    
    print_success "🎉 Deployment completed successfully!"
    print_status "Application is running at: http://localhost:8080"
    print_status "API documentation: http://localhost:8080/swagger-ui/"
    print_status "Database console: http://localhost:8080/h2-console (dev only)"
    print_status "Use './monitor.sh' to check application status"
    print_status "Use './backup.sh' to create backups"
}

# Cleanup function
cleanup() {
    print_status "Cleaning up deployment..."
    
    docker stop $CONTAINER_NAME 2>/dev/null || true
    docker rm $CONTAINER_NAME 2>/dev/null || true
    docker stop $DB_CONTAINER_NAME 2>/dev/null || true
    docker rm $DB_CONTAINER_NAME 2>/dev/null || true
    docker network rm $NETWORK_NAME 2>/dev/null || true
    docker volume rm $DB_VOLUME_NAME 2>/dev/null || true
    
    print_success "Cleanup completed"
}

# Show usage
usage() {
    echo "Usage: $0 [deploy|cleanup|monitor|backup]"
    echo ""
    echo "Commands:"
    echo "  deploy   - Deploy the complete application"
    echo "  cleanup  - Remove all containers and volumes"
    echo "  monitor  - Show application status"
    echo "  backup   - Create backup of data"
    echo ""
}

# Main script logic
case "${1:-deploy}" in
    deploy)
        deploy
        ;;
    cleanup)
        cleanup
        ;;
    monitor)
        ./monitor.sh
        ;;
    backup)
        ./backup.sh
        ;;
    *)
        usage
        exit 1
        ;;
esac 