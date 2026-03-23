package dao;

import model.User;
import model.UserStatus;
import util.ConnectionPool;
import util.ConfigLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * UserDao: Data Access Object for User entity.
 * Implements CRUD operations using new schema: 'users' table with UUID PKs.
 * Uses ConnectionPool for connection management and parameterized queries.
 * 
 * Per Constitution Principle II: ALL queries are parameterized (NO SQL injection).
 */
public class UserDao {
    
    private static final ConfigLoader CONFIG = new ConfigLoader();
    private static final String DATABASE_URL = CONFIG.getString(
        "database.url", 
        "jdbc:sqlite:kronus_local.db"
    );

    /**
     * Find user by username.
     * @param username the username to search for
     * @return User object if found, null otherwise
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Find user by email.
     * @param email the email to search for
     * @return User object if found, null otherwise
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? LIMIT 1";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Find user by ID (UUID).
     * @param id the user UUID
     * @return User object if found, null otherwise
     */
    public User findById(String id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? LIMIT 1";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Create new user in database.
     * @param user the User object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insert(User user) throws SQLException {
        String sql = "INSERT INTO users (id, username, email, password_hash, status, " +
                    "created_at, updated_at, last_login_at, failed_login_attempts, failed_login_reset_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPasswordHash());
            pstmt.setString(5, user.getStatus().name());
            
            // Timestamps
            LocalDateTime now = LocalDateTime.now();
            pstmt.setTimestamp(6, Timestamp.valueOf(now));  // created_at
            pstmt.setTimestamp(7, Timestamp.valueOf(now));  // updated_at
            pstmt.setTimestamp(8, Timestamp.valueOf(now));  // last_login_at
            
            pstmt.setInt(9, 0);  // failed_login_attempts
            pstmt.setTimestamp(10, Timestamp.valueOf(now));  // failed_login_reset_at
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Update user in database.
     * @param user the User object with updated values
     * @return true if update successful, false otherwise
     */
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ?, password_hash = ?, " +
                    "status = ?, updated_at = ?, last_login_at = ?, failed_login_attempts = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getStatus().name());
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(6, user.getLastLoginAt() != null ? 
                Timestamp.valueOf(user.getLastLoginAt()) : Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(7, user.getFailedLoginAttempts());
            pstmt.setString(8, user.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete user from database.
     * @param userId the user UUID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean delete(String userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Map ResultSet row to User object.
     * @param rs the ResultSet positioned at the user row
     * @return User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User.Builder()
            .id(rs.getString("id"))
            .username(rs.getString("username"))
            .email(rs.getString("email"))
            .passwordHash(rs.getString("password_hash"))
            .status(UserStatus.valueOf(rs.getString("status")))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
            .lastLoginAt(rs.getTimestamp("last_login_at") != null ? 
                rs.getTimestamp("last_login_at").toLocalDateTime() : null)
            .failedLoginAttempts(rs.getInt("failed_login_attempts"))
            .failedLoginResetAt(rs.getTimestamp("failed_login_reset_at") != null ?
                rs.getTimestamp("failed_login_reset_at").toLocalDateTime() : null)
            .build();
    }
}
