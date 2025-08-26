-- Create enum types for PostgreSQL if they don't exist

-- Create user_type enum
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_type') THEN
        CREATE TYPE user_type AS ENUM (
            'ADMIN',
            'MANAGER',
            'EMPLOYEE',
            'CLIENT'
        );
    END IF;
END
$$;

-- Create registration_status enum
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'registration_status') THEN
        CREATE TYPE registration_status AS ENUM (
            'PENDING',
            'APPROVED',
            'REJECTED',
            'SUSPENDED'
        );
    END IF;
END
$$;

-- Create contact_status enum  
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'contact_status') THEN
        CREATE TYPE contact_status AS ENUM (
            'PENDING',
            'IN_PROGRESS', 
            'RESOLVED',
            'CLOSED'
        );
    END IF;
END
$$;

-- Create task_status enum
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'task_status') THEN
        CREATE TYPE task_status AS ENUM (
            'TODO',
            'IN_PROGRESS',
            'REVIEW',
            'DONE',
            'CANCELLED'
        );
    END IF;
END
$$;

-- Create task_priority enum
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'task_priority') THEN
        CREATE TYPE task_priority AS ENUM (
            'LOW',
            'MEDIUM',
            'HIGH',
            'CRITICAL'
        );
    END IF;
END
$$;

-- Update database schema information
UPDATE pg_database SET datname = datname WHERE datname = current_database();

-- Grant necessary permissions
GRANT USAGE ON TYPE user_type TO PUBLIC;
GRANT USAGE ON TYPE registration_status TO PUBLIC;
GRANT USAGE ON TYPE contact_status TO PUBLIC;
GRANT USAGE ON TYPE task_status TO PUBLIC;
GRANT USAGE ON TYPE task_priority TO PUBLIC;

-- Verify enum types were created
SELECT 
    typname as enum_name,
    array_agg(enumlabel ORDER BY enumsortorder) as enum_values
FROM pg_enum pe
JOIN pg_type pt ON pe.enumtypid = pt.oid
WHERE pt.typname IN ('user_type', 'registration_status', 'contact_status', 'task_status', 'task_priority')
GROUP BY typname
ORDER BY typname;
