package com.mes.dao;

import java.sql.*;
import java.time.LocalDate;

/**
 * Traceability:
 * - FR-9 / UC-10: Generate reports (counts, status breakdown, performance rate)
 * - Supports Admin report generation by date range and status.
 */
public class ReportDAO {

    // Total complaints in date range
    public int countTotal(LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM complaints WHERE creation_date BETWEEN ? AND ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(end.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // Resolved complaints in date range
    public int countResolved(LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM complaints WHERE status = 'Resolved' AND resolution_date BETWEEN ? AND ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(end.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // Closed complaints in date range
    public int countClosed(LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM complaints WHERE status = 'Closed' AND resolution_date BETWEEN ? AND ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(end.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // Performance rate = (Resolved ÷ Total) × 100
    public double performanceRate(LocalDate start, LocalDate end) throws SQLException {
        int total = countTotal(start, end);
        if (total == 0) return 0.0;
        int resolved = countResolved(start, end);
        return (resolved * 100.0) / total;
    }

    // Complaints by technician in date range
    public int countByTechnician(int technicianId, LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM complaints WHERE assigned_to_engineer_id = ? AND creation_date BETWEEN ? AND ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            ps.setTimestamp(2, Timestamp.valueOf(start.atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(end.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // Complaints by status in date range
    public int countByStatus(String status, LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM complaints WHERE status = ? AND creation_date BETWEEN ? AND ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(start.atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(end.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
