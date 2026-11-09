package com.bloodlink.ui;

import com.bloodlink.model.Donor;
import com.bloodlink.model.User;
import com.bloodlink.service.SearchService;
import com.bloodlink.util.InputValidator;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DonorSearchPanel extends JPanel {
    private final SearchService service = new SearchService();
    private final DefaultTableModel model = UiKit.model("Name", "Phone", "Blood group", "City", "Last donation");
    private final JComboBox<String> group = new JComboBox<>(InputValidator.BLOOD_GROUPS.toArray(new String[0]));
    private final JTextField city = new JTextField(14);
    private final JLabel status = new JLabel(" ");

    public DonorSearchPanel(User user) {
        super(new BorderLayout());
        JButton search = new JButton("Search");
        search.addActionListener(e -> {
            search.setEnabled(false);
            status.setText("Searching…");
            Async.run(this, () -> service.findDonors(user, (String) group.getSelectedItem(), city.getText()), list -> {
                search.setEnabled(true);
                model.setRowCount(0);
                for (Donor d : list)
                    model.addRow(new Object[]{d.getFullName(), d.getPhone(), d.getBloodGroup(), d.getCity(),
                            d.getLastDonationDate() == null ? "—" : d.getLastDonationDate()});
                status.setText(list.isEmpty() ? "No available donors found." : list.size() + " potential donor(s).");
            });
            new javax.swing.Timer(1500, ev -> search.setEnabled(true)) {{ setRepeats(false); start(); }};
        });
        JPanel top = new JPanel(new BorderLayout());
        top.add(UiKit.flow(new JLabel("Blood group"), group, new JLabel("City"), city, search, status),
                BorderLayout.NORTH);
        top.add(UiKit.note(UiKit.DISCLAIMER + " Use donor contact details only for blood requests."),
                BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(UiKit.table(model)), BorderLayout.CENTER);
    }
}
