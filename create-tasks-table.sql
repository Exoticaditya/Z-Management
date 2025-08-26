-- Create tasks table for task management system
CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'TODO',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    project_id VARCHAR(100),
    assigned_to VARCHAR(100),
    created_by VARCHAR(100) NOT NULL,
    due_date TIMESTAMP,
    estimated_hours INTEGER,
    actual_hours INTEGER,
    notes TEXT,
    tags VARCHAR(500),
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_tasks_assigned_to ON tasks(assigned_to);
CREATE INDEX IF NOT EXISTS idx_tasks_project_id ON tasks(project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_priority ON tasks(priority);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_created_by ON tasks(created_by);
CREATE INDEX IF NOT EXISTS idx_tasks_created_at ON tasks(created_at);

-- Add constraint for task status values
ALTER TABLE tasks ADD CONSTRAINT IF NOT EXISTS chk_task_status 
    CHECK (status IN ('TODO', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED', 'CANCELLED', 'ON_HOLD'));

-- Add constraint for task priority values
ALTER TABLE tasks ADD CONSTRAINT IF NOT EXISTS chk_task_priority 
    CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT', 'CRITICAL'));

-- Create trigger to automatically update the updated_at column
CREATE OR REPLACE FUNCTION update_task_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_task_updated_at ON tasks;
CREATE TRIGGER update_task_updated_at 
    BEFORE UPDATE ON tasks 
    FOR EACH ROW 
    EXECUTE FUNCTION update_task_updated_at_column();

-- Insert some sample tasks for testing
INSERT INTO tasks (title, description, status, priority, project_id, assigned_to, created_by, due_date, estimated_hours, notes, tags) VALUES 
('Setup Database', 'Configure PostgreSQL database for production', 'COMPLETED', 'HIGH', 'admin-panel', 'admin', 'admin', '2024-01-15 10:00:00', 8, 'Database setup completed successfully', 'database,setup,backend'),
('Implement Authentication', 'Create JWT authentication system', 'COMPLETED', 'HIGH', 'admin-panel', 'admin', 'admin', '2024-01-20 15:00:00', 12, 'JWT authentication implemented with BCrypt password encoding', 'authentication,jwt,security'),
('Create Task Management', 'Build complete task management system', 'IN_PROGRESS', 'HIGH', 'admin-panel', 'admin', 'admin', '2024-01-25 17:00:00', 20, 'Working on task management features', 'tasks,management,crud'),
('Frontend Integration', 'Connect task management to frontend', 'TODO', 'MEDIUM', 'admin-panel', NULL, 'admin', '2024-01-30 12:00:00', 10, NULL, 'frontend,integration,ui'),
('Testing & QA', 'Comprehensive testing of all features', 'TODO', 'MEDIUM', 'admin-panel', NULL, 'admin', '2024-02-05 16:00:00', 15, NULL, 'testing,qa,validation'),
('Deployment Setup', 'Prepare application for production deployment', 'TODO', 'LOW', 'admin-panel', NULL, 'admin', '2024-02-10 14:00:00', 6, NULL, 'deployment,production,docker');

-- Update completed tasks with completion timestamps
UPDATE tasks 
SET completed_at = CURRENT_TIMESTAMP 
WHERE status = 'COMPLETED' AND completed_at IS NULL;
