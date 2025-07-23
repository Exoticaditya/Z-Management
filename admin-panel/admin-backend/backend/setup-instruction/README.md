# Z+ Admin Panel Backend

A comprehensive admin panel backend built with Spring Boot for managing user registrations, approvals, and system administration.

## 🚀 Features

- **User Registration & Management**: Complete user registration workflow with approval system
- **File Upload**: Profile photo upload and management
- **Email Notifications**: Automated email notifications for registration events
- **Admin Dashboard**: Comprehensive admin interface for user management
- **Security**: JWT-based authentication and authorization
- **Database Support**: PostgreSQL (production) and H2 (development)
- **API Documentation**: Swagger/OpenAPI documentation
- **Docker Support**: Containerized deployment

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher (for production)
- Git

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd admin-backend/backend
```

### 2. Database Setup

#### For Development (H2 - In-Memory)
No setup required. H2 database will be created automatically.

#### For Production (PostgreSQL)
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database and user
CREATE DATABASE zplus_admin_panel;
CREATE USER zplus_user WITH PASSWORD 'zplus_password';
GRANT ALL PRIVILEGES ON DATABASE zplus_admin_panel TO zplus_user;
\q
```

### 3. Environment Configuration

Create environment variables or use the provided configuration:

```bash
# Database Configuration
export DB_USERNAME=zplus_user
export DB_PASSWORD=zplus_password
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=zplus_admin_panel

# Email Configuration
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
export MAIL_FROM=your-email@gmail.com
export ADMIN_EMAIL=adityamalik5763@gmail.com

# JWT Configuration
export JWT_SECRET=your-secret-key

# Application Configuration
export UPLOAD_DIR=./uploads
export FRONTEND_URL=http://localhost:3000
export PORT=8080
```

### 4. Build the Application

#### Using Maven
```bash
mvn clean install
```

#### Using Build Scripts
```bash
# Linux/Mac
chmod +x build.sh
./build.sh

# Windows
build.bat
```

### 5. Run the Application

#### Development Mode (H2 Database)
```bash
# Linux/Mac
chmod +x run-dev.sh
./run-dev.sh

# Windows
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Production Mode (PostgreSQL)
```bash
# Linux/Mac
chmod +x run-prod.sh
./run-prod.sh

# Windows
java -jar target/admin-panel-backend-1.0.0.jar --spring.profiles.active=prod
```

#### Using Docker
```bash
# Build and run with Docker Compose
docker-compose up --build

# Or build and run individually
docker build -t zplus-admin-backend .
docker run -p 8080:8080 zplus-admin-backend
```

## 🌐 API Endpoints

### Base URL: `http://localhost:8080/api`

#### User Management
- `POST /users/register` - Register new user
- `GET /users` - Get all users (with pagination)
- `GET /users/{userId}` - Get user by ID
- `GET /users/email/{email}` - Get user by email
- `GET /users/pending` - Get pending registrations
- `PUT /users/{userId}` - Update user profile
- `DELETE /users/{userId}` - Delete user
- `POST /users/{userId}/profile-photo` - Upload profile photo
- `GET /users/stats` - Get registration statistics

#### Health & Info
- `GET /health` - Application health check
- `GET /info` - Application information

## 📊 Database Schema

### Users Table
- `id` - Primary key
- `first_name` - User's first name
- `last_name` - User's last name
- `email` - Unique email address
- `phone` - Phone number
- `department` - Department name
- `user_type` - User type (ADMIN, EMPLOYEE, CLIENT)
- `project_id` - Project identifier
- `self_id` - Self-generated ID
- `password_hash` - Encrypted password
- `profile_photo_path` - Profile photo file path
- `reason` - Registration reason
- `supervisor` - Supervisor name
- `status` - Registration status (PENDING, APPROVED, REJECTED)
- `approved_by` - Admin who approved
- `approved_at` - Approval timestamp
- `rejection_reason` - Reason for rejection
- `reference_id` - Unique reference ID
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp
- `last_login` - Last login timestamp
- `is_active` - Account active status

## 🔧 Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
# Database
spring.datasource.url: jdbc:postgresql://localhost:5432/zplus_admin_panel
spring.datasource.username: zplus_user
spring.datasource.password: zplus_password

# JPA
spring.jpa.hibernate.ddl-auto: update
spring.jpa.show-sql: true

# File Upload
spring.servlet.multipart.max-file-size: 10MB
spring.servlet.multipart.max-request-size: 10MB

# JWT
jwt.secret: your-secret-key
jwt.expiration: 86400000

# Email
spring.mail.host: smtp.gmail.com
spring.mail.port: 587
spring.mail.username: your-email@gmail.com
spring.mail.password: your-app-password
```

### Profiles

- **dev**: Uses H2 in-memory database, detailed logging
- **prod**: Uses PostgreSQL, minimal logging

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
```bash
mvn jacoco:report
```

## 📝 Logging

Logs are written to `logs/application.log` with the following levels:
- **DEBUG**: Detailed application logs
- **INFO**: General application information
- **WARN**: Warning messages
- **ERROR**: Error messages

## 🔒 Security

- **CORS**: Configured for cross-origin requests
- **CSRF**: Disabled for API endpoints
- **Password Encryption**: BCrypt password hashing
- **JWT**: Stateless authentication
- **Input Validation**: Comprehensive validation using Bean Validation

## 📧 Email Templates

Email templates are located in `src/main/resources/templates/email/`:
- `registration-confirmation.ftl` - Registration confirmation email
- `registration-approved.ftl` - Approval notification email
- `registration-rejected.ftl` - Rejection notification email

## 🐳 Docker

### Build Image
```bash
docker build -t zplus-admin-backend .
```

### Run Container
```bash
docker run -p 8080:8080 \
  -e DB_HOST=your-db-host \
  -e DB_USERNAME=your-db-user \
  -e DB_PASSWORD=your-db-password \
  zplus-admin-backend
```

### Docker Compose
```bash
docker-compose up -d
```

## 📚 API Documentation

Once the application is running, access the API documentation at:
- Swagger UI: `http://localhost:8080/api