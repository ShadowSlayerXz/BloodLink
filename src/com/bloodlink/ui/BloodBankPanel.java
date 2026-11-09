package com.bloodlink.ui;

import com.bloodlink.model.BloodBank;
import com.bloodlink.model.BloodInventory;
import com.bloodlink.model.User;
import com.bloodlink.service.BloodBankService;
import com.bloodlink.util.InputValidator;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Blood-bank directory with inventory (source and last-updated time shown). Admins get edit controls. */
public class BloodBankPanel extends JPanel {
    private final BloodBankService service = new BloodBankService();
    private final User user;
    private final DefaultTableModel banks = UiKit.model("ID", "Name", "Address", "City", "Contact");
    private final DefaultTableModel stock = UiKit.model("Blood group", "Units", "Source", "Last updated");
    private final JTable bankTable = UiKit.table(banks);
    private final JTextField cityFilter = new JTextField(12);

    public BloodBankPanel(User user) {
        super(new BorderLayout());
        this.user = user;
        JButton find = new JButton("Filter by city");
        find.addActionListener(e -> reload());
        JPanel bar = UiKit.flow(new JLabel("City"), cityFilter, find);
        if (user.isAdmin()) {
            JButton add = new JButton("Add");
            JButton edit = new JButton("Edit");
            JButton del = new JButton("Delete");
            JButton inv = new JButton("Set inventory");
            add.addActionListener(e -> edit(null));
            edit.addActionListener(e -> { BloodBank b = selected(); if (b != null) edit(b); });
            del.addActionListener(e -> delete());
            inv.addActionListener(e -> setInventory());
            bar.add(add); bar.add(edit); bar.add(del); bar.add(inv);
        }
        bankTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadStock();
        });
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(bankTable),
                new JScrollPane(UiKit.table(stock)));
        split.setResizeWeight(0.6);
        JPanel top = new JPanel(new BorderLayout());
        top.add(bar, BorderLayout.NORTH);
        top.add(UiKit.note("Inventory is only as reliable as its stated source and update time. "
                + "Always confirm availability directly with the blood bank."), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        reload();
    }

    private BloodBank selected() {
        int row = bankTable.getSelectedRow();
        if (row < 0) { Async.error(this, "Select a blood bank first."); return null; }
        int m = bankTable.convertRowIndexToModel(row);
        return new BloodBank((Integer) banks.getValueAt(m, 0), (String) banks.getValueAt(m, 1),
                (String) banks.getValueAt(m, 2), (String) banks.getValueAt(m, 3), (String) banks.getValueAt(m, 4));
    }

    private void reload() {
        Async.run(this, () -> service.list(user, cityFilter.getText()), (List<BloodBank> list) -> {
            banks.setRowCount(0);
            stock.setRowCount(0);
            for (BloodBank b : list)
                banks.addRow(new Object[]{b.getBankId(), b.getBankName(), b.getAddress(), b.getCity(),
                        b.getContactNumber()});
        });
    }

    private void loadStock() {
        int row = bankTable.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) banks.getValueAt(bankTable.convertRowIndexToModel(row), 0);
        Async.run(this, () -> service.inventory(user, id), (List<BloodInventory> list) -> {
            stock.setRowCount(0);
            for (BloodInventory i : list)
                stock.addRow(new Object[]{i.getBloodGroup(), i.getAvailableUnits(), i.getSource(),
                        i.getUpdatedAt() == null ? "" : i.getUpdatedAt().toString().replace('T', ' ')});
        });
    }

    private void edit(BloodBank existing) {
        JTextField n = new JTextField(existing == null ? "" : existing.getBankName(), 20);
        JTextField a = new JTextField(existing == null ? "" : existing.getAddress(), 20);
        JTextField c = new JTextField(existing == null ? "" : existing.getCity(), 20);
        JTextField p = new JTextField(existing == null ? "" : existing.getContactNumber(), 20);
        JPanel f = new JPanel(new GridBagLayout());
        UiKit.row(f, 0, "Name", n); UiKit.row(f, 1, "Address", a); UiKit.row(f, 2, "City", c);
        UiKit.row(f, 3, "Contact", p);
        if (JOptionPane.showConfirmDialog(this, f, existing == null ? "Add blood bank" : "Edit blood bank",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        BloodBank b = new BloodBank(existing == null ? 0 : existing.getBankId(), n.getText().trim(),
                a.getText().trim(), c.getText().trim(), p.getText().trim());
        Async.run(this, () -> { service.save(user, b); return Boolean.TRUE; }, ok -> reload());
    }

    private void delete() {
        BloodBank b = selected();
        if (b == null) return;
        if (JOptionPane.showConfirmDialog(this, "Delete " + b + " and its inventory?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Async.run(this, () -> { service.delete(user, b.getBankId()); return Boolean.TRUE; }, ok -> reload());
    }

    private void setInventory() {
        BloodBank b = selected();
        if (b == null) return;
        JComboBox<String> g = new JComboBox<>(InputValidator.BLOOD_GROUPS.toArray(new String[0]));
        JTextField u = new JTextField("0", 8);
        JTextField s = new JTextField("Entered by administrator", 20);
        JPanel f = new JPanel(new GridBagLayout());
        UiKit.row(f, 0, "Blood group", g); UiKit.row(f, 1, "Units", u); UiKit.row(f, 2, "Source", s);
        if (JOptionPane.showConfirmDialog(this, f, "Set inventory for " + b.getBankName(),
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        Async.run(this, () -> { service.setInventory(user, b.getBankId(), (String) g.getSelectedItem(),
                u.getText(), s.getText()); return Boolean.TRUE; }, ok -> loadStock());
    }
}
