package com.kronus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Configuration Loader with multi-layer precedence.
 *
 * Per Constitution Principle I (Cross-Platform Compatibility & Modular Architecture):
 * Configuration precedence:
 * 1. Environment variables (highest priority)
 * 2. application.properties file
 * 3. Code defaults (lowest priority)
 *
 * This enables environment-specific config without recompilation.
 */
public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    private final Properties properties;

    /**
     * Loads configuration from application.properties.
     *
     * @throws IOException if properties file cannot be read
     */
    public ConfigLoader() throws IOException {
        this.properties = new Properties();
        loadProperties();
        logConfiguration();
    }

    /**
     * Loads properties from application.properties on classpath.
     *
     * @throws IOException if file not found or read fails
     */
    private void loadProperties() throws IOException {
        try (InputStream is = ConfigLoader.class.getClassLoader()
            .getResourceAsStream("application.properties")) {
            if (is != null) {
                properties.load(is);
                logger.info("Loaded {} properties from application.properties", properties.size());
            } else {
                logger.warn("application.properties not found on classpath");
            }
        }
    }

    /**
     * Gets a String configuration value.
     *
     * Precedence: environment variable > properties > default
     *
     * @param key The configuration key
     * @param defaultValue Default if not found
     * @return Configuration value
     */
    public String getString(String key, String defaultValue) {
        // 1. Check environment variable (convert key to uppercase with underscores)
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        if (envValue != null) {
            logger.debug("Config {} from environment: {}", key, "***");
            return envValue;
        }

        // 2. Check properties file
        String propValue = properties.getProperty(key);
        if (propValue != null) {
            logger.debug("Config {} from properties: {}", key,
                key.contains("password") || key.contains("secret") ? "***" : propValue);
            return propValue;
        }

        // 3. Use default
        logger.debug("Config {} using default: {}", key,
            key.contains("password") || key.contains("secret") ? "***" : defaultValue);
        return defaultValue;
    }

    /**
     * Gets an Integer configuration value.
     *
     * @param key The configuration key
     * @param defaultValue Default if not found or invalid
     * @return Configuration value as Integer
     */
    public int getInt(String key, int defaultValue) {
        return Optional
            .ofNullable(System.getenv(key.toUpperCase().replace(".", "_")))
            .or(() -> Optional.ofNullable(properties.getProperty(key)))
            .map(v -> {
                try {
                    return Integer.parseInt(v);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid integer value for {}: {}", key, v);
                    return null;
                }
            })
            .orElse(defaultValue);
    }

    /**
     * Gets a Long configuration value.
     *
     * @param key The configuration key
     * @param defaultValue Default if not found or invalid
     * @return Configuration value as Long
     */
    public long getLong(String key, long defaultValue) {
        return Optional
            .ofNullable(System.getenv(key.toUpperCase().replace(".", "_")))
            .or(() -> Optional.ofNullable(properties.getProperty(key)))
            .map(v -> {
                try {
                    return Long.parseLong(v);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid long value for {}: {}", key, v);
                    return null;
                }
            })
            .orElse(defaultValue);
    }

    /**
     * Gets a Boolean configuration value.
     *
     * Treats "true", "yes", "1" as true (case-insensitive).
     *
     * @param key The configuration key
     * @param defaultValue Default if not found
     * @return Configuration value as Boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return Optional
            .ofNullable(System.getenv(key.toUpperCase().replace(".", "_")))
            .or(() -> Optional.ofNullable(properties.getProperty(key)))
            .map(v -> v.toLowerCase().matches("true|yes|1"))
            .orElse(defaultValue);
    }

    /**
     * Logs loaded configuration (excluding sensitive values).
     */
    private void logConfiguration() {
        logger.info("Configuration loaded from multiple sources");
        logger.debug("  Source priority: Environment variables > properties file > defaults");
        logger.debug("  Total properties: {}", properties.size());

        if (logger.isDebugEnabled()) {
            properties.forEach((key, value) -> {
                String safeValue = key.toString().contains("password") ||
                    key.toString().contains("secret") ? "***" : value.toString();
                logger.debug("  {} = {}", key, safeValue);
            });
        }
    }

    /**
     * Returns all loaded properties (sensitive keys masked).
     *
     * @return Properties object
     */
    public Properties getProperties() {
        return (Properties) properties.clone();
    }

    /**
     * Validates critical configuration values.
     *
     * @return true if all critical configs are valid
     * @throws IllegalArgumentException if any critical config is invalid
     */
    public boolean validate() throws IllegalArgumentException {
        String dbPath = getString("database.path", "");
        if (dbPath.isEmpty()) {
            throw new IllegalArgumentException("database.path must be configured");
        }

        int bcryptCost = getInt("bcrypt.cost", 0);
        if (bcryptCost < 12 || bcryptCost > 31) {
            throw new IllegalArgumentException("bcrypt.cost must be 12-31, got: " + bcryptCost);
        }

        String jwtKey = getString("jwt.signing.key", "");
        if (jwtKey.isEmpty() || jwtKey.length() < 32) {
            logger.warn("JWT signing key should be ≥32 characters for security");
        }

        logger.info("Configuration validation passed");
        return true;
    }
}
