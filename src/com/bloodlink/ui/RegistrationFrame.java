package com.bloodlink.ui;

import com.bloodlink.model.Role;
import com.bloodlink.service.AuthService;
import com.bloodlink.util.InputValidator;
import java.awt.*;
import javax.swing.*;

public class RegistrationFrame extends JDialog {
    private final AuthService auth = new AuthService();
    private final JTextField name = new JTextField(22), email = new JTextField(22), phone = new JTextField(22);
    private final JPasswordField password = new JPasswordField(22);
    private final JComboBox<String> role = new JComboBox<>(new String[]{"SEEKER", "DONOR"});
    private final JComboBox<String> group = new JComboBox<>(InputValidator.BLOOD_GROUPS.toArray(new String[0]));
    private final JTextField city = new JTextField(22);
    private final JCheckBox available = new JCheckBox("Currently available to donate", true);

    public RegistrationFrame(Frame owner) {
        super(owner, "BloodLink – Register", true);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 12));
        UiKit.row(form, 0, "Full name", name);
        UiKit.row(form, 1, "Email", email);
        UiKit.row(form, 2, "Phone", phone);
        UiKit.row(form, 3, "Password (8+, letters & digits)", password);
        UiKit.row(form, 4, "Role", role);
        UiKit.row(form, 5, "Blood group (donor)", group);
        UiKit.row(form, 6, "City (donor)", city);
        UiKit.row(form, 7, "", available);
        Runnable toggle = () -> {
            boolean d = "DONOR".equals(role.getSelectedItem());
            group.setEnabled(d); city.setEnabled(d); available.setEnabled(d);
        };
        role.addActionListener(e -> toggle.run());
        toggle.run();

        JButton submit = new JButton("Create account");
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        submit.addActionListener(e -> {
            Role r = Role.valueOf((String) role.getSelectedItem());
            char[] pw = password.getPassword();
            submit.setEnabled(false);
            Async.run(this, () -> {
                auth.register(name.getText(), email.getText(), phone.getText(), pw, r,
                        (String) group.getSelectedItem(), city.getText(), available.isSelected());
                return Boolean.TRUE;
            }, ok -> {
                Async.info(this, "Registration successful. You can now log in.");
                dispose();
            });
            new javax.swing.Timer(1500, ev -> submit.setEnabled(true)) {{ setRepeats(false); start(); }};
        });
        add(form, BorderLayout.CENTER);
        add(UiKit.flow(submit, cancel), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }
}
