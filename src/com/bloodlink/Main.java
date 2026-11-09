package com.bloodlink;

import com.bloodlink.service.AuthService;
import com.bloodlink.ui.LoginFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            String generated = new AuthService().ensureAdminExists();
            if (generated != null) {
                System.out.println("Created administrator " + AuthService.DEFAULT_ADMIN_EMAIL
                        + " with generated password: " + generated);
                System.out.println("(Set BLOODLINK_ADMIN_PASSWORD before first run to choose your own.)");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage()
                    + "\nCheck BLOODLINK_DB_URL / BLOODLINK_DB_USER / BLOODLINK_DB_PASSWORD and that "
                    + "database/schema.sql has been run.", "BloodLink", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
