package com.bloodlink.ui;

import com.bloodlink.model.User;
import com.bloodlink.service.AuthService;
import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame {
    private final AuthService auth = new AuthService();
    private final JTextField email = new JTextField(22);
    private final JPasswordField password = new JPasswordField(22);
    private final JButton loginBtn = new JButton("Login");

    public LoginFrame() {
        super("BloodLink – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        UiKit.row(form, 0, "Email", email);
        UiKit.row(form, 1, "Password", password);

        JButton register = new JButton("Register");
        register.addActionListener(e -> new RegistrationFrame(this).setVisible(true));
        loginBtn.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(loginBtn);

        JLabel title = new JLabel("BloodLink – Emergency Blood Availability Network", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        add(title, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(UiKit.flow(loginBtn, register), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void doLogin() {
        String em = email.getText();
        char[] pw = password.getPassword();
        loginBtn.setEnabled(false);
        Async.run(this, () -> auth.login(em, pw), (User u) -> {
            loginBtn.setEnabled(true);
            password.setText("");
            java.util.Arrays.fill(pw, '\0');
            JFrame next;
            switch (u.getRole()) {
                case ADMIN: next = new AdminDashboard(u); break;
                case DONOR: next = new DonorDashboard(u); break;
                default: next = new SeekerDashboard(u);
            }
            next.setVisible(true);
            dispose();
        });
        // Re-enable after failures too.
        new javax.swing.Timer(1500, e -> loginBtn.setEnabled(true)) {{ setRepeats(false); start(); }};
    }
}
