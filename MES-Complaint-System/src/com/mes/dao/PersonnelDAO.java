package com.mes.dao;

import com.mes.model.Personnel;
import java.sql.*;
import java.util.Optional;

/**
 * Traceability:
 * - UC-4: Verify Personnel
 * - FR-2, FR-3, FR-4, FR-10: Personnel verification required before complaint logging/assignment/view
 */
public class PersonnelDAO {

    /**
     * Find personnel record by service number.
     * @param sn Service number string
     * @return Optional Personnel if found, empty otherwise
     */
    public Optional<Personnel> findByServiceNumber(String sn) throws SQLException {
        // IMPORTANT: Updated SQL to use 'personnel_rank' instead of 'rank'
        String sql = "SELECT service_number, personnel_rank, name, unit FROM personnel WHERE service_number = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sn);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Personnel(
                        rs.getString("service_number"),
                        rs.getString("personnel_rank"), // Map from the new column name
                        rs.getString("name"),
                        rs.getString("unit")
                ));
            }
        }
    }
}