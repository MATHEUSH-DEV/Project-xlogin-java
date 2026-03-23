package com.kronus.util;

import com.kronus.model.User;
import com.kronus.model.Session;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mock Data Factory for test fixtures.
 *
 * Creates consistent, reusable test data (users, sessions, etc.)
 * for unit and integration tests.
 */
public class MockDataFactory {
    private static final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Creates a test user with valid data.
     *
     * @return User object with test data
     */
    public User createTestUser() {
        int num = counter.incrementAndGet();
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("testuser_" + num);
        user.setEmail("testuser_" + num + "@example.com");
        user.setPasswordHash("$2b$12$dummy-hash-for-testing");
        user.setStatus("ACTIVE");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }

    /**
     * Creates a test user with specific username.
     *
     * @param username The test username
     * @return User object
     */
    public User createTestUser(String username) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPasswordHash("$2b$12$dummy-hash-for-testing");
        user.setStatus("ACTIVE");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }

    /**
     * Creates a test session for a user.
     *
     * @param userId The user ID
     * @return Session object
     */
    public Session createTestSession(String userId) {
        Session session = new Session();
        session.setToken("test-jwt-token-" + UUID.randomUUID());
        session.setUserId(userId);
        session.setCreatedAt(Instant.now());
        session.setExpiresAt(Instant.now().plusSeconds(3600));  // 1 hour
        session.setIpAddress("127.0.0.1");
        session.setUserAgent("Mozilla/5.0 (Test)");
        return session;
    }

    /**
     * Resets the counter for test isolation.
     */
    public static void resetCounter() {
        counter.set(0);
    }
}
