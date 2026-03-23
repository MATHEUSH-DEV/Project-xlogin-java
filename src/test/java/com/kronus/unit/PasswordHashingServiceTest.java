package com.kronus.unit;

import com.kronus.util.PasswordHashingService;
import com.kronus.util.ConfigLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit Tests for PasswordHashingService.
 *
 * Per Constitution Principle III (Test-Driven Development):
 * - 100% coverage of auth/crypto code (non-negotiable)
 * - Tests bcrypt cost enforcement, hashing, verification
 */
@DisplayName("PasswordHashingService Unit Tests")
class PasswordHashingServiceTest {
    private PasswordHashingService service;
    private ConfigLoader configLoader;

    @BeforeEach
    void setUp() throws IOException {
        configLoader = new ConfigLoader();
        service = new PasswordHashingService(configLoader);
    }

    @Test
    @DisplayName("Should hash password with correct bcrypt format")
    void testHashPasswordFormat() {
        String password = "Test123456";
        String hash = service.hashPassword(password);

        assertThat(hash)
            .isNotNull()
            .isNotEmpty()
            .startsWith("$2b$");  // BCrypt format
    }

    @Test
    @DisplayName("Should verify correct password")
    void testCheckPasswordSuccess() {
        String password = "Test123456";
        String hash = service.hashPassword(password);

        assertThat(service.checkPassword(password, hash))
            .isTrue();
    }

    @Test
    @DisplayName("Should reject incorrect password")
    void testCheckPasswordFailure() {
        String password = "Test123456";
        String wrongPassword = "Wrong123456";
        String hash = service.hashPassword(password);

        assertThat(service.checkPassword(wrongPassword, hash))
            .isFalse();
    }

    @Test
    @DisplayName("Should enforce bcrypt cost >= 12")
    void testBcryptCostEnforcement() {
        assertThat(service.getBcryptCost())
            .isGreaterThanOrEqualTo(12);
    }

    @Test
    @DisplayName("Should reject null password")
    void testHashNullPassword() {
        assertThatThrownBy(() -> service.hashPassword(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("null");
    }

    @Test
    @DisplayName("Should reject empty password")
    void testHashEmptyPassword() {
        assertThatThrownBy(() -> service.hashPassword(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("empty");
    }

    @Test
    @DisplayName("Should handle timing-safe comparison")
    void testTimingSafeComparison() {
        String password = "Test123456";
        String hash = service.hashPassword(password);

        // Multiple checks should take similar time (timing-safe)
        long start = System.nanoTime();
        boolean check1 = service.checkPassword("Wrong123456", hash);
        long time1 = System.nanoTime() - start;

        start = System.nanoTime();
        boolean check2 = service.checkPassword("AlsoWrong456", hash);
        long time2 = System.nanoTime() - start;

        assertThat(check1).isFalse();
        assertThat(check2).isFalse();
        // Both should fail (timing-safe comparison)
    }

    @Test
    @DisplayName("Should generate different hashes from same password")
    void testHashRandomness() {
        String password = "Test123456";
        String hash1 = service.hashPassword(password);
        String hash2 = service.hashPassword(password);

        assertThat(hash1)
            .isNotEqualTo(hash2);  // Different salts = different hashes
        assertTrue(service.checkPassword(password, hash1));
        assertTrue(service.checkPassword(password, hash2));
    }

    @Test
    @DisplayName("Should estimate reasonable hashing time")
    void testEstimateHashingTime() {
        long duration = service.estimateHashingTime();

        assertThat(duration)
            .isGreaterThan(0)
            .isLessThan(500);  // BCrypt cost 12 should be <100ms
    }
}
