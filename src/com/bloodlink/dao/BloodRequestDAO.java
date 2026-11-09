package com.bloodlink.dao;

import com.bloodlink.config.DatabaseConnection;
import com.bloodlink.model.BloodRequest;
import com.bloodlink.model.RequestStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BloodRequestDAO {
    private static BloodRequest map(ResultSet rs) throws SQLException {
        BloodRequest r = new BloodRequest();
        r.setRequestId(rs.getInt("request_id"));
        r.setRequesterUserId(rs.getInt("requester_user_id"));
        r.setPatientName(rs.getString("patient_name"));
        r.setBloodGroup(rs.getString("blood_group"));
        r.setUnitsRequired(rs.getInt("units_required"));
        r.setHospitalName(rs.getString("hospital_name"));
        r.setCity(rs.getString("city"));
        r.setContactPhone(rs.getString("contact_phone"));
        r.setUrgency(rs.getString("urgency"));
        r.setStatus(RequestStatus.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        r.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        return r;
    }

    public int insert(BloodRequest r) throws SQLException {
        String sql = "INSERT INTO blood_requests (requester_user_id, patient_name, blood_group, units_required, "
                   + "hospital_name, city, contact_phone, urgency, status) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getRequesterUserId());
            ps.setString(2, r.getPatientName());
            ps.setString(3, r.getBloodGroup());
            ps.setInt(4, r.getUnitsRequired());
            ps.setString(5, r.getHospitalName());
            ps.setString(6, r.getCity());
            ps.setString(7, r.getContactPhone());
            ps.setString(8, r.getUrgency());
            ps.setString(9, r.getStatus().name());
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getInt(1); }
        }
    }

    public BloodRequest findById(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM blood_requests WHERE request_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<BloodRequest> findByRequester(int userId) throws SQLException {
        List<BloodRequest> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM blood_requests WHERE requester_user_id = ? ORDER BY created_at DESC, request_id DESC")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<BloodRequest> findAll() throws SQLException {
        List<BloodRequest> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM blood_requests ORDER BY created_at DESC, request_id DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void updateStatus(int id, RequestStatus status) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE blood_requests SET status=? WHERE request_id=?")) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM blood_requests WHERE request_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
