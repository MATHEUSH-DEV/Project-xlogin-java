package com.kronus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;

/**
 * Validation Utilities for user input.
 *
 * Per Constitution Principle II (Security-First Design):
 * - Validates input before persistence
 * - Prevents SQL injection and XSS via input constraints
 * - Provides user-friendly error messages
 *
 * Validation rules per data-model.md:
 * - Username: 3-20 chars, alphanumeric + underscore
 * - Email: RFC format, max 254 chars
 * - Password: ≥8 chars, uppercase, digit
 */
public class ValidationUtils {
    private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

    // Regex patterns for validation
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[A-Z])(?=.*\\d).{8,}$"
    );

    /**
     * Validates a username.
     *
     * Requirements:
     * - 3-20 characters
     * - Alphanumeric (a-z, A-Z, 0-9) + underscore only
     * - No spaces or special characters
     *
     * @param username The username to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid with descriptive message
     */
    public static boolean isValidUsername(String username) throws IllegalArgumentException {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }

        if (username.length() > 20) {
            throw new IllegalArgumentException("Username must be at most 20 characters");
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException(
                "Username must contain only alphanumeric characters and underscores"
            );
        }

        logger.debug("Username validation passed: {}", username);
        return true;
    }

    /**
     * Validates an email address.
     *
     * Requirements:
     * - Valid email format (RFC simplified)
     * - Max 254 characters
     *
     * @param email The email to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean isValidEmail(String email) throws IllegalArgumentException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (email.length() > 254) {
            throw new IllegalArgumentException("Email must be at most 254 characters");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Additional check: valid domain
        String domain = email.substring(email.lastIndexOf("@") + 1);
        if (domain.startsWith(".") || domain.endsWith(".")) {
            throw new IllegalArgumentException("Invalid email domain");
        }

        logger.debug("Email validation passed: {}", email);
        return true;
    }

    /**
     * Validates a password.
     *
     * Requirements:
     * - Minimum 8 characters
     * - At least 1 uppercase letter (A-Z)
     * - At least 1 digit (0-9)
     * - Optional: special characters allowed but not required
     *
     * @param password The password to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid with specific reason
     */
    public static boolean isValidPassword(String password) throws IllegalArgumentException {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        if (password.length() > 128) {
            throw new IllegalArgumentException("Password must be at most 128 characters");
        }

        // Check for uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        // Check for digit
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                "Password must have ≥8 chars including uppercase letter + digit"
            );
        }

        logger.debug("Password validation passed (length: {})", password.length());
        return true;
    }

    /**
     * Validates password confirmation (both passwords match).
     *
     * @param password The first password
     * @param passwordConfirmation The confirmation password
     * @return true if they match
     * @throws IllegalArgumentException if they don't match
     */
    public static boolean passwordsMatch(String password, String passwordConfirmation)
        throws IllegalArgumentException {
        if (password == null || passwordConfirmation == null) {
            throw new IllegalArgumentException("Passwords cannot be null");
        }

        if (!password.equals(passwordConfirmation)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        return true;
    }

    /**
     * Sanitizes user input to prevent injection attacks.
     *
     * Removes or escapes potentially dangerous characters.
     * Note: Primary defense is parameterized SQL queries (via DAO layer).
     *
     * @param input The user input to sanitize
     * @return Sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }

        // Remove leading/trailing whitespace
        String sanitized = input.trim();

        // Remove null bytes (could cause SQL injection in some cases)
        sanitized = sanitized.replace("\0", "");

        // Log if sanitization made changes (potential attack attempt)
        if (!sanitized.equals(input)) {
            logger.warn("Input sanitization removed suspicious characters");
        }

        return sanitized;
    }

    /**
     * Validates that input string does not contain SQL injection patterns.
     *
     * Per Constitution Principle II: Defense in depth.
     * Primary defense: parameterized queries in DAO layer.
     * This is a secondary check to detect obvious injection attempts.
     *
     * @param input The input to check
     * @return true if input looks safe (no SQL keywords)
     */
    public static boolean isSafeSqlInput(String input) {
        if (input == null) {
            return true;
        }

        String lower = input.toLowerCase();

        // Check for common SQL injection patterns (basic - not foolproof)
        String[] sqlPatterns = {
            "'; DROP", "'; DELETE", "'; UPDATE", "'; INSERT",
            "--", "/*", "*/", "xp_", "sp_"
        };

        for (String pattern : sqlPatterns) {
            if (lower.contains(pattern)) {
                logger.warn("Potential SQL injection pattern detected: {}", pattern);
                return false;
            }
        }

        return true;
    }

    /**
     * Performs comprehensive registration input validation.
     *
     * @param username The username
     * @param email The email
     * @param password The password
     * @param passwordConfirmation The password confirmation
     * @return true if all inputs are valid
     * @throws IllegalArgumentException if any input is invalid (first error thrown)
     */
    public static boolean validateRegistrationInputs(
        String username,
        String email,
        String password,
        String passwordConfirmation
    ) throws IllegalArgumentException {
        // Sanitize first
        username = sanitizeInput(username);
        email = sanitizeInput(email);

        // Validate each field
        isValidUsername(username);
        isValidEmail(email);
        isValidPassword(password);
        passwordsMatch(password, passwordConfirmation);

        // Check for SQL injection patterns (despite parameterized queries)
        if (!isSafeSqlInput(username) || !isSafeSqlInput(email)) {
            throw new IllegalArgumentException("Input contains suspicious patterns");
        }

        logger.info("Registration inputs validated successfully");
        return true;
    }

    /**
     * Provides detailed password strength feedback.
     *
     * @param password The password to analyze
     * @return Feedback string describing password strength
     */
    public static String getPasswordStrengthFeedback(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }

        StringBuilder feedback = new StringBuilder();

        if (password.length() < 8) {
            feedback.append("❌ At least 8 characters required\n");
        } else {
            feedback.append("✓ Length: ").append(password.length()).append(" characters\n");
        }

        if (!password.matches(".*[A-Z].*")) {
            feedback.append("❌ At least one uppercase letter required\n");
        } else {
            feedback.append("✓ Contains uppercase letter\n");
        }

        if (!password.matches(".*\\d.*")) {
            feedback.append("❌ At least one digit required\n");
        } else {
            feedback.append("✓ Contains digit\n");
        }

        if (!password.matches(".*[!@#$%^&*].*")) {
            feedback.append("ℹ Consider adding special characters for extra security\n");
        } else {
            feedback.append("✓ Contains special characters\n");
        }

        return feedback.toString();
    }
}
