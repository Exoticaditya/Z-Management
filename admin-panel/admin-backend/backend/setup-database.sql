-- Z+ Admin Panel Database Setup Script
-- Run this script as PostgreSQL superuser (postgres)

-- =====================================================
-- DATABASE CREATION AND USER SETUP
-- =====================================================

-- Create database
CREATE DATABASE zplus_admin_panel;

-- Create user
CREATE USER zplus_user WITH PASSWORD 'zplus_password';

-- Grant privileges on database
GRANT ALL PRIVILEGES ON DATABASE zplus_admin_panel TO zplus_user;

-- Connect to the database
\c zplus_admin_panel;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO zplus_user;
GRANT CREATE ON SCHEMA public TO zplus_user;

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- ENUM TYPES CREATION
-- =====================================================

-- Create UserType enum
CREATE TYPE user_type AS ENUM ('ADMIN', 'EMPLOYEE', 'CLIENT');

-- Create RegistrationStatus enum
CREATE TYPE registration_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'SUSPENDED', 'DEACTIVATED');

-- =====================================================
-- TABLES CREATION
-- =====================================================

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    department VARCHAR(100) NOT NULL,
    user_type user_type NOT NULL,
    project_id VARCHAR(20) NOT NULL,
    self_id VARCHAR(50) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    profile_photo_path VARCHAR(500),
    reason TEXT NOT NULL,
    supervisor VARCHAR(100) NOT NULL,
    status registration_status NOT NULL DEFAULT 'PENDING',
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    reference_id VARCHAR(50) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- =====================================================
-- INDEXES CREATION
-- =====================================================

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_self_id ON users(self_id);
CREATE INDEX idx_users_reference_id ON users(reference_id);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_user_type ON users(user_type);
CREATE INDEX idx_users_department ON users(department);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_project_id ON users(project_id);

-- Composite indexes for common queries
CREATE INDEX idx_users_status_user_type ON users(status, user_type);
CREATE INDEX idx_users_department_status ON users(department, status);
CREATE INDEX idx_users_created_at_status ON users(created_at, status);

-- =====================================================
-- TRIGGERS FOR UPDATED_AT
-- =====================================================

-- Function to update the updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to automatically update updated_at
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- INITIAL DATA INSERTION
-- =====================================================

-- Insert default admin user
-- Password: admin123 (BCrypt hash)
INSERT INTO users (
    first_name, 
    last_name, 
    email, 
    phone, 
    department, 
    user_type, 
    project_id, 
    self_id, 
    password_hash, 
    reason, 
    supervisor, 
    status, 
    approved_by, 
    approved_at, 
    reference_id,
    is_active
) VALUES (
    'System',
    'Administrator',
    'adityamalik5763@gmail.com',
    '+1234567890',
    'IT Administration',
    'ADMIN',
    'ZP-2024-001',
    'ZP001',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- admin123
    'System administrator for Z+ Admin Panel',
    'System Owner',
    'APPROVED',
    'System',
    CURRENT_TIMESTAMP,
    'REF-ADMIN-001',
    TRUE
);

-- Insert sample employee
INSERT INTO users (
    first_name, 
    last_name, 
    email, 
    phone, 
    department, 
    user_type, 
    project_id, 
    self_id, 
    password_hash, 
    reason, 
    supervisor, 
    status, 
    approved_by, 
    approved_at, 
    reference_id,
    is_active
) VALUES (
    'John',
    'Employee',
    'john.employee@zplus.com',
    '+1234567891',
    'Human Resources',
    'EMPLOYEE',
    'ZP-2024-002',
    'ZP002',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- admin123
    'HR department employee for project management',
    'HR Manager',
    'APPROVED',
    'System Administrator',
    CURRENT_TIMESTAMP,
    'REF-EMP-001',
    TRUE
);

-- Insert sample client
INSERT INTO users (
    first_name, 
    last_name, 
    email, 
    phone, 
    department, 
    user_type, 
    project_id, 
    self_id, 
    password_hash, 
    reason, 
    supervisor, 
    status, 
    approved_by, 
    approved_at, 
    reference_id,
    is_active
) VALUES (
    'Mike',
    'Client',
    'mike.client@example.com',
    '+1234567892',
    'External Client',
    'CLIENT',
    'ZP-2024-003',
    'ZP003',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- admin123
    'External client for project collaboration',
    'Project Manager',
    'APPROVED',
    'System Administrator',
    CURRENT_TIMESTAMP,
    'REF-CLIENT-001',
    TRUE
);

-- =====================================================
-- CONTACT INQUIRIES TABLE
-- =====================================================

-- Create contact_inquiries table
CREATE TABLE IF NOT EXISTS contact_inquiries (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    organization VARCHAR(200),
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address TEXT,
    business_duration VARCHAR(50),
    project_timeline VARCHAR(50),
    business_challenge TEXT NOT NULL,
    contact_method VARCHAR(20) NOT NULL,
    preferred_time VARCHAR(100),
    hear_about VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    assigned_to VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP,
    response_notes TEXT
);

