#!/bin/bash

# Z+ Management Platform Deployment Script
echo "🚀 Starting Z+ Management Platform Deployment..."

# Set variables
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_DIR="/opt/zplus"
SERVICE_NAME="zplus-management"
LOG_FILE="$PROJECT_DIR/deploy.log"

# Function to log messages
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Function to handle errors
handle_error() {
    log "❌ Error: $1"
    exit 1
}

# Check if running as root
if [[ $EUID -eq 0 ]]; then
    log "⚠️ Running as root user"
else
    log "ℹ️ Running as regular user"
fi

# Check deployment method
if [ "$1" = "docker" ]; then
    DEPLOY_METHOD="docker"
    log "🐳 Using Docker deployment"
elif [ "$1" = "standalone" ]; then
    DEPLOY_METHOD="standalone"
    log "🔧 Using standalone deployment"
else
    echo "Usage: $0 [docker|standalone]"
    echo ""
    echo "Deployment methods:"
    echo "  docker     - Deploy using Docker Compose"
    echo "  standalone - Deploy as standalone JAR"
    exit 1
fi

# Function to deploy with Docker
deploy_docker() {
    log "🐳 Starting Docker deployment..."
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        handle_error "Docker is not installed"
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        handle_error "Docker Compose is not installed"
    fi
    
    log "✅ Docker and Docker Compose are available"
    
    # Stop existing containers
    log "🛑 Stopping existing containers..."
    docker-compose down &>> "$LOG_FILE" || log "ℹ️ No existing containers to stop"
    
    # Build and start services
    log "🔨 Building and starting services..."
    docker-compose up --build -d &>> "$LOG_FILE" || handle_error "Failed to start Docker services"
    
    # Wait for services to be ready
    log "⏳ Waiting for services to be ready..."
    sleep 30
    
    # Check service health
    log "🏥 Checking service health..."
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        log "✅ Application is healthy"
    else
        handle_error "Application health check failed"
    fi
    
    log "✅ Docker deployment completed successfully!"
}

# Function to deploy standalone
deploy_standalone() {
    log "🔧 Starting standalone deployment..."
    
    # Check Java
    if ! command -v java &> /dev/null; then
        handle_error "Java is not installed"
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | grep "version" | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        handle_error "Java 17 or higher is required"
    fi
    
    log "✅ Java version check passed"
    
    # Create deployment directory
    sudo mkdir -p "$DEPLOY_DIR" &>> "$LOG_FILE" || handle_error "Failed to create deployment directory"
    sudo chown $USER:$USER "$DEPLOY_DIR" &>> "$LOG_FILE" || log "ℹ️ Could not change ownership (may need root)"
    
    # Copy application files
    log "📦 Copying application files..."
    cp build/*.jar "$DEPLOY_DIR/" &>> "$LOG_FILE" || handle_error "Failed to copy JAR file"
    cp .env.template "$DEPLOY_DIR/.env" &>> "$LOG_FILE" || handle_error "Failed to copy environment template"
    
    # Create systemd service
    log "⚙️ Creating systemd service..."
    sudo tee /etc/systemd/system/${SERVICE_NAME}.service > /dev/null << EOF
[Unit]
Description=Z+ Management Platform
After=network.target

[Service]
Type=simple
User=$USER
WorkingDirectory=$DEPLOY_DIR
Environment=SPRING_PROFILES_ACTIVE=prod
EnvironmentFile=$DEPLOY_DIR/.env
ExecStart=/usr/bin/java -jar $DEPLOY_DIR/admin-panel-backend-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

    # Reload systemd and start service
    log "🔄 Reloading systemd and starting service..."
    sudo systemctl daemon-reload &>> "$LOG_FILE" || handle_error "Failed to reload systemd"
    sudo systemctl enable ${SERVICE_NAME} &>> "$LOG_FILE" || handle_error "Failed to enable service"
    sudo systemctl start ${SERVICE_NAME} &>> "$LOG_FILE" || handle_error "Failed to start service"
    
    # Wait for service to be ready
    log "⏳ Waiting for service to be ready..."
    sleep 30
    
    # Check service status
    if sudo systemctl is-active --quiet ${SERVICE_NAME}; then
        log "✅ Service is active and running"
    else
        handle_error "Service failed to start"
    fi
    
    # Check application health
    log "🏥 Checking application health..."
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        log "✅ Application is healthy"
    else
        handle_error "Application health check failed"
    fi
    
    log "✅ Standalone deployment completed successfully!"
}

# Pre-deployment checks
log "🔍 Running pre-deployment checks..."

# Check if build directory exists
if [ ! -d "build" ]; then
    handle_error "Build directory not found. Please run ./build.sh first"
fi

# Check if JAR file exists
if [ ! -f build/*.jar ]; then
    handle_error "JAR file not found in build directory"
fi

log "✅ Pre-deployment checks passed"

# Execute deployment based on method
case $DEPLOY_METHOD in
    docker)
        deploy_docker
        ;;
    standalone)
        deploy_standalone
        ;;
esac

# Post-deployment information
log "📋 Post-deployment information:"
log "  Application URL: http://localhost:8080"
log "  Admin Panel: http://localhost:8080/admin/"
log "  Employee Dashboard: http://localhost:8080/employee-dashboard/"
log "  Client Portal: http://localhost:8080/client/"
log "  API Documentation: http://localhost:8080/swagger-ui/"
log "  Health Check: http://localhost:8080/actuator/health"

echo ""
echo "🎉 Deployment Summary:"
echo "  ✅ Application deployed successfully"
echo "  ✅ Services are running"
echo "  ✅ Health checks passed"
echo ""
echo "Access your application at: http://localhost:8080"
echo "Check logs with: $0 logs"
