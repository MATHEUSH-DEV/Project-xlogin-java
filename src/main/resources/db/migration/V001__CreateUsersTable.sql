-- Flyway Migration: V001__CreateUsersTable.sql
-- Date: 2026-03-23
-- Purpose: Create users table for player authentication
-- Rollback: DROP TABLE users;

CREATE TABLE users (
    id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    failed_login_reset_at TIMESTAMP,
    CHECK (length(username) >= 3 AND length(username) <= 20),
    CHECK (email LIKE '%@%.%'),
    CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED')),
    CHECK (failed_login_attempts >= 0)
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status) WHERE status != 'DELETED';

-- Auto-update timestamp trigger
CREATE TRIGGER trigger_users_updated_at
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;
