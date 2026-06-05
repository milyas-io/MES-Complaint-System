package com.mes.dao;

import com.mes.model.*;
import java.sql.*;
import java.util.Optional;

/**
 * Traceability:
 * - FR-1 / UC-1: authenticateUser (role-based login)
 * - UC-1 AF1.1: invalid credentials -> error message
 */
public class UserDAO {

    /**
     * Authenticates a user based on username and password.
     * @param username The username
     * @param rawPassword The plain-text password
     * @return Optional User if authentication is successful
     */
    public Optional<User> authenticateUser(String username, String rawPassword) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, name, email, role, status " +
                     "FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    // User not found
                    return Optional.empty();
                }

                String storedPassword = rs.getString("password_hash");
                
                // Direct comparison of plain text passwords (no hashing)
                boolean match = rawPassword.equals(storedPassword);

                if (!match) {
                    // Password does not match
                    return Optional.empty();
                }

                // Authentication successful, map to User object
                return Optional.of(mapResultSetToUser(rs));
            }
        }
    }

    /**
     * Checks if a username already exists in the database.
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { 
                return rs.next(); 
            }
        }
    }

    /**
     * Adds a new user to the system.
     * @param user The User object containing details
     * @param rawPassword The plain-text password for the new user
     * @return true if the user was added successfully
     */
    public boolean addUser(User user, String rawPassword) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, name, email, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            
            // Store plain text password (no hashing)
            pstmt.setString(2, rawPassword);
            
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole());
            pstmt.setString(6, "Available");
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a user from the system.
     * @param targetUserId The ID of the user to delete
     * @param activeAdminUserId The ID of the admin performing the deletion
     * @return true if deletion was successful
     */
    public boolean deleteUser(int targetUserId, int activeAdminUserId) throws SQLException {
        if (targetUserId == activeAdminUserId) {
            throw new SQLException("Cannot delete your own active Admin session.");
        }
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, targetUserId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a row from the users table to a User object.
     * @param rs The ResultSet pointing to the current row
     * @return A specific User object (Admin, Operator, or Engineer)
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String user = rs.getString("username");
        String pass = rs.getString("password_hash"); // plain text password
        String name = rs.getString("name");
        String email = rs.getString("email");
        String role = rs.getString("role");
        String dbStatus = rs.getString("status");

        switch (role) {
            case "Operator":
                return new Operator(id, user, pass, name, email);
            case "Engineer":
                Engineer.Status status = (dbStatus != null && dbStatus.equalsIgnoreCase("Busy"))
                        ? Engineer.Status.BUSY : Engineer.Status.AVAILABLE;
                return new Engineer(id, user, pass, name, email, status);
            case "Admin":
                return new Admin(id, user, pass, name, email);
            default:
                return new User(id, user, pass, name, email, role);
        }
    }
}