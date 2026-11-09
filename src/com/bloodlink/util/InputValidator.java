package com.bloodlink.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/** Reusable validation helpers. Each check returns an error message, or null when valid. */
public final class InputValidator {
    public static final List<String> BLOOD_GROUPS =
            Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
    public static final List<String> URGENCIES = Arrays.asList("LOW", "MEDIUM", "HIGH", "CRITICAL");
    public static final int MAX_UNITS = 20;

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{7,15}$");

    private InputValidator() {}

    public static String required(String label, String v, int maxLen) {
        if (v == null || v.trim().isEmpty()) return label + " is required.";
        if (v.trim().length() > maxLen) return label + " must be at most " + maxLen + " characters.";
        return null;
    }

    public static String email(String v) {
        String r = required("Email", v, 120);
        if (r != null) return r;
        return EMAIL.matcher(v.trim()).matches() ? null : "Email format is invalid.";
    }

    public static String phone(String v) {
        String r = required("Phone", v, 20);
        if (r != null) return r;
        return PHONE.matcher(v.trim()).matches() ? null : "Phone must be 7-15 digits (optional leading +).";
    }

    public static String password(char[] p) {
        if (p == null || p.length < 8) return "Password must be at least 8 characters.";
        boolean letter = false, digit = false;
        for (char c : p) { if (Character.isLetter(c)) letter = true; if (Character.isDigit(c)) digit = true; }
        return (letter && digit) ? null : "Password must contain letters and digits.";
    }

    public static String bloodGroup(String v) {
        return (v != null && BLOOD_GROUPS.contains(v)) ? null : "Select a valid blood group.";
    }

    public static String urgency(String v) {
        return (v != null && URGENCIES.contains(v)) ? null : "Select a valid urgency.";
    }

    /** Parses units; returns null result via {@link IllegalArgumentException} text on failure. */
    public static int parseUnits(String v) {
        int n;
        try { n = Integer.parseInt(v == null ? "" : v.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Units required must be a whole number."); }
        if (n <= 0 || n > MAX_UNITS)
            throw new IllegalArgumentException("Units required must be between 1 and " + MAX_UNITS + ".");
        return n;
    }

    /** Returns the first non-null message, or null if all are null. */
    public static String firstError(String... errors) {
        for (String e : errors) if (e != null) return e;
        return null;
    }
}
