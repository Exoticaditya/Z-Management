# Deployment Guide

## Prerequisites
- JDK 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher
- Minimum 2GB RAM
- 10GB disk space

## Environment Setup
1. Create a `.env` file based on `.env.template`
2. Configure all required environment variables
3. Ensure PostgreSQL is running and database is created

## Build Process
```bash
# Clean and package
mvn clean package -DskipTests

# Run tests
mvn test

# Create production build
mvn package -Pprod
```

## Deployment Steps
1. Set up environment variables
2. Create necessary directories:
   ```bash
   mkdir -p /path/to/uploads
   mkdir -p /path/to/logs
   ```
3. Deploy the JAR file:
   ```bash
   java -jar -Dspring.profiles.active=prod app.jar
   ```

## Health Checks
- API Health: `GET /actuator/health`
- Database Health: `GET /actuator/health/db`
- Disk Space: `GET /actuator/health/disk`

## Monitoring
- Metrics available at: `/actuator/metrics`
- Prometheus endpoint: `/actuator/prometheus`

## Backup
1. Database backup:
   ```bash
   pg_dump -U $DB_USERNAME -h $DB_HOST $DB_NAME > backup.sql
   ```
2. File backup:
   ```bash
   tar -czf uploads.tar.gz /path/to/uploads
   ```

## Troubleshooting
1. Check logs at: `/path/to/logs/zplus-admin-panel.log`
2. Monitor memory usage and CPU
3. Check disk space regularly
4. Monitor database connections

## Security Notes
- Change default credentials immediately
- Rotate JWT secret periodically
- Keep dependencies updated
- Monitor for suspicious activities
- Regular security audits recommended
