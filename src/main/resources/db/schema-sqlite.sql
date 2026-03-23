-- Kronus Rift Login System - SQLite Database Schema
-- Date: 2026-03-23
-- Version: 1.0.0
--
-- Entity definitions from data-model.md
-- Run this script during database initialization: sqlite3 kronus_local.db < schema-sqlite.sql

-- ============================================================================
-- USERS TABLE: Player account authentication
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
    -- Primary key: UUID v4 (16 bytes as hex string)
    id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
    
    -- User credentials (username unique per player, email unique globally)
    username TEXT NOT NULL UNIQUE CHECK (LENGTH(username) >= 3 AND LENGTH(username) <= 20),
    email TEXT NOT NULL UNIQUE CHECK (email LIKE '%@%.%'),
    
    -- Password hash: Bcrypt format (cost >= 12 per Constitution Principle II)
    -- Format: $2b$<cost>$<salt><hash>
    password_hash TEXT NOT NULL,
    
    -- Account status: ACTIVE | SUSPENDED | DELETED
    status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED')),
    
    -- Timestamps for audit trail
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Last login tracking (for analytics, not security-critical)
    last_login_at TIMESTAMP,
    
    -- Rate limiting counters (5 failed attempts / 15 minutes per Constitution Principle II)
    failed_login_attempts INTEGER NOT NULL DEFAULT 0 CHECK (failed_login_attempts >= 0),
    failed_login_reset_at TIMESTAMP
);

-- Indexes for fast lookups (login by username or email)
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status) WHERE status != 'DELETED';

-- ============================================================================
-- SESSIONS TABLE: Active user sessions with JWT tokens
-- ============================================================================
CREATE TABLE IF NOT EXISTS sessions (
    -- JWT token: primary key (unique per session)
    token TEXT PRIMARY KEY,
    
    -- Foreign key to users table (cascade delete when user deleted)
    user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Session lifecycle
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,  -- NOW() + 1 hour (configurable)
    
    -- Client identification (for security and debugging)
    ip_address TEXT NOT NULL,
    user_agent TEXT,
    
    -- Revocation (for explicit logout before expiration)
    revoked_at TIMESTAMP,
    
    -- Constraints
    CHECK (revoked_at IS NULL OR revoked_at >= created_at),
    CHECK (expires_at > created_at)
);

-- Indexes for efficient queries
CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_expires ON sessions(expires_at);
CREATE INDEX IF NOT EXISTS idx_sessions_revoked ON sessions(revoked_at) WHERE revoked_at IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_sessions_active ON sessions(expires_at, revoked_at) WHERE revoked_at IS NULL;

-- ============================================================================
-- LOGIN_EVENTS TABLE: Audit trail for all authentication attempts
-- ============================================================================
-- Per Constitution Principle II (Compliance & Audit)
-- - Every successful/failed login logged
-- - Retention: 90 days minimum
-- - PII-safe: NO passwords, NO tokens, just identifiable info
-- ============================================================================
CREATE TABLE IF NOT EXISTS login_events (
    -- Unique event ID
    id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
    
    -- User identification (user_id for successful logins, NULL if username unknown)
    user_id TEXT REFERENCES users(id) ON DELETE CASCADE,
    username TEXT,  -- Use for failed logins where user_id cannot be resolved
    
    -- Event details
    ip_address TEXT NOT NULL,
    user_agent TEXT,
    success INTEGER NOT NULL DEFAULT 0 CHECK (success IN (0, 1)),  -- 0=failed, 1=succeeded
    failure_reason TEXT,  -- e.g., "INVALID_CREDENTIALS", "ACCOUNT_SUSPENDED", "RATE_LIMITED"
    
    -- Timing
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    duration_ms INTEGER,  -- Time taken for authentication attempt
    
    -- Compliance & retention
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- expires_at auto-calculated: created_at + 90 days (90 * 24 * 60 * 60 seconds)
    expires_at TIMESTAMP NOT NULL DEFAULT (datetime('now', '+90 days'))
);

-- Indexes for efficient queries (rate limiting, audit search)
CREATE INDEX IF NOT EXISTS idx_login_events_ip ON login_events(ip_address, attempted_at) WHERE success = 0;
CREATE INDEX IF NOT EXISTS idx_login_events_user ON login_events(user_id, attempted_at);
CREATE INDEX IF NOT EXISTS idx_login_events_username ON login_events(username, attempted_at) WHERE success = 0;
CREATE INDEX IF NOT EXISTS idx_login_events_expiry ON login_events(expires_at);  -- For cleanup query

-- ============================================================================
-- SCHEMA VERSION TABLE: Track migrations for future upgrades
-- ============================================================================
CREATE TABLE IF NOT EXISTS schema_versions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    version TEXT NOT NULL UNIQUE,
    description TEXT,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Record this schema version
INSERT OR IGNORE INTO schema_versions (version, description) 
    VALUES ('1.0.0', 'Initial login system schema: users, sessions, login_events');

-- ============================================================================
-- TRIGGERS: Auto-update timestamps and enforce invariants
-- ============================================================================

-- Update users.updated_at on modification
CREATE TRIGGER IF NOT EXISTS trigger_users_updated_at
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;

-- Auto-cleanup expired login events (90-day retention)
CREATE TRIGGER IF NOT EXISTS trigger_cleanup_expired_login_events
AFTER INSERT ON login_events
WHEN (SELECT COUNT(*) FROM login_events WHERE expires_at < CURRENT_TIMESTAMP) > 1000
BEGIN
    DELETE FROM login_events WHERE expires_at < CURRENT_TIMESTAMP;
END;

-- ============================================================================
-- VIEWS: Useful queries for application layer
-- ============================================================================

-- Active sessions for a user
CREATE VIEW IF NOT EXISTS vw_active_sessions AS
SELECT 
    s.token,
    s.user_id,
    s.expires_at,
    s.created_at,
    s.ip_address,
    s.user_agent
FROM sessions s
WHERE s.revoked_at IS NULL AND s.expires_at > CURRENT_TIMESTAMP;

-- Recent login attempts (last 24 hours)
CREATE VIEW IF NOT EXISTS vw_recent_login_attempts AS
SELECT 
    le.id,
    le.user_id,
    le.username,
    le.ip_address,
    le.success,
    le.failure_reason,
    le.attempted_at,
    COUNT(*) OVER (PARTITION BY le.ip_address, DATE(le.attempted_at)) AS attempts_today
FROM login_events le
WHERE le.attempted_at > datetime('now', '-1 day')
ORDER BY le.attempted_at DESC;

-- Users with failed logins (for analysis)
CREATE VIEW IF NOT EXISTS vw_users_with_failed_logins AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.failed_login_attempts,
    COUNT(le.id) AS total_failed_attempts,
    MAX(le.attempted_at) AS last_failed_attempt
FROM users u
LEFT JOIN login_events le ON u.id = le.user_id AND le.success = 0
WHERE u.status = 'ACTIVE' AND u.failed_login_attempts > 0
GROUP BY u.id
ORDER BY u.failed_login_attempts DESC;

-- ============================================================================
-- COMMENTS (Documentation in database)
-- ============================================================================
PRAGMA table_info(users);
PRAGMA table_info(sessions);
PRAGMA table_info(login_events);

-- Schema complete. Application ready for testing.
-- Next: Run database migrations (V001-V003 from data-model.md)
