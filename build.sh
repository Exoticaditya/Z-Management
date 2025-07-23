#!/bin/bash

# Z+ Management Platform Build Script
echo "🚀 Starting Z+ Management Platform Build..."

# Set variables
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/admin-panel/admin-backend/backend"
BUILD_DIR="$PROJECT_DIR/build"
LOG_FILE="$PROJECT_DIR/build.log"

# Create build directory
mkdir -p "$BUILD_DIR"

# Function to log messages
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Function to handle errors
handle_error() {
    log "❌ Error: $1"
    exit 1
}

# Check prerequisites
log "🔍 Checking prerequisites..."

# Check Java
if ! command -v java &> /dev/null; then
    handle_error "Java is not installed or not in PATH"
fi

JAVA_VERSION=$(java -version 2>&1 | grep "version" | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    handle_error "Java 17 or higher is required. Found version: $JAVA_VERSION"
fi
log "✅ Java version: $(java -version 2>&1 | head -n1)"

# Check Maven
if ! command -v mvn &> /dev/null; then
    handle_error "Maven is not installed or not in PATH"
fi
log "✅ Maven version: $(mvn -version | head -n1)"

# Clean previous builds
log "🧹 Cleaning previous builds..."
rm -rf "$BUILD_DIR"/*
cd "$BACKEND_DIR" || handle_error "Backend directory not found"
./mvnw clean &>> "$LOG_FILE" || handle_error "Failed to clean project"

# Run tests
log "🧪 Running tests..."
./mvnw test &>> "$LOG_FILE" || handle_error "Tests failed"
log "✅ All tests passed"

# Build application
log "🔨 Building application..."
./mvnw package -DskipTests &>> "$LOG_FILE" || handle_error "Build failed"

# Copy artifacts
log "📦 Copying build artifacts..."
cp target/*.jar "$BUILD_DIR/" || handle_error "Failed to copy JAR file"
cp -r src/main/resources/static "$BUILD_DIR/" || handle_error "Failed to copy static files"

# Copy configuration files
log "⚙️ Copying configuration files..."
cp "$PROJECT_DIR/.env.template" "$BUILD_DIR/"
cp "$PROJECT_DIR/docker-compose.yml" "$BUILD_DIR/"
cp "$PROJECT_DIR/Dockerfile" "$BUILD_DIR/"

# Create deployment package
log "📦 Creating deployment package..."
cd "$BUILD_DIR" || handle_error "Build directory not found"
tar -czf "zplus-management-$(date +%Y%m%d-%H%M%S).tar.gz" * || handle_error "Failed to create deployment package"

# Generate build report
log "📊 Generating build report..."
{
    echo "Z+ Management Platform Build Report"
    echo "=================================="
    echo "Build Date: $(date)"
    echo "Java Version: $(java -version 2>&1 | head -n1)"
    echo "Maven Version: $(mvn -version | head -n1)"
    echo ""
    echo "Files Generated:"
    ls -la "$BUILD_DIR"
    echo ""
    echo "JAR File Info:"
    ls -la "$BUILD_DIR"/*.jar
} > "$BUILD_DIR/build-report.txt"

log "✅ Build completed successfully!"
log "📁 Build artifacts are in: $BUILD_DIR"
log "📄 Build log: $LOG_FILE"
log "🚀 Ready for deployment!"

echo ""
echo "🎉 Build Summary:"
echo "  ✅ Tests passed"
echo "  ✅ Application built"
echo "  ✅ Artifacts packaged"
echo "  ✅ Deployment ready"
echo ""
echo "Next steps:"
echo "  1. Review build artifacts in: $BUILD_DIR"
echo "  2. Configure environment variables"
echo "  3. Deploy using: ./deploy.sh"
