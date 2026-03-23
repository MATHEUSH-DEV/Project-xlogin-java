package com.kronus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kronus.util.RequestContextHolder;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Audit Logging Service for authentication events.
 *
 * Per Constitution Principle II (Security-First Design):
 * - Logs ALL successful and failed authentication attempts
 * - PII-safe: NO passwords, tokens, or sensitive data
 * - 90-day retention per compliance requirements
 * - Async logging to avoid blocking authentication flow
 */
public class AuditLoggingService {
    private static final Logger auditLogger = LoggerFactory.getLogger("com.kronus.service.AuthenticationService");
    private static final Logger logger = LoggerFactory.getLogger(AuditLoggingService.class);

    private final ScheduledExecutorService executor;
    private final ConcurrentHashMap<String, LoginAttempt> pendingEvents;

    /**
     * Initializes the audit logging service with async execution.
     */
    public AuditLoggingService() {
        this.executor = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "AuditLogger");
            t.setDaemon(true);
            return t;
        });
        this.pendingEvents = new ConcurrentHashMap<>();
        logger.info("AuditLoggingService initialized");
    }

    /**
     * Logs a successful login event.
     *
     * Per Constitution Principle II: NO passwords or tokens logged.
     *
     * @param userId User ID (from database)
     * @param username Username (for audit trail)
     * @param ipAddress Client IP address
     * @param userAgent User-Agent header
     * @param durationMs Time taken to authenticate (ms)
     */
    public void logSuccessfulLogin(String userId, String username, String ipAddress, String userAgent,
                                    long durationMs) {
        String requestId = RequestContextHolder.getRequestId();
        executor.submit(() -> {
            auditLogger.info("SUCCESSFUL_LOGIN user_id={} username={} ip={} request_id={} duration_ms={}",
                userId, username, ipAddress, requestId, durationMs);
            logger.debug("Successful login audit logged: user={}", username);
        });
    }

    /**
     * Logs a failed login attempt.
     *
     * Per Constitution Principle II: Reason logged (not password).
     *
     * @param username Username (for audit trail)
     * @param ipAddress Client IP address
     * @param failureReason Reason for failure (e.g., INVALID_CREDENTIALS)
     * @param userAgent User-Agent header
     * @param durationMs Time taken to process request (ms)
     */
    public void logFailedLoginAttempt(String username, String ipAddress, String failureReason, String userAgent,
                                       long durationMs) {
        String requestId = RequestContextHolder.getRequestId();
        executor.submit(() -> {
            auditLogger.warn("FAILED_LOGIN username={} ip={} reason={} request_id={} duration_ms={}",
                username, ipAddress, failureReason, requestId, durationMs);
            logger.debug("Failed login audit logged: username={} reason={}", username, failureReason);

            // Track for rate limiting
            String ipKey = ipAddress + "_" + System.currentTimeMillis() / 60000;  // Per minute
            pendingEvents.merge(ipKey, new LoginAttempt(username, ipAddress, false),
                (existing, newEvent) -> {
                    existing.count++;
                    return existing;
                });
        });
    }

    /**
     * Logs a registration event.
     *
     * @param username Username of new account
     * @param email Email of new account
     * @param ipAddress Client IP address
     */
    public void logRegistration(String username, String email, String ipAddress) {
        String requestId = RequestContextHolder.getRequestId();
        executor.submit(() -> {
            auditLogger.info("REGISTRATION username={} email={} ip={} request_id={}",
                username, email, ipAddress, requestId);
            logger.debug("Registration audit logged: username={}", username);
        });
    }

    /**
     * Logs a rate limit triggered event.
     *
     * @param ipAddress Client IP address
     * @param attemptCount Number of failed attempts
     */
    public void logRateLimitTriggered(String ipAddress, int attemptCount) {
        String requestId = RequestContextHolder.getRequestId();
        executor.submit(() -> {
            auditLogger.warn("RATE_LIMIT_TRIGGERED ip={} attempts={} request_id={}",
                ipAddress, attemptCount, requestId);
            logger.warn("Rate limit triggered for IP: {} (attempts: {})", ipAddress, attemptCount);
        });
    }

    /**
     * Logs an account suspension event.
     *
     * @param userId User ID
     * @param username Username
     * @param reason Reason for suspension
     */
    public void logAccountSuspension(String userId, String username, String reason) {
        String requestId = RequestContextHolder.getRequestId();
        executor.submit(() -> {
            auditLogger.warn("ACCOUNT_SUSPENDED user_id={} username={} reason={} request_id={}",
                userId, username, reason, requestId);
            logger.warn("Account suspended: user={} reason={}", username, reason);
        });
    }

    /**
     * Returns failed login attempts tracking (for rate limiting).
     *
     * @param ipAddress IP to check
     * @return Number of recent failed attempts
     */
    public int getFailedAttempts(String ipAddress) {
        return pendingEvents.values().stream()
            .filter(e -> e.ipAddress.equals(ipAddress) && !e.success)
            .mapToInt(e -> e.count)
            .sum();
    }

    /**
     * Shuts down the audit logging executor (should be called on app shutdown).
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            logger.info("AuditLoggingService shut down");
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Inner class to track login attempts.
     */
    private static class LoginAttempt {
        String username;
        String ipAddress;
        boolean success;
        int count;
        Instant timestamp;

        LoginAttempt(String username, String ipAddress, boolean success) {
            this.username = username;
            this.ipAddress = ipAddress;
            this.success = success;
            this.count = 1;
            this.timestamp = Instant.now();
        }
    }
}
