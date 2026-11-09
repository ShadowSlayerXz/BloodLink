package com.bloodlink.ui;

import com.bloodlink.model.Donor;
import com.bloodlink.model.User;
import com.bloodlink.service.AdminService;
import com.bloodlink.service.DonorService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends DashboardBase {
    private final AdminService admin = new AdminService();
    private final DonorService donors = new DonorService();
    private final DefaultTableModel userModel = UiKit.model("ID", "Name", "Email", "Phone", "Role", "Created");
    private final DefaultTableModel donorModel =
            UiKit.model("Donor ID", "Name", "Phone", "Group", "City", "Available", "Last donation");
    private final JTable userTable = UiKit.table(userModel);

    public AdminDashboard(User user) {
        super(user, "Admin Dashboard");
        tabs.addTab("Users", usersPanel());
        tabs.addTab("Donors", donorsPanel());
        tabs.addTab("Requests", new RequestsPanel(user, true));
        tabs.addTab("Blood banks", new BloodBankPanel(user));
        tabs.addTab("Donor search", new DonorSearchPanel(user));
    }

    private JPanel usersPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JButton refresh = new JButton("Refresh"), del = new JButton("Delete user");
        refresh.addActionListener(e -> loadUsers());
        del.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) { Async.error(this, "Select a user first."); return; }
            int id = (Integer) userModel.getValueAt(userTable.convertRowIndexToModel(row), 0);
            if (JOptionPane.showConfirmDialog(this, "Delete user #" + id + " and all their data?", "Confirm",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            Async.run(this, () -> { admin.deleteUser(user, id); return Boolean.TRUE; }, ok -> loadUsers());
        });
        p.add(UiKit.flow(refresh, del), BorderLayout.NORTH);
        p.add(new JScrollPane(userTable), BorderLayout.CENTER);
        loadUsers();
        return p;
    }

    private void loadUsers() {
        Async.run(this, () -> admin.listUsers(user), (List<User> list) -> {
            userModel.setRowCount(0);
            for (User u : list)
                userModel.addRow(new Object[]{u.getUserId(), u.getFullName(), u.getEmail(), u.getPhone(),
                        u.getRole(), u.getCreatedAt() == null ? "" : u.getCreatedAt().toString().replace('T', ' ')});
        });
    }

    private JPanel donorsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadDonors());
        p.add(UiKit.flow(refresh), BorderLayout.NORTH);
        p.add(new JScrollPane(UiKit.table(donorModel)), BorderLayout.CENTER);
        loadDonors();
        return p;
    }

    private void loadDonors() {
        Async.run(this, () -> donors.listAll(user), (List<Donor> list) -> {
            donorModel.setRowCount(0);
            for (Donor d : list)
                donorModel.addRow(new Object[]{d.getDonorId(), d.getFullName(), d.getPhone(), d.getBloodGroup(),
                        d.getCity(), d.isAvailable() ? "Yes" : "No",
                        d.getLastDonationDate() == null ? "—" : d.getLastDonationDate()});
        });
    }
}
