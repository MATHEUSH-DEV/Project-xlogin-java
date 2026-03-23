package com.kronus.model;

import java.time.Instant;

/**
 * Session Model - Represents an active user session with JWT token.
 *
 * Corresponds to `sessions` table in data-model.md.
 */
public class Session {
    private String token;  // JWT token (primary key)
    private String userId;
    private Instant createdAt;
    private Instant expiresAt;
    private String ipAddress;
    private String userAgent;
    private Instant revokedAt;

    // Constructors
    public Session() {}

    public Session(String token, String userId, Instant expiresAt, String ipAddress) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.ipAddress = ipAddress;
        this.createdAt = Instant.now();
    }

    // Getters & Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }

    /**
     * Checks if session is expired.
     *
     * @return true if current time > expiresAt
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Checks if session is revoked.
     *
     * @return true if revokedAt is set
     */
    public boolean isRevoked() {
        return revokedAt != null;
    }

    /**
     * Checks if session is still active.
     *
     * @return true if not expired and not revoked
     */
    public boolean isActive() {
        return !isExpired() && !isRevoked();
    }

    @Override
    public String toString() {
        return "Session{" +
            "token='" + (token != null ? token.substring(0, Math.min(16, token.length())) + "..." : null) + '\'' +
            ", userId='" + userId + '\'' +
            ", active=" + isActive() +
            ", expiresAt=" + expiresAt +
            '}';
    }
}