-- Create contact_service_interests table for many-to-many relationship
CREATE TABLE IF NOT EXISTS contact_service_interests (
    contact_inquiry_id BIGINT NOT NULL,
    service_interest VARCHAR(100) NOT NULL,
    PRIMARY KEY (contact_inquiry_id, service_interest),
    FOREIGN KEY (contact_inquiry_id) REFERENCES contact_inquiries(id) ON DELETE CASCADE
);

-- Create indexes for contact_inquiries
CREATE INDEX idx_contact_inquiries_email ON contact_inquiries(email);
CREATE INDEX idx_contact_inquiries_status ON contact_inquiries(status);
CREATE INDEX idx_contact_inquiries_created_at ON contact_inquiries(created_at);
CREATE INDEX idx_contact_inquiries_country ON contact_inquiries(country);
CREATE INDEX idx_contact_inquiries_state ON contact_inquiries(state);
CREATE INDEX idx_contact_inquiries_city ON contact_inquiries(city);
CREATE INDEX idx_contact_inquiries_assigned_to ON contact_inquiries(assigned_to);

-- Trigger for contact_inquiries updated_at
CREATE TRIGGER update_contact_inquiries_updated_at 
    BEFORE UPDATE ON contact_inquiries 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- VIEWS CREATION
-- =====================================================

-- View for active users
CREATE VIEW active_users AS
SELECT 
    id,
    first_name,
    last_name,
    email,
    phone,
    department,
    user_type,
    project_id,
    self_id,
    status,
    created_at,
    last_login
FROM users 
WHERE is_active = TRUE AND status = 'APPROVED';

-- View for pending registrations
CREATE VIEW pending_registrations AS
SELECT 
    id,
    first_name,
    last_name,
    email,
    phone,
    department,
    user_type,
    project_id,
    self_id,
    reason,
    supervisor,
    created_at
FROM users 
WHERE status = 'PENDING';

-- View for user statistics
CREATE VIEW user_statistics AS
SELECT 
    user_type,
    status,
    COUNT(*) as count
FROM users 
GROUP BY user_type, status;

-- =====================================================
-- FUNCTIONS CREATION
-- =====================================================

-- Function to get user count by department
CREATE OR REPLACE FUNCTION get_user_count_by_department()
RETURNS TABLE(department_name VARCHAR, user_count BIGINT) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.department,
        COUNT(*) as user_count
    FROM users u
    WHERE u.status = 'APPROVED' AND u.is_active = TRUE
    GROUP BY u.department
    ORDER BY user_count DESC;
END;
$$ LANGUAGE plpgsql;

-- Function to get recent registrations
CREATE OR REPLACE FUNCTION get_recent_registrations(days_back INTEGER DEFAULT 30)
RETURNS TABLE(
    id BIGINT,
    full_name VARCHAR,
    email VARCHAR,
    department VARCHAR,
    user_type user_type,
    status registration_status,
    created_at TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.id,
        CONCAT(u.first_name, ' ', u.last_name) as full_name,
        u.email,
        u.department,
        u.user_type,
        u.status,
        u.created_at
    FROM users u
    WHERE u.created_at >= CURRENT_DATE - INTERVAL '1 day' * days_back
    ORDER BY u.created_at DESC;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Verify database setup
SELECT current_database() as database_name;
SELECT current_user as current_user;
SELECT version();

-- List all tables
\dt

-- List all views
\dv

-- List all functions
\df

-- Check user count
SELECT COUNT(*) as total_users FROM users;

-- Check users by type
SELECT user_type, COUNT(*) as count FROM users GROUP BY user_type;

-- Check users by status
SELECT status, COUNT(*) as count FROM users GROUP BY status;

-- Test the views
SELECT * FROM active_users LIMIT 5;
SELECT * FROM pending_registrations LIMIT 5;
SELECT * FROM user_statistics;

-- Test the functions
SELECT * FROM get_user_count_by_department();
SELECT * FROM get_recent_registrations(7);

-- =====================================================
-- GRANT PERMISSIONS
-- =====================================================

-- Grant permissions to zplus_user
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO zplus_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO zplus_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO zplus_user;
GRANT ALL PRIVILEGES ON ALL VIEWS IN SCHEMA public TO zplus_user;

-- Grant future permissions
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO zplus_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO zplus_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO zplus_user;

-- =====================================================
-- FINAL VERIFICATION
-- =====================================================

-- List all databases
\l

-- List all users
\du

-- Show table structure
\d users

-- Show enum types
\dT+ user_type
\dT+ registration_status

PRINT 'Z+ Admin Panel Database Setup Completed Successfully!';
PRINT 'Database: zplus_admin_panel';
PRINT 'User: zplus_user';
PRINT 'Default Admin Email: admin@zplus.com';
PRINT 'Default Password: admin123'; 