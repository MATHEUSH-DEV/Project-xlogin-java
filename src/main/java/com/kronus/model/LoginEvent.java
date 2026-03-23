package com.kronus.model;

import java.time.Instant;

/**
 * LoginEvent Model - Represents an authentication attempt (success or failure).
 *
 * Per Constitution Principle II (Security-First Design):
 * - Audit trail for compliance
 * - 90-day retention
 * - PII-safe: NO passwords, NO tokens
 *
 * Corresponds to `login_events` table in data-model.md.
 */
public class LoginEvent {
    private String id;
    private String userId;  // Null for failed attempts with unknown user
    private String username;
    private String ipAddress;
    private String userAgent;
    private boolean success;
    private String failureReason;  // INVALID_CREDENTIALS, ACCOUNT_SUSPENDED, etc.
    private Instant attemptedAt;
    private Integer durationMs;
    private Instant createdAt;
    private Instant expiresAt;

    // Constructors
    public LoginEvent() {}

    public LoginEvent(String ipAddress, String username, boolean success, String failureReason) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.success = success;
        this.failureReason = failureReason;
        this.attemptedAt = Instant.now();
        this.createdAt = Instant.now();
        this.expiresAt = Instant.now().plusSeconds(90 * 24 * 60 * 60);  // 90 days
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public Instant getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(Instant attemptedAt) { this.attemptedAt = attemptedAt; }

    public Integer getDurationMs() { return durationMs; }
    public void setDurationMs(Integer durationMs) { this.durationMs = durationMs; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    /**
     * Checks if this event is expired (beyond 90-day retention).
     *
     * @return true if event should be deleted
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", success=" + success +
            ", failureReason='" + failureReason + '\'' +
            ", ip='" + ipAddress + '\'' +
            ", attemptedAt=" + attemptedAt +
            '}';
    }
}
