# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy Maven wrapper and pom.xml first for better caching
COPY admin-panel/admin-backend/backend/mvnw .
COPY admin-panel/admin-backend/backend/.mvn .mvn
COPY admin-panel/admin-backend/backend/pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY admin-panel/admin-backend/backend/src src

# Copy static assets
COPY *.html ./src/main/resources/static/
COPY style.css ./src/main/resources/static/
COPY asset ./src/main/resources/static/asset
COPY employee-dashboard ./src/main/resources/static/employee-dashboard

# Build the application
RUN ./mvnw clean package -DskipTests

# Create uploads directory
RUN mkdir -p uploads

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "target/admin-panel-backend-1.0.0.jar"]
