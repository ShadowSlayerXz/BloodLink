package com.bloodlink.service;

/** User-presentable failure (validation, authorization, or database problem). */
public class ServiceException extends Exception {
    public ServiceException(String message) { super(message); }
    public ServiceException(String message, Throwable cause) { super(message, cause); }
}
