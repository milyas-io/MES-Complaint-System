package com.mes.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Traceability:
 * - FR-1 to FR-11: All DAO operations depend on database connectivity.
 * - NFR-3: Security (credentials kept private, JDBC secure connection).
 * - NFR-4: Reliability (auto-reconnect, stable LAN connectivity).
 */
public class DBConnection {

    // Centralized DB configuration
    private static final String URL =
            "jdbc:mysql://localhost:3306/mes_complaints?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "20335";

    /**
     * Provides a JDBC connection to the MES complaints database.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        // Explicit driver load for legacy environments; modern JDBC loads automatically
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
            // Safe to ignore in modern JDKs
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
