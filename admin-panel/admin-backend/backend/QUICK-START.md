# Z+ Admin Panel - Quick Start Guide

Get your Z+ Admin Panel up and running in minutes!

## 🚀 Quick Setup (Choose One Method)

### Method 1: Docker Compose (Easiest)

```bash
# Navigate to backend directory
cd admin-panel/admin-backend/backend

# Start PostgreSQL database
docker-compose up -d postgres

# Wait 30 seconds for database to initialize, then start the application
docker-compose up -d admin-backend
```

**Access Points:**
- Application: http://localhost:8080/api
- Database: localhost:5432
- pgAdmin: http://localhost:5050 (adityamalik5763@gmail.com / admin123)

### Method 2: Local PostgreSQL

#### Windows
```bash
cd admin-panel/admin-backend/backend
setup-database.bat
```

#### Linux/macOS
```bash
cd admin-panel/admin-backend/backend
chmod +x setup-database.sh
./setup-database.sh
```

Then start the application:
```bash
mvn spring-boot:run
```

### Method 3: Manual Setup

1. **Install PostgreSQL** (if not already installed)
2. **Create database:**
   ```bash
   psql -U postgres
   \i setup-database.sql
   ```
3. **Start application:**
   ```bash
   mvn spring-boot:run
   ```

## 🔑 Default Login

- **Email:** adityamalik5763@gmail.com
- **Password:** admin123

## 📋 What's Included

✅ **Complete Database Setup**
- PostgreSQL database with all tables
- User management system
- Registration workflow
- Email notifications

✅ **Spring Boot Backend**
- RESTful API endpoints
- JWT authentication
- File upload handling
- Email service integration

✅ **Admin Dashboard**
- User management interface
- Registration approvals
- System statistics
- File management

✅ **Security Features**
- Password encryption (BCrypt)
- JWT token authentication
- Role-based access control
- Input validation

## 🛠️ Development

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 12+ (or Docker)

### Build and Run
```bash
# Build the project
mvn clean install

# Run in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run in production mode
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### API Endpoints

#### Authentication
- `POST /api/users/register` - User registration
- `POST /api/auth/login` - User login

#### User Management
- `GET /api/users` - List all users
- `GET /api/users/pending` - Pending registrations
- `PUT /api/users/{id}/approve` - Approve user
- `PUT /api/users/{id}/reject` - Reject user

#### File Management
- `POST /api/users/{id}/profile-photo` - Upload profile photo
- `GET /api/files/{filename}` - Download file

## 🔧 Configuration

### Database Connection
The application is pre-configured to connect to:
- **Host:** localhost
- **Port:** 5432
- **Database:** zplus_admin_panel
- **User:** zplus_user
- **Password:** zplus_password

### Email Configuration
Update `application.yml` with your email settings:
```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-app-password
```

### Environment Variables
You can override settings using environment variables:
```bash
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export MAIL_USERNAME=your_email
export MAIL_PASSWORD=your_email_password
export JWT_SECRET=your_jwt_secret
```

## 📊 Database Schema

### Main Tables
- **users** - User accounts and registration data
- **user_types** - Admin, Employee, Client roles
- **registration_status** - Pending, Approved, Rejected, etc.

### Views
- **active_users** - All approved and active users
- **pending_registrations** - Users awaiting approval
- **user_statistics** - User counts by type and status

## 🚨 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   ```bash
   # Check if PostgreSQL is running
   sudo systemctl status postgresql
   
   # Start PostgreSQL
   sudo systemctl start postgresql
   ```

2. **Port Already in Use**
   ```bash
   # Check what's using port 8080
   netstat -tulpn | grep 8080
   
   # Kill the process or change port in application.yml
   ```

3. **Permission Denied**
   ```bash
   # Make scripts executable
   chmod +x setup-database.sh
   chmod +x run-dev.sh
   chmod +x run-prod.sh
   ```

### Logs
- **Application logs:** `logs/application.log`
- **Database logs:** Check PostgreSQL logs in your system

## 📚 Next Steps

1. **Customize the Application**
   - Update company branding
   - Modify email templates
   - Add custom fields to user registration

2. **Deploy to Production**
   - Set up production database
   - Configure SSL certificates
   - Set up monitoring and backups

3. **Extend Functionality**
   - Add more user roles
   - Implement advanced reporting
   - Add API documentation

## 📞 Support

- **Documentation:** README-DATABASE.md
- **Issues:** Check the troubleshooting section
- **Configuration:** Review application.yml

---

**Happy Coding! 🎉**

Your Z+ Admin Panel is now ready to use! 