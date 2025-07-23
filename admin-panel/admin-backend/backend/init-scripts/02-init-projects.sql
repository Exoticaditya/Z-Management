-- Project Management Tables
-- This script creates the necessary tables for project management

-- Create Project Status Enum
DO $$ BEGIN
    CREATE TYPE project_status AS ENUM ('PLANNING', 'ACTIVE', 'ON_HOLD', 'COMPLETED', 'CANCELLED', 'SUSPENDED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Create Project Priority Enum
DO $$ BEGIN
    CREATE TYPE project_priority AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Create Projects Table
CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    project_name VARCHAR(255) NOT NULL,
    project_id VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    status project_status NOT NULL DEFAULT 'PLANNING',
    priority project_priority NOT NULL DEFAULT 'MEDIUM',
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    budget DECIMAL(15,2),
    actual_cost DECIMAL(15,2),
    progress_percentage INTEGER CHECK (progress_percentage >= 0 AND progress_percentage <= 100),
    manager_id BIGINT REFERENCES users(id),
    client_id BIGINT REFERENCES users(id),
    department VARCHAR(100),
    technologies TEXT,
    notes TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create Project Team Members Junction Table
CREATE TABLE IF NOT EXISTS project_team_members (
    project_id BIGINT REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(100),
    PRIMARY KEY (project_id, user_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_projects_project_id ON projects(project_id);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);
CREATE INDEX IF NOT EXISTS idx_projects_priority ON projects(priority);
CREATE INDEX IF NOT EXISTS idx_projects_department ON projects(department);
CREATE INDEX IF NOT EXISTS idx_projects_manager_id ON projects(manager_id);
CREATE INDEX IF NOT EXISTS idx_projects_client_id ON projects(client_id);
CREATE INDEX IF NOT EXISTS idx_projects_created_at ON projects(created_at);
CREATE INDEX IF NOT EXISTS idx_projects_is_active ON projects(is_active);
CREATE INDEX IF NOT EXISTS idx_projects_start_date ON projects(start_date);
CREATE INDEX IF NOT EXISTS idx_projects_end_date ON projects(end_date);
CREATE INDEX IF NOT EXISTS idx_projects_progress_percentage ON projects(progress_percentage);

-- Composite indexes for common queries
CREATE INDEX IF NOT EXISTS idx_projects_status_priority ON projects(status, priority);
CREATE INDEX IF NOT EXISTS idx_projects_department_status ON projects(department, status);
CREATE INDEX IF NOT EXISTS idx_projects_created_at_status ON projects(created_at, status);

-- Create trigger to update updated_at column
CREATE OR REPLACE FUNCTION update_projects_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_projects_updated_at 
    BEFORE UPDATE ON projects 
    FOR EACH ROW 
    EXECUTE FUNCTION update_projects_updated_at_column();

-- Create views for common queries
CREATE OR REPLACE VIEW active_projects AS
SELECT * FROM projects 
WHERE status = 'ACTIVE' AND is_active = true;

CREATE OR REPLACE VIEW overdue_projects AS
SELECT * FROM projects 
WHERE end_date < CURRENT_TIMESTAMP 
  AND status NOT IN ('COMPLETED', 'CANCELLED') 
  AND is_active = true;

CREATE OR REPLACE VIEW project_statistics AS
SELECT 
    status,
    priority,
    department,
    COUNT(*) as project_count,
    AVG(progress_percentage) as avg_progress,
    SUM(budget) as total_budget,
    SUM(actual_cost) as total_actual_cost
FROM projects 
WHERE is_active = true
GROUP BY status, priority, department;

-- Insert sample project data
INSERT INTO projects (project_name, project_id, description, status, priority, start_date, end_date, budget, progress_percentage, department, technologies, notes) VALUES
('Website Redesign', 'ZP-2024-001', 'Complete redesign of company website with modern UI/UX', 'ACTIVE', 'HIGH', '2024-01-15 00:00:00', '2024-06-30 00:00:00', 50000.00, 75, 'Technology', 'React, Node.js, PostgreSQL', 'On track for completion'),
('Mobile App Development', 'ZP-2024-002', 'iOS and Android mobile application for client services', 'ACTIVE', 'CRITICAL', '2024-02-01 00:00:00', '2024-08-15 00:00:00', 75000.00, 45, 'Technology', 'React Native, Firebase, AWS', 'Behind schedule due to API integration issues'),
('Marketing Campaign', 'ZP-2024-003', 'Q2 marketing campaign for new product launch', 'PLANNING', 'MEDIUM', '2024-04-01 00:00:00', '2024-06-30 00:00:00', 25000.00, 0, 'Marketing', 'Social Media, Google Ads, Analytics', 'Campaign strategy in development'),
('Database Migration', 'ZP-2024-004', 'Migrate legacy database to cloud infrastructure', 'COMPLETED', 'HIGH', '2024-01-01 00:00:00', '2024-03-31 00:00:00', 30000.00, 100, 'Technology', 'AWS RDS, PostgreSQL, Data Migration Tools', 'Successfully completed ahead of schedule'),
('HR System Upgrade', 'ZP-2024-005', 'Upgrade HR management system with new features', 'ON_HOLD', 'MEDIUM', '2024-03-01 00:00:00', '2024-07-31 00:00:00', 40000.00, 30, 'HR', 'Java, Spring Boot, MySQL', 'On hold due to budget constraints'),
('Financial Audit', 'ZP-2024-006', 'Annual financial audit and compliance review', 'ACTIVE', 'HIGH', '2024-01-01 00:00:00', '2024-05-31 00:00:00', 15000.00, 80, 'Finance', 'Audit Tools, Excel, Financial Software', 'Final review in progress'),
('Product Research', 'ZP-2024-007', 'Market research for new product development', 'ACTIVE', 'LOW', '2024-02-15 00:00:00', '2024-05-15 00:00:00', 20000.00, 60, 'Research', 'Survey Tools, Analytics, Market Data', 'Data collection phase completed'),
('Infrastructure Upgrade', 'ZP-2024-008', 'Upgrade server infrastructure and security', 'PLANNING', 'CRITICAL', '2024-05-01 00:00:00', '2024-08-31 00:00:00', 100000.00, 0, 'Technology', 'AWS, Docker, Kubernetes, Security Tools', 'Planning phase - vendor selection in progress'),
('Customer Portal', 'ZP-2024-009', 'Develop customer self-service portal', 'ACTIVE', 'HIGH', '2024-03-01 00:00:00', '2024-07-31 00:00:00', 60000.00, 55, 'Technology', 'Angular, .NET Core, SQL Server', 'Frontend development completed, backend in progress'),
('Training Program', 'ZP-2024-010', 'Employee training program for new software', 'COMPLETED', 'MEDIUM', '2024-01-01 00:00:00', '2024-02-28 00:00:00', 10000.00, 100, 'HR', 'Learning Management System, Video Content', 'All employees trained successfully')
ON CONFLICT (project_id) DO NOTHING;

-- Grant permissions
GRANT ALL PRIVILEGES ON TABLE projects TO zplus_user;
GRANT ALL PRIVILEGES ON TABLE project_team_members TO zplus_user;
GRANT ALL PRIVILEGES ON SEQUENCE projects_id_seq TO zplus_user;
GRANT SELECT ON active_projects TO zplus_user;
GRANT SELECT ON overdue_projects TO zplus_user;
GRANT SELECT ON project_statistics TO zplus_user; 