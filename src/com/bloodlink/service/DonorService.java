package com.bloodlink.service;

import com.bloodlink.dao.DonorDAO;
import com.bloodlink.dao.UserDAO;
import com.bloodlink.model.Donor;
import com.bloodlink.model.Role;
import com.bloodlink.model.User;
import com.bloodlink.util.InputValidator;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DonorService {
    private final DonorDAO donorDAO = new DonorDAO();
    private final UserDAO userDAO = new UserDAO();

    public Donor getProfile(User actor) throws ServiceException {
        Access.requireLogin(actor);
        if (actor.getRole() != Role.DONOR) throw new ServiceException("Only donors have a donor profile.");
        try {
            Donor d = donorDAO.findByUserId(actor.getUserId());
            if (d == null) throw new ServiceException("Donor profile not found.");
            return d;
        } catch (SQLException e) {
            throw new ServiceException("Could not load profile (database error).", e);
        }
    }

    public void updateProfile(User actor, String name, String phone, String bloodGroup, String city,
                              boolean available, LocalDate lastDonation) throws ServiceException {
        Access.requireLogin(actor);
        if (actor.getRole() != Role.DONOR) throw new ServiceException("Only donors can update a donor profile.");
        String err = InputValidator.firstError(InputValidator.required("Full name", name, 100),
                InputValidator.phone(phone), InputValidator.bloodGroup(bloodGroup),
                InputValidator.required("City", city, 100),
                (lastDonation != null && lastDonation.isAfter(LocalDate.now()))
                        ? "Last donation date cannot be in the future." : null);
        if (err != null) throw new ServiceException(err);
        try {
            userDAO.updateContact(actor.getUserId(), name.trim(), phone.trim());
            donorDAO.updateProfile(actor.getUserId(), bloodGroup, city.trim(), available, lastDonation);
            actor.setFullName(name.trim());
            actor.setPhone(phone.trim());
        } catch (SQLException e) {
            throw new ServiceException("Could not save profile (database error).", e);
        }
    }

    public void setAvailability(User actor, boolean available) throws ServiceException {
        Access.requireLogin(actor);
        if (actor.getRole() != Role.DONOR) throw new ServiceException("Only donors can set availability.");
        try { donorDAO.updateAvailability(actor.getUserId(), available); }
        catch (SQLException e) { throw new ServiceException("Could not update availability.", e); }
    }

    public List<Donor> listAll(User actor) throws ServiceException {
        Access.requireAdmin(actor);
        try { return donorDAO.findAll(); }
        catch (SQLException e) { throw new ServiceException("Could not load donors (database error).", e); }
    }
}
