# Use Eclipse Temurin (more reliable than openjdk)
FROM eclipse-temurin:17-jdk-alpine AS builder

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy Maven files first (for better caching)
COPY admin-panel/admin-backend/backend/pom.xml admin-panel/admin-backend/backend/
COPY admin-panel/admin-backend/backend/src admin-panel/admin-backend/backend/src/

# Build the application
WORKDIR /app/admin-panel/admin-backend/backend
RUN mvn clean package -DskipTests -q

# Runtime stage - use lighter image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY --from=builder /app/admin-panel/admin-backend/backend/target/admin-panel-backend-1.0.0.jar app.jar

# Create non-root user for security
RUN addgroup -g 1001 -S spring && \
    adduser -S spring -u 1001 -G spring

USER spring

# Expose port
EXPOSE 8080

# Set Spring profile for Railway
ENV SPRING_PROFILES_ACTIVE=railway

# Run the application with Railway port configuration
CMD ["java", "-Xmx512m", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]
