-- Create user_type enum if it doesn't exist
DO $$ BEGIN
    CREATE TYPE user_type AS ENUM ('ADMIN', 'CLIENT', 'EMPLOYEE');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Verify the enum was created
SELECT 
    e.enumlabel AS enum_values 
FROM 
    pg_type t 
    JOIN pg_enum e ON t.oid = e.enumtypid  
WHERE 
    t.typname = 'user_type';
