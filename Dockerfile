# Use OpenJDK 17 with Maven
FROM maven:3.9.4-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy the entire project
COPY . .

# Navigate to backend directory and build
WORKDIR /app/admin-panel/admin-backend/backend
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/admin-panel/admin-backend/backend/target/admin-panel-backend-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Set Spring profile for Railway
ENV SPRING_PROFILES_ACTIVE=railway

# Run the application
CMD ["java", "-Xmx512m", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]
