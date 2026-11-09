package com.bloodlink.dao;

import com.bloodlink.config.DatabaseConnection;
import com.bloodlink.model.Donor;
import com.bloodlink.model.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonorDAO {
    private static final String SELECT =
            "SELECT u.user_id, u.full_name, u.email, u.phone, u.created_at, d.donor_id, d.blood_group, "
          + "d.city, d.available, d.last_donation_date FROM donors d JOIN users u ON u.user_id = d.user_id ";

    private static Donor map(ResultSet rs) throws SQLException {
        Donor d = new Donor();
        d.setUserId(rs.getInt("user_id"));
        d.setFullName(rs.getString("full_name"));
        d.setEmail(rs.getString("email"));
        d.setPhone(rs.getString("phone"));
        d.setRole(Role.DONOR);
        Timestamp ts = rs.getTimestamp("created_at");
        d.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        d.setDonorId(rs.getInt("donor_id"));
        d.setBloodGroup(rs.getString("blood_group"));
        d.setCity(rs.getString("city"));
        d.setAvailable(rs.getBoolean("available"));
        Date ld = rs.getDate("last_donation_date");
        d.setLastDonationDate(ld == null ? null : ld.toLocalDate());
        return d;
    }

    public void insert(Connection c, int userId, Donor d) throws SQLException {
        String sql = "INSERT INTO donors (user_id, blood_group, city, available, last_donation_date) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, d.getBloodGroup());
            ps.setString(3, d.getCity());
            ps.setBoolean(4, d.isAvailable());
            ps.setDate(5, d.getLastDonationDate() == null ? null : Date.valueOf(d.getLastDonationDate()));
            ps.executeUpdate();
        }
    }

    public Donor findByUserId(int userId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE u.user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Donor> findAll() throws SQLException {
        List<Donor> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SELECT + "ORDER BY d.donor_id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Available donors matching blood group and city (case-insensitive). */
    public List<Donor> search(String bloodGroup, String city) throws SQLException {
        List<Donor> list = new ArrayList<>();
        String sql = SELECT + "WHERE d.blood_group = ? AND LOWER(d.city) = LOWER(?) AND d.available = TRUE "
                   + "ORDER BY u.full_name";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, bloodGroup);
            ps.setString(2, city);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public void updateProfile(int userId, String bloodGroup, String city, boolean available,
                              java.time.LocalDate lastDonation) throws SQLException {
        String sql = "UPDATE donors SET blood_group=?, city=?, available=?, last_donation_date=? WHERE user_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, bloodGroup);
            ps.setString(2, city);
            ps.setBoolean(3, available);
            ps.setDate(4, lastDonation == null ? null : Date.valueOf(lastDonation));
            ps.setInt(5, userId);
            ps.executeUpdate();
        }
    }

    public void updateAvailability(int userId, boolean available) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE donors SET available=? WHERE user_id=?")) {
            ps.setBoolean(1, available);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}
