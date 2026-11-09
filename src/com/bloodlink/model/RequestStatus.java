package com.bloodlink.model;

public enum RequestStatus {
    PENDING, IN_PROGRESS, FULFILLED, CANCELLED;

    /** Allowed transitions: PENDING -> IN_PROGRESS/CANCELLED, IN_PROGRESS -> FULFILLED/CANCELLED. */
    public boolean canMoveTo(RequestStatus next) {
        switch (this) {
            case PENDING: return next == IN_PROGRESS || next == CANCELLED;
            case IN_PROGRESS: return next == FULFILLED || next == CANCELLED;
            default: return false;
        }
    }
}
