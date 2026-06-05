package com.mes.dao;

import com.mes.model.Complaint;
import com.mes.model.Engineer;
import com.mes.model.Feedback;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Traceability:
 * - FR-2 / UC-2: logNewComplaint
 * - FR-4 / UC-12: getComplaintsByOperator, searchComplaintsForOperator
 * - FR-6 / UC-6: getComplaintsByEngineerId (alias getComplaintsByTechnicianId)
 * - FR-3 & FR-11 / UC-5: assignEngineer (assignment transaction)
 * - FR-7 / UC-6, UC-7: updateComplaintStatus with guard, resolution_date logic
 * - FR-5 / UC-7: submitFeedback (closes complaint and appends remarks)
 * - FR-10 / UC-13: getAllComplaints, searchComplaintsAdmin
 *
 * Schema: snake_case columns enforced.
 * Note: SRS uses "Technician"; current domain uses "Engineer". API maintains Engineer naming in code,
 * but SQL columns use assigned_to_engineer_id. If you standardize to Technician later, rename consistently.
 */
public class ComplaintDAO {

    private Complaint mapResultSetToComplaint(ResultSet rs) throws SQLException {
        // Status strings in DB: "Pending", "In Progress", "Resolved", "Closed"
        Complaint.Status status = Complaint.Status.valueOf(rs.getString("status").toUpperCase().replace(' ', '_'));
        Complaint.Priority pr = Complaint.Priority.valueOf(rs.getString("priority").toUpperCase());
        return new Complaint(
            rs.getInt("complaint_id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getTimestamp("creation_date"),
            pr,
            status,
            rs.getString("remarks"),
            rs.getTimestamp("resolution_date"),
            rs.getInt("logged_by_operator_id"),
            (Integer) rs.getObject("assigned_to_engineer_id")
        );
    }

    // FR-2 / UC-2: Log new complaint (Operator-assisted)
    public int logNewComplaint(Complaint c) throws SQLException {
        String sql = "INSERT INTO complaints (title, description, priority, status, logged_by_operator_id, creation_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getTitle());
            ps.setString(2, c.getDescription());
            // Priority string stored exactly as model returns (e.g., "High", "Medium", "Low")
            ps.setString(3, c.getPriorityString());
            ps.setString(4, "Pending");
            ps.setInt(5, c.getLoggedByOperatorId());
            ps.setTimestamp(6, new Timestamp(c.getCreationDate().getTime()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // FR-10 / UC-13: Admin view — list all complaints (latest first)
    public List<Complaint> getAllComplaints() throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT * FROM complaints ORDER BY complaint_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToComplaint(rs));
        }
        return list;
    }

    // FR-10 / UC-13: Admin search with optional filters (status, date range, engineer)
    public List<Complaint> searchComplaintsAdmin(String status, Date from, Date to, Integer engineerId) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT * FROM complaints WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isBlank()) {
            sb.append(" AND status = ?");
            params.add(status);
        }
        if (from != null) {
            sb.append(" AND creation_date >= ?");
            params.add(new Timestamp(from.getTime()));
        }
        if (to != null) {
            sb.append(" AND creation_date <= ?");
            params.add(new Timestamp(to.getTime()));
        }
        if (engineerId != null) {
            sb.append(" AND assigned_to_engineer_id = ?");
            params.add(engineerId);
        }
        sb.append(" ORDER BY complaint_id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) ps.setString(i + 1, (String) p);
                else if (p instanceof Timestamp) ps.setTimestamp(i + 1, (Timestamp) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Complaint> out = new ArrayList<>();
                while (rs.next()) out.add(mapResultSetToComplaint(rs));
                return out;
            }
        }
    }

    // FR-4 / UC-12: Operator view — list complaints for a specific operator
    public List<Complaint> getComplaintsByOperator(int operatorUserId) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE logged_by_operator_id = ? ORDER BY complaint_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operatorUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToComplaint(rs));
            }
        }
        return list;
    }

    // FR-4 / UC-12: Operator search with optional filters (status, date range)
    public List<Complaint> searchComplaintsForOperator(int operatorUserId, String status, Date from, Date to) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT * FROM complaints WHERE logged_by_operator_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(operatorUserId);

        if (status != null && !status.isBlank()) {
            sb.append(" AND status = ?");
            params.add(status);
        }
        if (from != null) {
            sb.append(" AND creation_date >= ?");
            params.add(new Timestamp(from.getTime()));
        }
        if (to != null) {
            sb.append(" AND creation_date <= ?");
            params.add(new Timestamp(to.getTime()));
        }
        sb.append(" ORDER BY complaint_id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else if (p instanceof String) ps.setString(i + 1, (String) p);
                else if (p instanceof Timestamp) ps.setTimestamp(i + 1, (Timestamp) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Complaint> out = new ArrayList<>();
                while (rs.next()) out.add(mapResultSetToComplaint(rs));
                return out;
            }
        }
    }

    // FR-10 / UC-8: Track complaint status (find by ID)
    public Optional<Complaint> findById(int id) throws SQLException {
        String sql = "SELECT * FROM complaints WHERE complaint_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToComplaint(rs)) : Optional.empty();
            }
        }
    }

    // FR-6 / UC-6: View assigned complaints (Engineer/Technician)
    public List<Complaint> getComplaintsByEngineerId(int engineerId) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE assigned_to_engineer_id = ? ORDER BY complaint_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, engineerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToComplaint(rs));
            }
        }
        return list;
    }

    // Alias for SRS terminology (Technician)
    public List<Complaint> getComplaintsByTechnicianId(int technicianUserId) throws SQLException {
        return getComplaintsByEngineerId(technicianUserId);
    }

    // FR-3 / FR-11 / UC-5: Assign Engineer (Atomic transaction)
    public boolean assignEngineer(int complaintId, int engineerId) throws SQLException {
        String updateComplaint = "UPDATE complaints SET assigned_to_engineer_id = ?, status = 'In Progress' " +
                                 "WHERE complaint_id = ? AND assigned_to_engineer_id IS NULL";
        String updateEngineer = "UPDATE users SET status = 'Busy' WHERE user_id = ? AND role = 'Engineer'";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(updateComplaint);
                 PreparedStatement p2 = conn.prepareStatement(updateEngineer)) {
                p1.setInt(1, engineerId);
                p1.setInt(2, complaintId);
                p2.setInt(1, engineerId);

                int rows1 = p1.executeUpdate();
                int rows2 = p2.executeUpdate();
                if (rows1 > 0 && rows2 > 0) { conn.commit(); return true; }
                conn.rollback(); return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // FR-7 / UC-6, UC-7: Update status with guard and resolution_date handling
    public boolean updateComplaintStatus(int cid, int eid, Complaint.Status status, String remarks) throws SQLException {
        // Removed StatusGuard check as it was removed from the system
        if ((status == Complaint.Status.RESOLVED || status == Complaint.Status.CLOSED)
            && (remarks == null || remarks.trim().isEmpty())) {
            throw new SQLException("Resolution notes required");
        }

        String toDb = switch (status) {
            case PENDING -> "Pending";
            case IN_PROGRESS -> "In Progress";
            case RESOLVED -> "Resolved";
            case CLOSED -> "Closed";
        };

        String sql = "UPDATE complaints SET status = ?, remarks = ?, " +
                     "resolution_date = CASE WHEN ? IN ('Resolved','Closed') THEN CURRENT_TIMESTAMP ELSE resolution_date END " +
                     "WHERE complaint_id = ? AND assigned_to_engineer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toDb);
            ps.setString(2, remarks);
            ps.setString(3, toDb);
            ps.setInt(4, cid);
            ps.setInt(5, eid);
            return ps.executeUpdate() > 0;
        }
    }

    // Removed fetchCurrentStatus method as it was only used with StatusGuard

    // FR-5 / UC-7: Record feedback and close complaint
    public boolean submitFeedback(Feedback fb) throws SQLException {
        String sqlFeedback = "INSERT INTO feedback (complaint_id, rating, comments, feedback_date) " +
                             "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        String sqlClose = "UPDATE complaints SET status = 'Closed', " +
                          "remarks = CONCAT(IFNULL(remarks,''), '\nRating: ', ?, ' | ', ?), " +
                          "resolution_date = IFNULL(resolution_date, CURRENT_TIMESTAMP) " +
                          "WHERE complaint_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pf = conn.prepareStatement(sqlFeedback);
                 PreparedStatement pc = conn.prepareStatement(sqlClose)) {

                pf.setInt(1, fb.getComplaintId());
                pf.setInt(2, fb.getRating());
                pf.setString(3, fb.getComments());

                pc.setInt(1, fb.getRating());
                pc.setString(2, fb.getComments());
                pc.setInt(3, fb.getComplaintId());

                int a = pf.executeUpdate();
                int b = pc.executeUpdate();
                if (a > 0 && b > 0) { conn.commit(); return true; }
                conn.rollback(); return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // FR-3 / FR-11 / UC-5: Engineers (Technicians) available list for assignment
    public List<Engineer> getAvailableEngineers() throws SQLException {
        List<Engineer> engineers = new ArrayList<>();
        String sql = "SELECT user_id, username, name, email, status FROM users " +
                     "WHERE role = 'Engineer' AND status = 'Available' AND user_id NOT IN (" +
                     "SELECT assigned_to_engineer_id FROM complaints " +
                     "WHERE status = 'In Progress' AND assigned_to_engineer_id IS NOT NULL)";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Engineer.Status st = "Busy".equalsIgnoreCase(rs.getString("status"))
                        ? Engineer.Status.BUSY
                        : Engineer.Status.AVAILABLE;
                engineers.add(new Engineer(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        "", // do not expose password
                        rs.getString("name"),
                        rs.getString("email"),
                        st
                ));
            }
        }
        return engineers;
    }
}