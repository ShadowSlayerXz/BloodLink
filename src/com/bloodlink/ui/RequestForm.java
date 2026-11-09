package com.bloodlink.ui;

import com.bloodlink.model.User;
import com.bloodlink.service.RequestService;
import com.bloodlink.util.InputValidator;
import java.awt.*;
import javax.swing.*;

/** Emergency blood request form. Calls {@code onSaved} after a successful submission. */
public class RequestForm extends JPanel {
    private final RequestService service = new RequestService();
    private final JTextField patient = new JTextField(22), units = new JTextField("1", 6),
            hospital = new JTextField(22), city = new JTextField(22), phone = new JTextField(22);
    private final JComboBox<String> group = new JComboBox<>(InputValidator.BLOOD_GROUPS.toArray(new String[0]));
    private final JComboBox<String> urgency = new JComboBox<>(InputValidator.URGENCIES.toArray(new String[0]));

    public RequestForm(User user, Runnable onSaved) {
        super(new BorderLayout());
        phone.setText(user.getPhone());
        urgency.setSelectedItem("HIGH");
        JPanel form = new JPanel(new GridBagLayout());
        UiKit.row(form, 0, "Patient name", patient);
        UiKit.row(form, 1, "Blood group", group);
        UiKit.row(form, 2, "Units required (1-" + InputValidator.MAX_UNITS + ")", units);
        UiKit.row(form, 3, "Hospital", hospital);
        UiKit.row(form, 4, "City", city);
        UiKit.row(form, 5, "Contact phone", phone);
        UiKit.row(form, 6, "Urgency", urgency);
        JButton submit = new JButton("Submit request");
        submit.addActionListener(e -> {
            submit.setEnabled(false);
            Async.run(this, () -> service.createRequest(user, patient.getText(), (String) group.getSelectedItem(),
                    units.getText(), hospital.getText(), city.getText(), phone.getText(),
                    (String) urgency.getSelectedItem()), id -> {
                submit.setEnabled(true);
                Async.info(this, "Request #" + id + " submitted with status PENDING.");
                patient.setText(""); hospital.setText(""); units.setText("1");
                if (onSaved != null) onSaved.run();
            });
            new javax.swing.Timer(1500, ev -> submit.setEnabled(true)) {{ setRepeats(false); start(); }};
        });
        add(UiKit.note("Requests are shared information only. They do not guarantee blood supply; "
                + "contact your hospital or a licensed blood bank for emergencies."), BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(UiKit.flow(submit), BorderLayout.SOUTH);
    }
}
