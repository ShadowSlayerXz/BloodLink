package com.bloodlink.service;

import com.bloodlink.model.User;

/** Central authorization checks, enforced in the service layer rather than by hiding buttons. */
final class Access {
    private Access() {}

    static void requireLogin(User actor) throws ServiceException {
        if (actor == null) throw new ServiceException("You must be logged in.");
    }

    static void requireAdmin(User actor) throws ServiceException {
        requireLogin(actor);
        if (!actor.isAdmin()) throw new ServiceException("Administrator access required.");
    }
}
