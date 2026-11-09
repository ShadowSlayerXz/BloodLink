package com.bloodlink.service;

import com.bloodlink.dao.BloodBankDAO;
import com.bloodlink.model.BloodBank;
import com.bloodlink.model.BloodInventory;
import com.bloodlink.model.User;
import com.bloodlink.util.InputValidator;
import java.sql.SQLException;
import java.util.List;

public class BloodBankService {
    private final BloodBankDAO dao = new BloodBankDAO();

    public List<BloodBank> list(User actor, String cityFilter) throws ServiceException {
        Access.requireLogin(actor);
        try {
            return (cityFilter == null || cityFilter.trim().isEmpty())
                    ? dao.findAll() : dao.findByCity(cityFilter.trim());
        } catch (SQLException e) { throw new ServiceException("Could not load blood banks (database error).", e); }
    }

    public List<BloodInventory> inventory(User actor, int bankId) throws ServiceException {
        Access.requireLogin(actor);
        try { return dao.findInventory(bankId); }
        catch (SQLException e) { throw new ServiceException("Could not load inventory (database error).", e); }
    }

    private void validate(BloodBank b) throws ServiceException {
        String err = InputValidator.firstError(InputValidator.required("Bank name", b.getBankName(), 150),
                InputValidator.required("Address", b.getAddress(), 255),
                InputValidator.required("City", b.getCity(), 100),
                InputValidator.phone(b.getContactNumber()));
        if (err != null) throw new ServiceException(err);
    }

    public void save(User actor, BloodBank b) throws ServiceException {
        Access.requireAdmin(actor);
        validate(b);
        try { if (b.getBankId() == 0) dao.insert(b); else dao.update(b); }
        catch (SQLException e) { throw new ServiceException("Could not save blood bank (database error).", e); }
    }

    public void delete(User actor, int bankId) throws ServiceException {
        Access.requireAdmin(actor);
        try { dao.delete(bankId); }
        catch (SQLException e) { throw new ServiceException("Could not delete blood bank (database error).", e); }
    }

    public void setInventory(User actor, int bankId, String bloodGroup, String unitsText, String source)
            throws ServiceException {
        Access.requireAdmin(actor);
        String err = InputValidator.firstError(InputValidator.bloodGroup(bloodGroup),
                InputValidator.required("Source", source, 150));
        if (err != null) throw new ServiceException(err);
        int units;
        try { units = Integer.parseInt(unitsText.trim()); }
        catch (NumberFormatException e) { throw new ServiceException("Units must be a whole number."); }
        if (units < 0) throw new ServiceException("Units cannot be negative.");
        try { dao.upsertInventory(bankId, bloodGroup, units, source.trim()); }
        catch (SQLException e) { throw new ServiceException("Could not save inventory (database error).", e); }
    }
}
