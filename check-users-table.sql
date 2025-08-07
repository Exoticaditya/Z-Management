-- Check if users table exists and has data
SELECT COUNT(*) as user_count FROM users;

-- Show table structure
\d users;

-- Show first few users
SELECT id, email, username, first_name, last_name, user_type, status FROM users LIMIT 5;

-- Check if there's an admin user
SELECT * FROM users WHERE user_type = 'ADMIN' OR email LIKE '%admin%' LIMIT 5;
