package com.bloodlink.service;

import com.bloodlink.dao.UserDAO;
import com.bloodlink.model.User;
import java.sql.SQLException;
import java.util.List;

public class AdminService {
    private final UserDAO dao = new UserDAO();

    public List<User> listUsers(User actor) throws ServiceException {
        Access.requireAdmin(actor);
        try { return dao.findAll(); }
        catch (SQLException e) { throw new ServiceException("Could not load users (database error).", e); }
    }

    public void deleteUser(User actor, int userId) throws ServiceException {
        Access.requireAdmin(actor);
        if (actor.getUserId() == userId) throw new ServiceException("You cannot delete your own account.");
        try { dao.delete(userId); }
        catch (SQLException e) { throw new ServiceException("Could not delete user (database error).", e); }
    }
}
