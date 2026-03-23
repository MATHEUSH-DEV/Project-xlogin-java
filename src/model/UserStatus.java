package model;

/**
 * UserStatus: Authentication account status enum.
 * 
 * Per Constitution Principle II (Security-First):
 * - ACTIVE: Normal, usable account
 * - SUSPENDED: Temporarily disabled (can be reactivated)
 * - DELETED: Permanently deleted (CANNOT be reactivated - data separation)
 * 
 * @author Kronus Rift Security Team
 * @version 2.0 (Phase 2: Foundation) 
 */
public enum UserStatus {
    ACTIVE("ACTIVE"),
    SUSPENDED("SUSPENDED"),
    DELETED("DELETED");
    
    private final String value;
    
    UserStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Parse UserStatus from string value.
     * @param value the string value (case-insensitive)
     * @return the matching UserStatus, or ACTIVE if not found
     */
    public static UserStatus fromString(String value) {
        if (value == null) return ACTIVE;
        try {
            return UserStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;  // Default fallback
        }
    }
    
    @Override
    public String toString() {
        return value;
    }
}
