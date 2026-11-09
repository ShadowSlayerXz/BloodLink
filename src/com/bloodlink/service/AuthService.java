package com.bloodlink.service;

import com.bloodlink.config.DatabaseConnection;
import com.bloodlink.dao.DonorDAO;
import com.bloodlink.dao.UserDAO;
import com.bloodlink.model.Donor;
import com.bloodlink.model.Role;
import com.bloodlink.model.User;
import com.bloodlink.util.InputValidator;
import com.bloodlink.util.PasswordHasher;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;

public class AuthService {
    public static final String DEFAULT_ADMIN_EMAIL = "admin@bloodlink.local";
    private final UserDAO userDAO = new UserDAO();
    private final DonorDAO donorDAO = new DonorDAO();

    /**
     * Registers a user. Public registration may only create SEEKER or DONOR accounts.
     * For DONOR, bloodGroup/city are required; user and donor rows are saved in one transaction.
     */
    public void register(String name, String email, String phone, char[] password, Role role,
                         String bloodGroup, String city, boolean available) throws ServiceException {
        if (role == null || role == Role.ADMIN) throw new ServiceException("Select Seeker or Donor.");
        String err = InputValidator.firstError(
                InputValidator.required("Full name", name, 100),
                InputValidator.email(email), InputValidator.phone(phone), InputValidator.password(password),
                role == Role.DONOR ? InputValidator.bloodGroup(bloodGroup) : null,
                role == Role.DONOR ? InputValidator.required("City", city, 100) : null);
        if (err != null) throw new ServiceException(err);
        createAccount(name.trim(), email.trim().toLowerCase(), phone.trim(), password, role,
                bloodGroup, city == null ? null : city.trim(), available);
    }

    private void createAccount(String name, String email, String phone, char[] password, Role role,
                               String bloodGroup, String city, boolean available) throws ServiceException {
        try {
            if (userDAO.findByEmail(email) != null) throw new ServiceException("Email is already registered.");
            User u = new User(0, name, email, phone, PasswordHasher.hash(password), role, null);
            try (Connection c = DatabaseConnection.getConnection()) {
                c.setAutoCommit(false);
                try {
                    int id = userDAO.insert(c, u);
                    if (role == Role.DONOR) {
                        Donor d = new Donor();
                        d.setBloodGroup(bloodGroup); d.setCity(city); d.setAvailable(available);
                        donorDAO.insert(c, id, d);
                    }
                    c.commit();
                } catch (SQLException e) {
                    c.rollback();
                    throw e;
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) throw new ServiceException("Email is already registered.", e);
            throw new ServiceException("Could not register: database error. Check the connection settings.", e);
        }
    }

    /** Returns the authenticated user; same message for unknown account and wrong password. */
    public User login(String email, char[] password) throws ServiceException {
        if (email == null || email.trim().isEmpty() || password == null || password.length == 0)
            throw new ServiceException("Enter email and password.");
        try {
            User u = userDAO.findByEmail(email.trim().toLowerCase());
            if (u == null || !PasswordHasher.verify(password, u.getPasswordHash()))
                throw new ServiceException("Invalid email or password.");
            return u;
        } catch (SQLException e) {
            throw new ServiceException("Cannot reach the database. Check connection settings.", e);
        }
    }

    /**
     * Creates the first administrator if none exists. Password comes from BLOODLINK_ADMIN_PASSWORD,
     * otherwise a random one is generated. Returns the generated password, or null if nothing was generated.
     */
    public String ensureAdminExists() throws ServiceException {
        try {
            if (userDAO.countByRole(Role.ADMIN) > 0) return null;
            String pw = System.getenv("BLOODLINK_ADMIN_PASSWORD");
            boolean generated = pw == null || pw.isEmpty();
            if (generated) {
                byte[] b = new byte[9];
                new SecureRandom().nextBytes(b);
                pw = Base64.getUrlEncoder().withoutPadding().encodeToString(b) + "1a";
            }
            createAccount("Administrator", DEFAULT_ADMIN_EMAIL, "0000000000", pw.toCharArray(),
                    Role.ADMIN, null, null, false);
            return generated ? pw : null;
        } catch (SQLException e) {
            throw new ServiceException("Cannot reach the database. Check connection settings.", e);
        }
    }
}
