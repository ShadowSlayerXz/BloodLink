package com.bloodlink.ui;

import com.bloodlink.model.Donor;
import com.bloodlink.model.User;
import com.bloodlink.service.DonorService;
import com.bloodlink.util.InputValidator;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;

public class DonorDashboard extends DashboardBase {
    private final DonorService service = new DonorService();
    private final JTextField name = new JTextField(22), phone = new JTextField(22), city = new JTextField(22),
            last = new JTextField(10);
    private final JComboBox<String> group = new JComboBox<>(InputValidator.BLOOD_GROUPS.toArray(new String[0]));
    private final JCheckBox available = new JCheckBox("Available to donate");

    public DonorDashboard(User user) {
        super(user, "Donor Dashboard");
        RequestsPanel mine = new RequestsPanel(user, false);
        tabs.addTab("My profile", profilePanel());
        tabs.addTab("Blood banks", new BloodBankPanel(user));
        tabs.addTab("Request blood", new RequestForm(user, mine::reload));
        tabs.addTab("My requests", mine);
        Async.run(this, () -> service.getProfile(user), this::fill);
    }

    private void fill(Donor d) {
        name.setText(d.getFullName()); phone.setText(d.getPhone()); city.setText(d.getCity());
        group.setSelectedItem(d.getBloodGroup()); available.setSelected(d.isAvailable());
        last.setText(d.getLastDonationDate() == null ? "" : d.getLastDonationDate().toString());
    }

    private JPanel profilePanel() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        UiKit.row(form, 0, "Full name", name);
        UiKit.row(form, 1, "Phone", phone);
        UiKit.row(form, 2, "Blood group", group);
        UiKit.row(form, 3, "City", city);
        UiKit.row(form, 4, "Last donation (YYYY-MM-DD, optional)", last);
        UiKit.row(form, 5, "", available);
        JButton save = new JButton("Save profile");
        save.addActionListener(e -> {
            LocalDate ld = null;
            try { if (!last.getText().trim().isEmpty()) ld = LocalDate.parse(last.getText().trim()); }
            catch (DateTimeParseException ex) { Async.error(this, "Date must be YYYY-MM-DD."); return; }
            LocalDate fld = ld;
            Async.run(this, () -> {
                service.updateProfile(user, name.getText(), phone.getText(), (String) group.getSelectedItem(),
                        city.getText(), available.isSelected(), fld);
                return Boolean.TRUE;
            }, ok -> Async.info(this, "Profile saved."));
        });
        p.add(UiKit.note("Your availability is self-reported. Keep it up to date; your name, phone, blood group "
                + "and city are visible to seekers who search."), BorderLayout.NORTH);
        p.add(form, BorderLayout.CENTER);
        p.add(UiKit.flow(save), BorderLayout.SOUTH);
        return p;
    }
}
