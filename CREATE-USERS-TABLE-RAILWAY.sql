-- Create enum types first
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

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    department VARCHAR(100),
    user_type user_type NOT NULL,
    project_id VARCHAR(20),
    self_id VARCHAR(50) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    profile_photo_url VARCHAR(500),
    reason TEXT,
    supervisor VARCHAR(100),
    status registration_status NOT NULL DEFAULT 'PENDING',
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    rejected_by VARCHAR(100),
    rejected_at TIMESTAMP,
    reference_id VARCHAR(50) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    username VARCHAR(50) UNIQUE,
    position VARCHAR(100)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_self_id ON users(self_id);
CREATE INDEX IF NOT EXISTS idx_users_reference_id ON users(reference_id);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Insert default admin user
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
    is_active,
    username,
    position
) VALUES (
    'System',
    'Administrator',
    'adityamalik5763@gmail.com',
    '+1234567890',
    'IT Administration',
    'ADMIN',
    'ZP-2024-001',
    'ZP001',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: admin123
    'System administrator for Z+ Admin Panel',
    'System Owner',
    'APPROVED',
    'System',
    CURRENT_TIMESTAMP,
    'REF-ADMIN-001',
    TRUE,
    'admin',
    'System Administrator'
) ON CONFLICT (email) DO NOTHING;
