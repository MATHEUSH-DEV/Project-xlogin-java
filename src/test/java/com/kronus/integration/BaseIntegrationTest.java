package com.kronus.integration;

import com.kronus.util.TestDatabaseInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Base class for integration tests.
 *
 * Provides:
 * - In-memory SQLite database setup/teardown
 * - Connection pooling
 * - Clean state between tests
 */
public class BaseIntegrationTest {
    protected Connection connection;
    protected static final String JDBC_URL = "jdbc:sqlite::memory:";

    @BeforeEach
    void setUpDatabase() throws SQLException, ClassNotFoundException {
        // Load SQLite driver
        Class.forName("org.sqlite.JDBC");

        // Create in-memory database
        connection = DriverManager.getConnection(JDBC_URL);
        connection.setAutoCommit(false);

        // Initialize test schema
        TestDatabaseInitializer.initializeTestDatabase(connection);
    }

    @AfterEach
    void tearDownDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            TestDatabaseInitializer.clearTestDatabase(connection);
            connection.close();
        }
    }

    /**
     * Utility: Commits pending changes.
     */
    protected void commit() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
        }
    }

    /**
     * Utility: Rolls back pending changes.
     */
    protected void rollback() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
        }
    }
}
