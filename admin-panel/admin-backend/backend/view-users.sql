-- View All Users Data
-- This script shows all user information including IDs and password hashes

-- Show all users with their IDs and credentials
SELECT 
    id,
    email,
    first_name,
    last_name,
    project_id,
    self_id,
    password_hash,
    status,
    created_at,
    updated_at
FROM users 
ORDER BY id;

-- Show summary counts
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN status = 'PENDING' THEN 1 END) as pending_users,
    COUNT(CASE WHEN status = 'APPROVED' THEN 1 END) as approved_users,
    COUNT(CASE WHEN status = 'REJECTED' THEN 1 END) as rejected_users
FROM users; 