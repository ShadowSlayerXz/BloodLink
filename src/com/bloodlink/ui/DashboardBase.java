package com.bloodlink.ui;

import com.bloodlink.model.User;
import java.awt.*;
import javax.swing.*;

/** Shared window shell: header with user name, logout, and a tabbed body. */
abstract class DashboardBase extends JFrame {
    protected final User user;
    protected final JTabbedPane tabs = new JTabbedPane();

    DashboardBase(User user, String title) {
        super("BloodLink – " + title);
        this.user = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        header.add(new JLabel("Signed in: " + user.getFullName() + " (" + user.getRole() + ")"), BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        setSize(980, 620);
        setLocationRelativeTo(null);
    }
}
