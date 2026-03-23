package com.kronus.model;

import java.time.Instant;

/**
 * User Model - Represents a player account.
 *
 * Corresponds to `users` table in data-model.md.
 */
public class User {
    private String id;
    private String username;
    private String email;
    private String passwordHash;
    private String status;  // ACTIVE, SUSPENDED, DELETED
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;
    private int failedLoginAttempts;
    private Instant failedLoginResetAt;

    // Constructors
    public User() {}

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = "ACTIVE";
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.failedLoginAttempts = 0;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public Instant getFailedLoginResetAt() { return failedLoginResetAt; }
    public void setFailedLoginResetAt(Instant failedLoginResetAt) { this.failedLoginResetAt = failedLoginResetAt; }

    @Override
    public String toString() {
        return "User{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", status='" + status + '\'' +
            ", failedLoginAttempts=" + failedLoginAttempts +
            '}';
    }
}
