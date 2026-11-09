package com.bloodlink.service;

import com.bloodlink.dao.BloodRequestDAO;
import com.bloodlink.model.BloodRequest;
import com.bloodlink.model.RequestStatus;
import com.bloodlink.model.User;
import com.bloodlink.util.InputValidator;
import java.sql.SQLException;
import java.util.List;

public class RequestService {
    private final BloodRequestDAO dao = new BloodRequestDAO();

    public int createRequest(User actor, String patient, String bloodGroup, String unitsText, String hospital,
                             String city, String contactPhone, String urgency) throws ServiceException {
        Access.requireLogin(actor);
        String err = InputValidator.firstError(InputValidator.required("Patient name", patient, 100),
                InputValidator.bloodGroup(bloodGroup), InputValidator.required("Hospital", hospital, 150),
                InputValidator.required("City", city, 100), InputValidator.phone(contactPhone),
                InputValidator.urgency(urgency));
        if (err != null) throw new ServiceException(err);
        int units;
        try { units = InputValidator.parseUnits(unitsText); }
        catch (IllegalArgumentException e) { throw new ServiceException(e.getMessage()); }

        BloodRequest r = new BloodRequest();
        r.setRequesterUserId(actor.getUserId());
        r.setPatientName(patient.trim()); r.setBloodGroup(bloodGroup); r.setUnitsRequired(units);
        r.setHospitalName(hospital.trim()); r.setCity(city.trim()); r.setContactPhone(contactPhone.trim());
        r.setUrgency(urgency); r.setStatus(RequestStatus.PENDING);
        try { return dao.insert(r); }
        catch (SQLException e) { throw new ServiceException("Could not save request (database error).", e); }
    }

    public List<BloodRequest> myRequests(User actor) throws ServiceException {
        Access.requireLogin(actor);
        try { return dao.findByRequester(actor.getUserId()); }
        catch (SQLException e) { throw new ServiceException("Could not load requests (database error).", e); }
    }

    public List<BloodRequest> allRequests(User actor) throws ServiceException {
        Access.requireAdmin(actor);
        try { return dao.findAll(); }
        catch (SQLException e) { throw new ServiceException("Could not load requests (database error).", e); }
    }

    /** Admins may apply any valid transition; requesters may only cancel their own open request. */
    public void updateStatus(User actor, int requestId, RequestStatus next) throws ServiceException {
        Access.requireLogin(actor);
        try {
            BloodRequest r = dao.findById(requestId);
            if (r == null) throw new ServiceException("Request not found.");
            boolean owner = r.getRequesterUserId() == actor.getUserId();
            if (!actor.isAdmin() && !(owner && next == RequestStatus.CANCELLED))
                throw new ServiceException("You are not allowed to make this change.");
            if (!r.getStatus().canMoveTo(next))
                throw new ServiceException("Cannot change status from " + r.getStatus() + " to " + next + ".");
            dao.updateStatus(requestId, next);
        } catch (SQLException e) {
            throw new ServiceException("Could not update status (database error).", e);
        }
    }

    public void delete(User actor, int requestId) throws ServiceException {
        Access.requireAdmin(actor);
        try { dao.delete(requestId); }
        catch (SQLException e) { throw new ServiceException("Could not delete request (database error).", e); }
    }
}
