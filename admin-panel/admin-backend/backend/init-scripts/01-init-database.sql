-- Z+ Admin Panel Database Initialization Script
-- This script runs automatically when PostgreSQL container starts

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create enum types
DO $$ BEGIN
    CREATE TYPE user_type AS ENUM ('ADMIN', 'EMPLOYEE', 'CLIENT');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE registration_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'SUSPENDED', 'DEACTIVATED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Create users table if it doesn't exist
CREATE TABLE IF NOT EXISTS users (
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

-- Create indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_self_id ON users(self_id);
CREATE INDEX IF NOT EXISTS idx_users_reference_id ON users(reference_id);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_user_type ON users(user_type);
CREATE INDEX IF NOT EXISTS idx_users_department ON users(department);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_users_project_id ON users(project_id);

-- Create composite indexes
CREATE INDEX IF NOT EXISTS idx_users_status_user_type ON users(status, user_type);
CREATE INDEX IF NOT EXISTS idx_users_department_status ON users(department, status);
CREATE INDEX IF NOT EXISTS idx_users_created_at_status ON users(created_at, status);

-- Create or replace function for updated_at trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger if it doesn't exist
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Insert default admin user if not exists
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
) ON CONFLICT (email) DO NOTHING;

-- Create views
CREATE OR REPLACE VIEW active_users AS
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

CREATE OR REPLACE VIEW pending_registrations AS
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

CREATE OR REPLACE VIEW user_statistics AS
SELECT 
    user_type,
    status,
    COUNT(*) as count
FROM users 
GROUP BY user_type, status;

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO zplus_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO zplus_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO zplus_user;
GRANT ALL PRIVILEGES ON ALL VIEWS IN SCHEMA public TO zplus_user;

-- Set default privileges
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO zplus_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO zplus_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO zplus_user;

-- Log completion
DO $$
BEGIN
    RAISE NOTICE 'Z+ Admin Panel database initialization completed successfully!';
    RAISE NOTICE 'Default admin user: admin@zplus.com / admin123';
END $$; 