package com.bloodlink.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Small helpers for consistent screens. */
final class UiKit {
    static final String DISCLAIMER =
            "Search results show potential donors only (self-reported). Eligibility, compatibility and "
          + "transfusion suitability must be confirmed by qualified medical professionals.";

    private UiKit() {}

    static JTable table(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(24);
        t.setAutoCreateRowSorter(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return t;
    }

    static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    static JLabel note(String text) {
        JLabel l = new JLabel("<html><body style='width:640px'>" + text + "</body></html>");
        l.setForeground(new Color(110, 60, 20));
        l.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return l;
    }

    /** Adds label+field rows to a GridBagLayout panel. */
    static void row(JPanel p, int y, String label, JComponent field) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.gridy = y; g.gridx = 0; g.anchor = GridBagConstraints.EAST;
        p.add(new JLabel(label), g);
        g.gridx = 1; g.anchor = GridBagConstraints.WEST; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        p.add(field, g);
    }

    static JPanel flow(Component... cs) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Component c : cs) p.add(c);
        return p;
    }
}
