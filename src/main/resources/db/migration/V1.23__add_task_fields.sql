-- Add new nullable columns to ai_dev_task table for Phase 1 optimization
ALTER TABLE ai_dev_task 
ADD COLUMN IF NOT EXISTS target_branch VARCHAR(255),
ADD COLUMN IF NOT EXISTS related_issues VARCHAR(255),
ADD COLUMN IF NOT EXISTS constraints TEXT,
ADD COLUMN IF NOT EXISTS affected_projects JSONB,
ADD COLUMN IF NOT EXISTS priority VARCHAR(50),
ADD COLUMN IF NOT EXISTS labels JSONB;
