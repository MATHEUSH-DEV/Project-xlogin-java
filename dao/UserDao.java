package dao;

import model.User;
import model.UserStatus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private static final String URL = "jdbc:sqlite:kronus_local.db";

    public User findByUsername(String username) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String email = rs.getString("email");
                    String hash = rs.getString("password");
                    int statusInt = rs.getInt("status");
                    UserStatus status = UserStatus.fromInt(statusInt);
                    User u = new User(id, username, email, hash, status);
                    return u;
                }
            }
        }
        return null;
    }

    public boolean insert(User user) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        String sql = "INSERT INTO usuarios (username, password, email, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getEmail());
            pstmt.setInt(4, user.getStatus().getValue());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }
}
