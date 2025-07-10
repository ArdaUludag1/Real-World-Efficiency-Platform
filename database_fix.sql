-- Add missing created_at column to vehicles table
ALTER TABLE vehicles ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add missing created_at column to users table
ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add missing created_at column to history table
ALTER TABLE history ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Update existing records to have a created_at timestamp
UPDATE vehicles SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE users SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE history SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL; 