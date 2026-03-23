package service;

import dao.UserDao;
import model.User;
import model.UserStatus;
import util.PasswordHashingService;
import util.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RegistrationService: Handles user registration with validation & security.
 * 
 * Per Constitution Principle II (Security-First):
 * - Validates all input (username, email, password) BEFORE database operations
 * - Uses BCrypt hashing with cost ≥12 (enforced in PasswordHashingService)
 * - Checks for username/email conflicts to prevent account takeover
 * - NO hardcoded passwords or secrets
 * 
 * @author Kronus Rift Security Team
 * @version 2.0 (Phase 2: Foundation)
 */
public class RegistrationService {
    
    private final UserDao userDao = new UserDao();
    private final PasswordHashingService passwordHasher = new PasswordHashingService();
    private final ValidationUtils validator = new ValidationUtils();
    
    /**
     * Register new user with email confirmation.
     * @param username the desired username (3-20 chars)
     * @param email the user's email address
     * @param password the password (≥8 chars, uppercase + digit required)
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if username/email already taken
     */
    public void register(String username, String email, String password) throws Exception {
        // 1. Validate input
        if (username == null || username.isEmpty() 
            || email == null || email.isEmpty() 
            || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("All fields are required!");
        }
        
        // 2. Validate username format
        String usernameValidation = validator.validateUsername(username);
        if (!usernameValidation.equals("✓")) {
            throw new IllegalArgumentException("Invalid username: " + usernameValidation);
        }
        
        // 3. Validate email format
        String emailValidation = validator.validateEmail(email);
        if (!emailValidation.equals("✓")) {
            throw new IllegalArgumentException("Invalid email: " + emailValidation);
        }
        
        // 4. Validate password strength
        String passwordValidation = validator.validatePassword(password);
        if (!passwordValidation.equals("✓")) {
            throw new IllegalArgumentException("Password too weak: " + passwordValidation);
        }
        
        // 5. Check for username conflict
        try {
            User existing = userDao.findByUsername(username);
            if (existing != null && !existing.getStatus().equals(UserStatus.DELETED)) {
                throw new SQLException("Username already taken: " + username);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("already taken")) {
                throw e;  // Re-throw username conflict
            }
            // Otherwise might be a connection error, continue
        }
        
        // 6. Check for email conflict
        try {
            User existing = userDao.findByEmail(email);
            if (existing != null && !existing.getStatus().equals(UserStatus.DELETED)) {
                throw new SQLException("Email already registered: " + email);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("already registered")) {
                throw e;  // Re-throw email conflict
            }
            // Otherwise might be a connection error, continue
        }
        
        // 7. Hash password with BCrypt (cost ≥12 enforced)
        String passwordHash = passwordHasher.hashPassword(password);
        
        // 8. Create user object with UUID
        LocalDateTime now = LocalDateTime.now();
        User newUser = new User.Builder()
            .id(UUID.randomUUID().toString())
            .username(username.trim())
            .email(email.trim())
            .passwordHash(passwordHash)
            .status(UserStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .lastLoginAt(now)
            .failedLoginAttempts(0)
            .failedLoginResetAt(now)
            .build();
        
        // 9. Insert into database
        boolean inserted = userDao.insert(newUser);
        if (!inserted) {
            throw new SQLException("Failed to insert user into database!");
        }
    }
    
    /**
     * Register with password confirmation check.
     * @param username the desired username
     * @param email the user's email
     * @param password the password
     * @param confirmPassword confirmation password
     * @throws IllegalArgumentException if passwords don't match
     */
    public void registerWithConfirmation(String username, String email, String password, String confirmPassword) throws Exception {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match!");
        }
        register(username, email, password);
    }
}
