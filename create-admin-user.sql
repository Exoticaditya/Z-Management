-- Create ENUM types if they don't exist
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
    position,
    created_at,
    updated_at
) VALUES (
    'System',
    'Administrator',
    'admin@zplus.com',
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
    'System Administrator',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO UPDATE SET
    password_hash = EXCLUDED.password_hash,
    status = 'APPROVED',
    is_active = TRUE;

-- Verify the user was created
SELECT id, email, username, first_name, last_name, user_type, status, is_active 
FROM users WHERE email = 'admin@zplus.com';
