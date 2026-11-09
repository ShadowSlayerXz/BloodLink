package com.bloodlink.service;

import com.bloodlink.dao.DonorDAO;
import com.bloodlink.model.Donor;
import com.bloodlink.model.Role;
import com.bloodlink.model.User;
import com.bloodlink.util.InputValidator;
import java.sql.SQLException;
import java.util.List;

public class SearchService {
    private final DonorDAO donorDAO = new DonorDAO();

    /**
     * Finds potential donors (self-reported available). Contact details are returned only to
     * seekers and administrators; donors cannot browse other donors' phone numbers.
     */
    public List<Donor> findDonors(User actor, String bloodGroup, String city) throws ServiceException {
        Access.requireLogin(actor);
        if (actor.getRole() != Role.SEEKER && !actor.isAdmin())
            throw new ServiceException("Donor search is available to seekers and administrators.");
        String err = InputValidator.firstError(InputValidator.bloodGroup(bloodGroup),
                InputValidator.required("City", city, 100));
        if (err != null) throw new ServiceException(err);
        try { return donorDAO.search(bloodGroup, city.trim()); }
        catch (SQLException e) { throw new ServiceException("Search failed (database error).", e); }
    }
}
