package com.kronus.unit;

import com.kronus.util.MockDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit Tests for MockDataFactory.
 *
 * Verifies test fixtures are correctly generated.
 */
@DisplayName("MockDataFactory Unit Tests")
class MockDataFactoryTest {
    private MockDataFactory factory;

    @BeforeEach
    void setUp() {
        factory = new MockDataFactory();
    }

    @Test
    @DisplayName("Should create valid test user")
    void testCreateTestUser() {
        var user = factory.createTestUser();

        assertThat(user)
            .isNotNull();
        assertThat(user.getUsername())
            .isNotNull()
            .matches("^[a-z0-9_]+$");  // Valid username format
        assertThat(user.getEmail())
            .isNotNull()
            .contains("@");
    }

    @Test
    @DisplayName("Should create unique test users")
    void testCreateMultipleUniqueUsers() {
        var user1 = factory.createTestUser();
        var user2 = factory.createTestUser();

        assertThat(user1.getUsername())
            .isNotEqualTo(user2.getUsername());
    }
}
