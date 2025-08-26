-- Database Migration Script for Z+ Management System
-- This script ensures all necessary database enums and tables exist

-- Create enum types
CREATE TYPE IF NOT EXISTS user_type AS ENUM (
    'ADMIN',
    'MANAGER', 
    'EMPLOYEE',
    'CLIENT'
);

CREATE TYPE IF NOT EXISTS registration_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED',
    'SUSPENDED'
);

CREATE TYPE IF NOT EXISTS contact_status AS ENUM (
    'PENDING',
    'IN_PROGRESS',
    'RESOLVED', 
    'CLOSED'
);

CREATE TYPE IF NOT EXISTS task_status AS ENUM (
    'TODO',
    'IN_PROGRESS',
    'REVIEW',
    'DONE',
    'CANCELLED'
);

CREATE TYPE IF NOT EXISTS task_priority AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH',
    'CRITICAL'
);

-- Ensure all necessary tables exist with correct structure
-- (The JPA entities will handle table creation, but we ensure enums exist first)
