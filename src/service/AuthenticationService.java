package service;

import dao.UserDao;
import model.User;
import model.UserStatus;
import util.PasswordHashingService;
import util.AuditLoggingService;
import util.RequestContextHolder;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * AuthenticationService: Authenticates users with security hardening.
 * 
 * Per Constitution Principle II (Security-First):
 * - Timing-safe password comparison (prevents timing attacks)
 * - Tracks failed attempts for rate limiting + audit logging
 * - Never exposes system information (generic error messages)
 * - Updates last_login_at on successful authentication
 * - Requires ACTIVE status (SUSPENDED/DELETED cannot login)
 * - Request correlation via RequestContextHolder for audit trail
 * 
 * @author Kronus Rift Security Team
 * @version 2.0 (Phase 2: Foundation)
 */
public class AuthenticationService {
    
    private final UserDao userDao = new UserDao();
    private final PasswordHashingService passwordHasher = new PasswordHashingService();
    private final AuditLoggingService auditLog = new AuditLoggingService();
    
    /**
     * Authenticate user by username and password.
     * 
     * @param username the username
     * @param password the plain-text password (never stored)
     * @return authenticated User object
     * @throws IllegalStateException if account is SUSPENDED/DELETED
     * @throws RuntimeException if password verification fails
     * @throws Exception for general errors (connection, database)
     */
    public User authenticate(String username, String password) throws Exception {
        String requestId = RequestContextHolder.getRequestId();
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. Find user by username
            User user = userDao.findByUsername(username);
            
            // 2. Check if user exists (generic failure message for security)
            if (user == null) {
                auditLog.logFailedLoginAttempt(
                    username, "unknown_user", "UNKNOWN", "User not found", requestId
                );
                throw new RuntimeException("Invalid username or password");
            }
            
            // 3. Check if account is active (not SUSPENDED or DELETED)
            if (!user.isActive()) {
                auditLog.logFailedLoginAttempt(
                    username, user.getId(), "ACTIVE_CHECK_FAILED", 
                    "Account status: " + user.getStatus(), requestId
                );
                
                if (user.getStatus() == UserStatus.SUSPENDED) {
                    throw new IllegalStateException("Your account is temporarily suspended. Contact support.");
                } else if (user.getStatus() == UserStatus.DELETED) {
                    throw new IllegalStateException("Your account has been deleted and cannot be recovered.");
                }
                throw new IllegalStateException("Your account is not available for login.");
            }
            
            // 4. Verify password with timing-safe comparison
            boolean passwordMatches = passwordHasher.verifyPassword(password, user.getPasswordHash());
            
            if (!passwordMatches) {
                // Increment failed attempts
                user.incrementFailedAttempts();
                userDao.update(user);
                
                auditLog.logFailedLoginAttempt(
                    username, user.getId(), "PASSWORD_MISMATCH", 
                    "Failed attempt " + user.getFailedLoginAttempts(), requestId
                );
                throw new RuntimeException("Invalid username or password");
            }
            
            // 5. Reset failed attempts on successful login
            user.resetFailedAttempts();
            user.setLastLoginAt(LocalDateTime.now());
            userDao.update(user);
            
            // 6. Log successful login
            long duration = System.currentTimeMillis() - startTime;
            auditLog.logSuccessfulLogin(username, user.getId(), duration, requestId);
            
            return user;
            
        } catch (IllegalStateException ex) {
            // Account status errors (re-throw)
            throw ex;
        } catch (RuntimeException ex) {
            // Password/username errors (re-throw with generic message)
            throw ex;
        } catch (Exception ex) {
            // Database or other errors
            auditLog.logFailedLoginAttempt(
                username, "unknown", "DATABASE_ERROR", ex.getMessage(), requestId
            );
            throw new RuntimeException("Authentication service temporarily unavailable", ex);
        }
    }
    
    /**
     * Launch game lobby after successful authentication.
     * @param userId the authenticated user's UUID
     * @throws IOException if lobby launch fails
     */
    public void launchLobby(String userId) throws IOException {
        GameLauncher.launchLobby(userId);
    }
}
