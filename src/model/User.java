package model;

import java.time.LocalDateTime;

/**
 * User: Domain model for authenticated users.
 * 
 * Per Constitution Principle II (Security-First):
 * - Uses UUID for distributed ID generation (no predictable sequences)
 * - Tracks failed login attempts for rate limiting
 * - Never stores plain-text passwords (only hashes)
 * - Status field prevents compromise of deleted accounts (DELETED ≠ NULL)
 * - Timestamps track account lifecycle and security events
 * 
 * @author Kronus Rift Security Team
 * @version 2.0 (Phase 2: Foundation)
 */
public class User {
    
    // Identity & Authentication
    private String id;                      // UUID (unique, no sequence predictability)
    private String username;                // 3-20 chars, unique, indexed
    private String email;                   // RFC-compliant, unique, indexed
    private String passwordHash;            // BCrypt $2b$12$... (cost ≥12, NEVER plain-text)
    
    // Status & Lifecycle
    private UserStatus status;              // ACTIVE | SUSPENDED | DELETED
    private LocalDateTime createdAt;        // Account creation timestamp
    private LocalDateTime updatedAt;        // Last profile update timestamp
    private LocalDateTime lastLoginAt;      // Last successful login timestamp
    
    // Security & Rate Limiting
    private int failedLoginAttempts;        // Counter for failed login attempts
    private LocalDateTime failedLoginResetAt; // When counter should reset
    
    // ==================== Constructors ====================
    
    public User() {
    }
    
    /**
     * Legacy constructor for backward compatibility.
     * @deprecated Use User.Builder instead for new code
     */
    public User(int legacyId, String username, String email, String passwordHash, UserStatus status) {
        this.id = String.valueOf(legacyId);
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.lastLoginAt = now;
        this.failedLoginAttempts = 0;
        this.failedLoginResetAt = now;
    }
    
    // ==================== Builder Pattern ====================
    
    /**
     * Fluent User builder for clean object construction.
     * Usage: new User.Builder().id(uuid).username("admin").email("admin@kronus.com")...build()
     */
    public static class Builder {
        private String id;
        private String username;
        private String email;
        private String passwordHash;
        private UserStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLoginAt;
        private int failedLoginAttempts;
        private LocalDateTime failedLoginResetAt;
        
        public Builder id(String id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder status(UserStatus status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder lastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; return this; }
        public Builder failedLoginAttempts(int attempts) { this.failedLoginAttempts = attempts; return this; }
        public Builder failedLoginResetAt(LocalDateTime resetAt) { this.failedLoginResetAt = resetAt; return this; }
        
        public User build() {
            User user = new User();
            user.id = this.id;
            user.username = this.username;
            user.email = this.email;
            user.passwordHash = this.passwordHash;
            user.status = this.status;
            user.createdAt = this.createdAt;
            user.updatedAt = this.updatedAt;
            user.lastLoginAt = this.lastLoginAt;
            user.failedLoginAttempts = this.failedLoginAttempts;
            user.failedLoginResetAt = this.failedLoginResetAt;
            return user;
        }
    }
    
    // ==================== Getters & Setters ====================
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    
    public LocalDateTime getFailedLoginResetAt() { return failedLoginResetAt; }
    public void setFailedLoginResetAt(LocalDateTime failedLoginResetAt) { this.failedLoginResetAt = failedLoginResetAt; }
    
    // ==================== Business Logic Methods ====================
    
    /**
     * Check if account is active for login.
     * Per Constitution Principle II: DELETED accounts cannot be reactivated.
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
    
    /**
     * Check if account is locked due to failed attempts.
     * Consult rate limiting service for state-of-art thresholds.
     */
    public boolean isLocked() {
        return failedLoginAttempts >= 5;  // Configurable threshold
    }
    
    /**
     * Reset failed login counter after successful auth.
     */
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.failedLoginResetAt = LocalDateTime.now();
    }
    
    /**
     * Increment failed attempts counter.
     * Called after each failed login attempt for rate limiting.
     */
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }
    
    // ==================== Object Methods ====================
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", failedAttempts=" + failedLoginAttempts +
                '}';
    }
}
