package com.kronus.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * SQLite Connection Pool Manager using HikariCP.
 *
 * Manages database connections with automatic pooling, connection validation,
 * and leak detection per Constitution Principle II (Security-First Design).
 */
public class ConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    private static volatile ConnectionPool instance;

    private final HikariDataSource dataSource;
    private final DatabaseConfig config;

    /**
     * Initializes the connection pool.
     *
     * @param databaseConfig The database configuration
     * @throws SQLException if connection pool initialization fails
     */
    private ConnectionPool(DatabaseConfig databaseConfig) throws SQLException {
        this.config = databaseConfig;

        try {
            // Configure HikariCP connection pool
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(databaseConfig.getJdbcUrl());
            hikariConfig.setMaximumPoolSize(databaseConfig.getPoolSize());
            hikariConfig.setConnectionTimeout(databaseConfig.getPoolTimeoutSeconds() * 1000L);
            hikariConfig.setLeakDetectionThreshold(databaseConfig.getLeakDetectionThreshold());
            hikariConfig.setPoolName("KronusRiftPool");

            // SQLite-specific settings
            hikariConfig.setAutoCommit(false);  // Require explicit commits
            hikariConfig.setValidationQuery("SELECT 1");  // Connection validation

            this.dataSource = new HikariDataSource(hikariConfig);

            logger.info("Connection pool initialized: {}", databaseConfig);
        } catch (Exception e) {
            logger.error("Failed to initialize connection pool", e);
            throw new SQLException("Connection pool initialization failed", e);
        }
    }

    /**
     * Gets or creates the singleton connection pool instance.
     *
     * @param databaseConfig The database configuration
     * @return ConnectionPool instance
     * @throws SQLException if pool creation fails
     */
    public static ConnectionPool getInstance(DatabaseConfig databaseConfig) throws SQLException {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool(databaseConfig);
                }
            }
        }
        return instance;
    }

    /**
     * Gets a connection from the pool.
     *
     * @return A database connection
     * @throws SQLException if connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Connection pool is closed");
        }
        return dataSource.getConnection();
    }

    /**
     * Gets the underlying DataSource.
     *
     * @return HikariDataSource instance
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Closes the connection pool and releases all connections.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Connection pool closed");
        }
    }

    /**
     * Checks if the pool is healthy (can obtain a connection).
     *
     * @return true if pool is healthy, false otherwise
     */
    public boolean isHealthy() {
        try (Connection conn = getConnection()) {
            return conn != null;
        } catch (SQLException e) {
            logger.warn("Connection pool health check failed", e);
            return false;
        }
    }

    /**
     * Returns pool statistics for monitoring.
     *
     * @return String with pool stats
     */
    public String getPoolStats() {
        if (dataSource == null) {
            return "Pool not initialized";
        }

        HikariDataSource hikari = (HikariDataSource) dataSource;
        return String.format(
            "Active: %d, Idle: %d, Pending: %d, Size: %d",
            hikari.getHikariPoolMXBean().getActiveConnections(),
            hikari.getHikariPoolMXBean().getIdleConnections(),
            hikari.getHikariPoolMXBean().getPendingThreads(),
            hikari.getHikariPoolMXBean().getTotalConnections()
        );
    }

    /**
     * Resets the singleton instance (for testing only).
     */
    public static void resetInstance() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }
}
