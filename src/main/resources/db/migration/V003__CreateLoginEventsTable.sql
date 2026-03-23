-- Flyway Migration: V003__CreateLoginEventsTable.sql
-- Date: 2026-03-23
-- Purpose: Create audit trail for login events (successful + failed attempts)
-- Retention: 90 days (per Constitution Principle II - compliance requirement)
-- Rollback: DROP TABLE login_events;

CREATE TABLE login_events (
    id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
    user_id TEXT REFERENCES users(id) ON DELETE CASCADE,
    username TEXT,
    ip_address TEXT NOT NULL,
    user_agent TEXT,
    success INTEGER NOT NULL DEFAULT 0,
    failure_reason TEXT,
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    duration_ms INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL DEFAULT (datetime('now', '+90 days')),
    CHECK (success IN (0, 1))
);

CREATE INDEX idx_login_events_ip ON login_events(ip_address, attempted_at) WHERE success = 0;
CREATE INDEX idx_login_events_user ON login_events(user_id, attempted_at);
CREATE INDEX idx_login_events_username ON login_events(username, attempted_at) WHERE success = 0;
CREATE INDEX idx_login_events_expiry ON login_events(expires_at);

-- View: Recent failed login attempts (rate limiting lookup)
CREATE VIEW vw_recent_login_attempts AS
SELECT 
    id,
    user_id,
    username,
    ip_address,
    success,
    failure_reason,
    attempted_at,
    COUNT(*) OVER (PARTITION BY ip_address, DATE(attempted_at)) AS attempts_today
FROM login_events
WHERE attempted_at > datetime('now', '-1 day')
ORDER BY attempted_at DESC;

-- Trigger: Auto-cleanup expired login events (90-day retention)
CREATE TRIGGER trigger_cleanup_expired_login_events
AFTER INSERT ON login_events
WHEN (SELECT COUNT(*) FROM login_events WHERE expires_at < CURRENT_TIMESTAMP) > 1000
BEGIN
    DELETE FROM login_events WHERE expires_at < CURRENT_TIMESTAMP;
END;

-- View: Users with failed logins (analytics)
CREATE VIEW vw_users_with_failed_logins AS
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
