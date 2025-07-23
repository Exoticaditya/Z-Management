# Z+ Management Platform

A comprehensive business management platform built with Spring Boot and modern web technologies.

## 🚀 Features

- **Admin Dashboard**: Complete administrative control panel
- **Employee Dashboard**: Employee management and tracking
- **Client Portal**: Client-facing dashboard and services
- **Contact Management**: Advanced contact inquiry handling
- **User Management**: Role-based access control
- **Games Section**: Interactive mini-games for engagement
- **Security**: JWT-based authentication with role-based access

## 🛠️ Technologies Used

### Backend
- Java 17+
- Spring Boot 3.2.1
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT Authentication
- Maven

### Frontend
- HTML5/CSS3
- JavaScript (ES6+)
- Font Awesome Icons
- Responsive Design

## 📋 Prerequisites

Before running this application, make sure you have:

- Java 17 or higher
- PostgreSQL 12+
- Maven 3.6+
- Git

## 🔧 Installation & Setup

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/zplus-management.git
cd zplus-management
```

### 2. Database Setup
```sql
CREATE DATABASE zplus_db;
CREATE USER zplus_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE zplus_db TO zplus_user;
```

### 3. Configure Application
Create `application-local.yml` in `admin-panel/admin-backend/backend/src/main/resources/`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/zplus_db
    username: zplus_user
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

app:
  jwt:
    secret: your-jwt-secret-key-here
    expiration: 86400000
```

### 4. Build and Run
```bash
cd admin-panel/admin-backend/backend
mvn clean package
mvn spring-boot:run
```

### 5. Access the Application
- **Main Website**: http://localhost:8080/
- **Admin Panel**: http://localhost:8080/admin/
- **Employee Dashboard**: http://localhost:8080/employee-dashboard/
- **Client Portal**: http://localhost:8080/client/
- **API Documentation**: http://localhost:8080/swagger-ui/

## 🔑 Default Credentials

### Admin Access
- **Self ID**: ZP001
- **Type**: ADMIN

### Employee Access
- **Self ID**: ZP002
- **Type**: EMPLOYEE

### Client Access
- **Self ID**: ZP003
- **Type**: CLIENT

## 📁 Project Structure

```
zplus-management/
├── admin-panel/
│   └── admin-backend/
│       └── backend/          # Spring Boot Application
│           ├── src/
│           │   ├── main/
│           │   │   ├── java/
│           │   │   └── resources/
│           │   │       └── static/
│           │   │           ├── admin/     # Admin Dashboard
│           │   │           ├── client/    # Client Portal
│           │   │           ├── employee/  # Employee Dashboard
│           │   │           └── index/     # Login System
│           │   └── test/
│           └── pom.xml
├── employee-dashboard/       # Standalone Employee Dashboard
├── asset/                   # Shared Assets
├── src/                     # Additional Source Files
├── *.html                   # Landing Pages
├── style.css               # Main Stylesheet
└── README.md
```

## 🎮 Features Breakdown

### Admin Dashboard
- User registration approval/rejection
- Contact inquiry management
- Games section (Tic Tac Toe, Memory, Snake)
- System statistics and monitoring
- Data export functionality

### Employee Dashboard
- Personal profile management
- Task tracking (coming soon)
- Time management (coming soon)
- Reports and analytics (coming soon)

### Client Portal
- Project management
- Document access
- Communication tools
- Service requests

### Contact Management
- Inquiry categorization
- Status tracking (NEW, PENDING, RESOLVED, etc.)
- Sharing functionality
- Response management
- Statistics and reporting

## 🔒 Security Features

- JWT-based authentication
- Role-based access control (RBAC)
- Password encryption
- CORS configuration
- Input validation
- SQL injection prevention

## 🚀 Deployment

### Local Development
```bash
mvn spring-boot:run -Dspring.profiles.active=local
```

### Production Deployment
```bash
mvn clean package -Pprod
java -jar target/admin-panel-backend-1.0.0.jar --spring.profiles.active=prod
```

### Docker Deployment
```bash
docker build -t zplus-management .
docker run -p 8080:8080 zplus-management
```

## 📊 API Documentation

The application includes Swagger/OpenAPI documentation available at:
`http://localhost:8080/swagger-ui/`

### Key API Endpoints

- **Authentication**: `/api/auth/*`
- **User Management**: `/api/users/*`
- **Registrations**: `/api/registrations/*`
- **Contact Inquiries**: `/api/contact/*`

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with coverage
mvn test jacoco:report
```

## 📝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For support and questions:
- Email: support@zplus.com
- Documentation: [Wiki](../../wiki)
- Issues: [GitHub Issues](../../issues)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Font Awesome for the icons
- All contributors and testers

---

**Made with ❤️ by Z+ Technologies**
