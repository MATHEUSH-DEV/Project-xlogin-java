package com.kronus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Optional;

/**
 * Database Configuration Manager.
 *
 * Reads configuration from multiple sources per precedence:
 * 1. Environment variables (highest priority)
 * 2. application.properties
 * 3. Code defaults (lowest priority)
 *
 * Per Constitution Principle I (Cross-Platform Compatibility & Modular Architecture):
 * Configuration is externalized and environment-specific.
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private final String databasePath;
    private final String driver;
    private final int poolSize;
    private final int poolTimeoutSeconds;
    private final long leakDetectionThreshold;

    /**
     * Creates a DatabaseConfig from environment variables and properties.
     *
     * @param configLoader The config loader with properties
     */
    public DatabaseConfig(ConfigLoader configLoader) {
        // DATABASE_PATH: env var > properties > default
        this.databasePath = Optional
            .ofNullable(System.getenv("DATABASE_PATH"))
            .orElseGet(() -> configLoader.getString("database.path", "./kronus_local.db"));

        // DRIVER: typically static for SQLite
        this.driver = Optional
            .ofNullable(System.getenv("DATABASE_DRIVER"))
            .orElseGet(() -> configLoader.getString("database.driver", "org.sqlite.JDBC"));

        // Connection pool configuration
        this.poolSize = Optional
            .ofNullable(System.getenv("DATABASE_POOL_SIZE"))
            .map(Integer::parseInt)
            .orElseGet(() -> configLoader.getInt("database.pool.size", 10));

        this.poolTimeoutSeconds = Optional
            .ofNullable(System.getenv("DATABASE_POOL_TIMEOUT_SECONDS"))
            .map(Integer::parseInt)
            .orElseGet(() -> configLoader.getInt("database.pool.timeout.seconds", 30));

        this.leakDetectionThreshold = Optional
            .ofNullable(System.getenv("DATABASE_LEAK_DETECTION_THRESHOLD"))
            .map(Long::parseLong)
            .orElseGet(() -> configLoader.getLong("database.pool.leak.detection.threshold", 15000L));

        logConfiguration();
    }

    /**
     * Returns the JDBC connection URL for SQLite.
     *
     * @return JDBC URL in format: jdbc:sqlite:/path/to/db.sqlite
     */
    public String getJdbcUrl() {
        return "jdbc:sqlite:" + databasePath;
    }

    /**
     * Returns the JDBC driver class name.
     *
     * @return Driver class (e.g., org.sqlite.JDBC)
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Returns the database file path.
     *
     * @return Path to SQLite database file
     */
    public String getDatabasePath() {
        return databasePath;
    }

    /**
     * Returns the HikariCP connection pool size.
     *
     * @return Number of connections in pool
     */
    public int getPoolSize() {
        return poolSize;
    }

    /**
     * Returns the connection timeout in seconds.
     *
     * @return Timeout duration
     */
    public int getPoolTimeoutSeconds() {
        return poolTimeoutSeconds;
    }

    /**
     * Returns the leak detection threshold in milliseconds.
     *
     * @return Time threshold for detecting connection leaks
     */
    public long getLeakDetectionThreshold() {
        return leakDetectionThreshold;
    }

    /**
     * Validates database configuration.
     * Checks that database path is writable and driver class exists.
     *
     * @return true if configuration is valid, false otherwise
     * @throws IllegalArgumentException if configuration is invalid
     */
    public boolean validate() throws IllegalArgumentException {
        // Check database path
        File dbFile = new File(databasePath);
        File parentDir = dbFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            logger.warn("Database parent directory does not exist: {}", parentDir.getAbsolutePath());
            if (!parentDir.mkdirs()) {
                throw new IllegalArgumentException("Cannot create database directory: " + parentDir.getAbsolutePath());
            }
        }

        if (parentDir != null && !parentDir.canWrite()) {
            throw new IllegalArgumentException("Database directory is not writable: " + parentDir.getAbsolutePath());
        }

        // Check driver class
        try {
            Class.forName(driver);
            logger.debug("JDBC driver {} loaded successfully", driver);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("JDBC driver not found: " + driver, e);
        }

        // Validate pool settings
        if (poolSize <= 0 || poolSize > 100) {
            throw new IllegalArgumentException("Pool size must be between 1 and 100: " + poolSize);
        }

        if (poolTimeoutSeconds <= 0) {
            throw new IllegalArgumentException("Pool timeout must be positive: " + poolTimeoutSeconds);
        }

        logger.info("Database configuration validated successfully");
        return true;
    }

    /**
     * Logs the current configuration (without sensitive data).
     */
    private void logConfiguration() {
        logger.info("Database configuration:");
        logger.info("  Path: {}", databasePath);
        logger.info("  Driver: {}", driver);
        logger.info("  Pool Size: {}", poolSize);
        logger.info("  Pool Timeout: {} seconds", poolTimeoutSeconds);
        logger.info("  Leak Detection: {} ms", leakDetectionThreshold);
    }

    /**
     * Returns a descriptive string of the configuration.
     *
     * @return Configuration summary (safe for logging)
     */
    @Override
    public String toString() {
        return "DatabaseConfig{" +
            "path='" + databasePath + '\'' +
            ", driver='" + driver + '\'' +
            ", poolSize=" + poolSize +
            ", poolTimeout=" + poolTimeoutSeconds + "s" +
            '}';
    }
}
