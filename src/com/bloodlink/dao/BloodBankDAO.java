package com.bloodlink.dao;

import com.bloodlink.config.DatabaseConnection;
import com.bloodlink.model.BloodBank;
import com.bloodlink.model.BloodInventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BloodBankDAO {
    private static BloodBank map(ResultSet rs) throws SQLException {
        return new BloodBank(rs.getInt("bank_id"), rs.getString("bank_name"), rs.getString("address"),
                rs.getString("city"), rs.getString("contact_number"));
    }

    public List<BloodBank> findAll() throws SQLException {
        List<BloodBank> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM blood_banks ORDER BY city, bank_name")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<BloodBank> findByCity(String city) throws SQLException {
        List<BloodBank> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM blood_banks WHERE LOWER(city) = LOWER(?) ORDER BY bank_name")) {
            ps.setString(1, city);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public void insert(BloodBank b) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO blood_banks (bank_name, address, city, contact_number) VALUES (?,?,?,?)")) {
            ps.setString(1, b.getBankName()); ps.setString(2, b.getAddress());
            ps.setString(3, b.getCity()); ps.setString(4, b.getContactNumber());
            ps.executeUpdate();
        }
    }

    public void update(BloodBank b) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE blood_banks SET bank_name=?, address=?, city=?, contact_number=? WHERE bank_id=?")) {
            ps.setString(1, b.getBankName()); ps.setString(2, b.getAddress());
            ps.setString(3, b.getCity()); ps.setString(4, b.getContactNumber());
            ps.setInt(5, b.getBankId());
            ps.executeUpdate();
        }
    }

    public void delete(int bankId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM blood_banks WHERE bank_id=?")) {
            ps.setInt(1, bankId);
            ps.executeUpdate();
        }
    }

    public List<BloodInventory> findInventory(int bankId) throws SQLException {
        List<BloodInventory> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM blood_inventory WHERE bank_id = ? ORDER BY blood_group")) {
            ps.setInt(1, bankId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("updated_at");
                    list.add(new BloodInventory(rs.getInt("bank_id"), rs.getString("blood_group"),
                            rs.getInt("available_units"), rs.getString("source"),
                            ts == null ? null : ts.toLocalDateTime()));
                }
            }
        }
        return list;
    }

    public void upsertInventory(int bankId, String bloodGroup, int units, String source) throws SQLException {
        String sql = "INSERT INTO blood_inventory (bank_id, blood_group, available_units, source) VALUES (?,?,?,?) "
                   + "ON DUPLICATE KEY UPDATE available_units = VALUES(available_units), source = VALUES(source)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bankId); ps.setString(2, bloodGroup); ps.setInt(3, units); ps.setString(4, source);
            ps.executeUpdate();
        }
    }
}
