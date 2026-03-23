package com.kronus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Password Hashing Service using BCrypt.
 *
 * Per Constitution Principle II (Security-First Design):
 * - Enforces BCrypt cost factor ≥12 (non-negotiable)
 * - Never stores, logs, or transmits plaintext passwords
 * - Timing-safe comparison to prevent timing attacks
 */
public class PasswordHashingService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordHashingService.class);

    private final int bcryptCost;
    private final BCryptPasswordEncoder encoder;

    /**
     * Initializes password hashing service with configurable BCrypt cost.
     *
     * @param configLoader The configuration loader
     * @throws IllegalArgumentException if cost factor is less than 12
     */
    public PasswordHashingService(ConfigLoader configLoader) throws IllegalArgumentException {
        // Read BCrypt cost from config (env var > properties > default)
        this.bcryptCost = configLoader.getInt("bcrypt.cost", 12);

        // Validate cost ≥12 (Constitution Principle II - NON-NEGOTIABLE)
        if (bcryptCost < 12) {
            logger.error("BCrypt cost must be ≥12 per Constitution Principle II, got: {}", bcryptCost);
            throw new IllegalArgumentException("BCrypt cost must be ≥12, got: " + bcryptCost);
        }

        if (bcryptCost > 31) {
            logger.error("BCrypt cost must be ≤31, got: {}", bcryptCost);
            throw new IllegalArgumentException("BCrypt cost must be ≤31, got: " + bcryptCost);
        }

        // Create BCrypt encoder with specified cost
        this.encoder = new BCryptPasswordEncoder(bcryptCost);

        logger.info("PasswordHashingService initialized with BCrypt cost: {}", bcryptCost);
    }

    /**
     * Hashes a password using BCrypt.
     *
     * Per Constitution Principle II: Never logs the password.
     *
     * @param plainPassword The password in plaintext (will NOT be stored)
     * @return Bcrypt-hashed password (format: $2b$cost$...)
     * @throws IllegalArgumentException if password is null or empty
     */
    public String hashPassword(String plainPassword) throws IllegalArgumentException {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (plainPassword.length() > 72) {
            // BCrypt has a 72-character limit; longer passwords are truncated
            logger.warn("Password exceeds 72 characters and will be truncated");
        }

        return encoder.encode(plainPassword);
    }

    /**
     * Verifies a plaintext password against a BCrypt hash.
     *
     * Timing-safe comparison prevents timing attacks.
     * Per Constitution Principle II: Never logs the plaintext password.
     *
     * @param plainPassword The password to verify (plaintext)
     * @param hashedPassword The stored BCrypt hash
     * @return true if password matches hash, false otherwise
     */
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            logger.warn("Null password or hash during verification");
            return false;
        }

        try {
            // BCryptPasswordEncoder.matches() performs timing-safe comparison
            return encoder.matches(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            logger.warn("Invalid BCrypt hash format during verification", e);
            return false;
        }
    }

    /**
     * Upgrades a password hash if the BCrypt cost has changed.
     *
     * Use case: If cost is increased in config, old hashes can be rehashed on next login.
     *
     * @param hashedPassword The stored hash
     * @return true if hash needs upgrade (current cost differs), false otherwise
     */
    public boolean needsUpgrade(String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2")) {
            return false;
        }

        // Extract current cost from hash (e.g., $2b$12$... -> cost 12)
        try {
            String costStr = hashedPassword.substring(4, 6);
            int hashCost = Integer.parseInt(costStr);
            return hashCost != bcryptCost;
        } catch (Exception e) {
            logger.warn("Failed to extract cost from hash", e);
            return false;
        }
    }

    /**
     * Returns the current BCrypt cost factor (for testing/monitoring).
     *
     * @return BCrypt cost (10-31)
     */
    public int getBcryptCost() {
        return bcryptCost;
    }

    /**
     * Estimates the hashing time for performance monitoring.
     *
     * @return Approximate milliseconds to hash a password
     */
    public long estimateHashingTime() {
        long start = System.currentTimeMillis();
        hashPassword("benchmark-password-" + System.nanoTime());
        return System.currentTimeMillis() - start;
    }
}
