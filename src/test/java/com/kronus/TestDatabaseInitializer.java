package com.kronus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test Database Initializer for in-memory SQLite setup.
 *
 * Provides fast database initialization for unit and integration tests
 * without external dependencies.
 */
public class TestDatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseInitializer.class);

    /**
     * Initializes an in-memory SQLite database for testing.
     *
     * @param connection The connection to initialize
     * @throws SQLException if initialization fails
     */
    public static void initializeTestDatabase(Connection connection) throws SQLException {
        logger.debug("Initializing test database");

        try (Statement stmt = connection.createStatement()) {
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16))))," +
                "username TEXT NOT NULL UNIQUE," +
                "email TEXT NOT NULL UNIQUE," +
                "password_hash TEXT NOT NULL," +
                "status TEXT NOT NULL DEFAULT 'ACTIVE'," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "last_login_at TIMESTAMP," +
                "failed_login_attempts INTEGER NOT NULL DEFAULT 0," +
                "failed_login_reset_at TIMESTAMP," +
                "CHECK (length(username) >= 3 AND length(username) <= 20)," +
                "CHECK (email LIKE '%@%.%')," +
                "CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED'))," +
                "CHECK (failed_login_attempts >= 0)" +
                ")");

            // Create sessions table
            stmt.execute("CREATE TABLE IF NOT EXISTS sessions (" +
                "token TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "expires_at TIMESTAMP NOT NULL," +
                "ip_address TEXT NOT NULL," +
                "user_agent TEXT," +
                "revoked_at TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                "CHECK (revoked_at IS NULL OR revoked_at >= created_at)," +
                "CHECK (expires_at > created_at)" +
                ")");

            // Create login_events table
            stmt.execute("CREATE TABLE IF NOT EXISTS login_events (" +
                "id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16))))," +
                "user_id TEXT REFERENCES users(id) ON DELETE CASCADE," +
                "username TEXT," +
                "ip_address TEXT NOT NULL," +
                "user_agent TEXT," +
                "success INTEGER NOT NULL DEFAULT 0," +
                "failure_reason TEXT," +
                "attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "duration_ms INTEGER," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "expires_at TIMESTAMP NOT NULL DEFAULT (datetime('now', '+90 days'))," +
                "CHECK (success IN (0, 1))" +
                ")");

            logger.debug("Test database tables created");
        }

        connection.commit();
    }

    /**
     * Clears all data from test database tables.
     *
     * @param connection The connection to use
     * @throws SQLException if clearing fails
     */
    public static void clearTestDatabase(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM sessions");
            stmt.execute("DELETE FROM login_events");
            stmt.execute("DELETE FROM users");
            logger.debug("Test database cleared");
        }
        connection.commit();
    }

    /**
     * Drops all test database tables.
     *
     * @param connection The connection to use
     * @throws SQLException if dropping fails
     */
    public static void dropTestDatabase(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS sessions");
            stmt.execute("DROP TABLE IF EXISTS login_events");
            stmt.execute("DROP TABLE IF EXISTS users");
            logger.debug("Test database tables dropped");
        }
        connection.commit();
    }
}
