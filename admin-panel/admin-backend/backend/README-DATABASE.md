# Z+ Admin Panel Database Setup Guide

This guide provides comprehensive instructions for setting up and managing the PostgreSQL database for the Z+ Admin Panel project.

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Installation Methods](#installation-methods)
3. [Database Schema](#database-schema)
4. [Configuration](#configuration)
5. [Management](#management)
6. [Troubleshooting](#troubleshooting)

## 🔧 Prerequisites

### Required Software
- **PostgreSQL 12+** (recommended: PostgreSQL 15)
- **Java 17+** (for Spring Boot application)
- **Maven 3.6+** (for building the project)

### PostgreSQL Installation

#### Windows
1. Download PostgreSQL from [https://www.postgresql.org/download/windows/](https://www.postgresql.org/download/windows/)
2. Run the installer and follow the setup wizard
3. Remember the password you set for the `postgres` user
4. Add PostgreSQL bin directory to your PATH

#### macOS
```bash
# Using Homebrew
brew install postgresql
brew services start postgresql

# Or download from https://www.postgresql.org/download/macosx/
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Linux (CentOS/RHEL)
```bash
sudo yum install postgresql postgresql-server
sudo postgresql-setup initdb
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

## 🚀 Installation Methods

### Method 1: Automated Script (Recommended)

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

### Method 2: Docker Compose (Recommended for Development)

```bash
cd admin-panel/admin-backend/backend
docker-compose up -d postgres
```

This will:
- Start PostgreSQL container
- Create the database automatically
- Set up all tables and initial data
- Make database available on `localhost:5432`

### Method 3: Manual Setup

1. **Connect to PostgreSQL**
```bash
psql -U postgres
```

2. **Run the setup script**
```sql
\i setup-database.sql
```

3. **Verify setup**
```sql
\l  -- List databases
\dt -- List tables
\du -- List users
```

## 🗄️ Database Schema

### Tables

#### `users` Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-incrementing user ID |
| `first_name` | VARCHAR(50) | NOT NULL | User's first name |
| `last_name` | VARCHAR(50) | NOT NULL | User's last name |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | User's email address |
| `phone` | VARCHAR(20) | NOT NULL | User's phone number |
| `department` | VARCHAR(100) | NOT NULL | User's department |
| `user_type` | user_type | NOT NULL | User type (ADMIN/EMPLOYEE/CLIENT) |
| `project_id` | VARCHAR(20) | NOT NULL | Project ID (format: ZP-YYYY-XXX) |
| `self_id` | VARCHAR(50) | UNIQUE | User's self-assigned ID |
| `password_hash` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `profile_photo_path` | VARCHAR(500) | NULL | Path to profile photo |
| `reason` | TEXT | NOT NULL | Reason for access request |
| `supervisor` | VARCHAR(100) | NOT NULL | Supervisor's name |
| `status` | registration_status | NOT NULL, DEFAULT 'PENDING' | Registration status |
| `approved_by` | VARCHAR(100) | NULL | Name of approver |
| `approved_at` | TIMESTAMP | NULL | Approval timestamp |
| `rejection_reason` | TEXT | NULL | Reason for rejection |
| `reference_id` | VARCHAR(50) | UNIQUE | Unique reference ID |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW | Creation timestamp |
| `updated_at` | TIMESTAMP | DEFAULT NOW | Last update timestamp |
| `last_login` | TIMESTAMP | NULL | Last login timestamp |
| `is_active` | BOOLEAN | DEFAULT TRUE | Account active status |

### Enums

#### `user_type`
- `ADMIN` - System administrator
- `EMPLOYEE` - Regular employee
- `CLIENT` - External client

#### `registration_status`
- `PENDING` - Awaiting approval
- `APPROVED` - Registration approved
- `REJECTED` - Registration rejected
- `SUSPENDED` - Account suspended
- `DEACTIVATED` - Account deactivated

### Views

#### `active_users`
Shows all active and approved users.

#### `pending_registrations`
Shows all pending registration requests.

#### `user_statistics`
Shows user counts by type and status.

### Indexes
- Primary key on `id`
- Unique indexes on `email`, `self_id`, `reference_id`
- Performance indexes on `status`, `user_type`, `department`, `created_at`
- Composite indexes for common query patterns

## ⚙️ Configuration

### Application Properties

Update your `application.yml` with the correct database credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/zplus_admin_panel
    username: zplus_user
    password: zplus_password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update  # Use 'validate' in production
    show-sql: true      # Set to false in production
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### Environment Variables

You can also use environment variables:

```bash
export DB_USERNAME=zplus_user
export DB_PASSWORD=zplus_password
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=zplus_admin_panel
```

## 🔍 Management

### Database Connection

#### Using psql
```bash
psql -h localhost -p 5432 -U zplus_user -d zplus_admin_panel
```

#### Using pgAdmin (if using Docker)
- URL: http://localhost:5050
- Email: admin@zplus.com
- Password: admin123

### Common Queries

#### View all users
```sql
SELECT * FROM users ORDER BY created_at DESC;
```

#### View pending registrations
```sql
SELECT * FROM pending_registrations;
```

#### View active users
```sql
SELECT * FROM active_users;
```

#### Get user statistics
```sql
SELECT * FROM user_statistics;
```

#### Find user by email
```sql
SELECT * FROM users WHERE email = 'user@example.com';
```

#### Update user status
```sql
UPDATE users 
SET status = 'APPROVED', approved_by = 'Admin Name', approved_at = NOW() 
WHERE id = 1;
```

### Backup and Restore

#### Create backup
```bash
pg_dump -h localhost -U zplus_user -d zplus_admin_panel > backup.sql
```

#### Restore backup
```bash
psql -h localhost -U zplus_user -d zplus_admin_panel < backup.sql
```

## 🛠️ Troubleshooting

### Common Issues

#### 1. Connection Refused
```
Error: connection to server at "localhost" (127.0.0.1), port 5432 failed
```
**Solution:**
- Check if PostgreSQL is running: `sudo systemctl status postgresql`
- Start PostgreSQL: `sudo systemctl start postgresql`
- Check if port 5432 is open: `netstat -an | grep 5432`

#### 2. Authentication Failed
```
Error: FATAL: password authentication failed for user "zplus_user"
```
**Solution:**
- Verify password in `pg_hba.conf`
- Reset password: `ALTER USER zplus_user PASSWORD 'new_password';`
- Check connection parameters in application.yml

#### 3. Database Does Not Exist
```
Error: FATAL: database "zplus_admin_panel" does not exist
```
**Solution:**
- Run the setup script: `psql -U postgres -f setup-database.sql`
- Or create manually: `CREATE DATABASE zplus_admin_panel;`

#### 4. Permission Denied
```
Error: permission denied for table users
```
**Solution:**
- Grant permissions: `GRANT ALL PRIVILEGES ON ALL TABLES TO zplus_user;`
- Check user ownership: `\l+` and `\dt+`

### Performance Optimization

#### 1. Check Query Performance
```sql
EXPLAIN ANALYZE SELECT * FROM users WHERE status = 'PENDING';
```

#### 2. Monitor Database Size
```sql
SELECT pg_size_pretty(pg_database_size('zplus_admin_panel'));
```

#### 3. Check Index Usage
```sql
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes
WHERE schemaname = 'public';
```

### Logs and Monitoring

#### Enable Query Logging
Add to `postgresql.conf`:
```
log_statement = 'all'
log_min_duration_statement = 1000
```

#### View PostgreSQL Logs
```bash
# Ubuntu/Debian
sudo tail -f /var/log/postgresql/postgresql-*.log

# CentOS/RHEL
sudo tail -f /var/log/postgresql/postgresql-*.log

# macOS
tail -f /usr/local/var/log/postgres.log
```

## 🔐 Security Best Practices

1. **Change Default Passwords**
   - Change the default `zplus_password`
   - Use strong, unique passwords

2. **Network Security**
   - Restrict database access to application servers only
   - Use SSL connections in production

3. **User Permissions**
   - Grant minimal required permissions
   - Use separate users for different applications

4. **Regular Backups**
   - Set up automated daily backups
   - Test restore procedures regularly

5. **Monitoring**
   - Monitor database performance
   - Set up alerts for disk space and connection limits

## 📞 Support

If you encounter issues:

1. Check the troubleshooting section above
2. Review PostgreSQL logs for detailed error messages
3. Verify your configuration matches the examples
4. Ensure all prerequisites are met

For additional help, refer to:
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Boot Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Project Issues](https://github.com/your-repo/issues)

---

**Default Admin Account:**
- Email: `adityamalik5763@gmail.com`
- Password: `admin123`

**Database Credentials:**
- Database: `zplus_admin_panel`
- User: `zplus_user`
- Password: `zplus_password` 