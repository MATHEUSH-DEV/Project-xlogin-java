-- Flyway Migration: V002__CreateSessionsTable.sql
-- Date: 2026-03-23
-- Purpose: Create sessions table for JWT token management
-- Rollback: DROP TABLE sessions;

CREATE TABLE sessions (
    token TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    ip_address TEXT NOT NULL,
    user_agent TEXT,
    revoked_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (revoked_at IS NULL OR revoked_at >= created_at),
    CHECK (expires_at > created_at)
);

CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_sessions_expires ON sessions(expires_at);
CREATE INDEX idx_sessions_revoked ON sessions(revoked_at) WHERE revoked_at IS NOT NULL;
CREATE INDEX idx_sessions_active ON sessions(expires_at, revoked_at) WHERE revoked_at IS NULL;

-- View: Active sessions for quick lookup
CREATE VIEW vw_active_sessions AS
SELECT 
    token,
    user_id,
    expires_at,
    created_at,
    ip_address,
    user_agent
FROM sessions
WHERE revoked_at IS NULL AND expires_at > CURRENT_TIMESTAMP;
