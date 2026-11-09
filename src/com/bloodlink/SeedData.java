package com.bloodlink;

import com.bloodlink.model.Role;
import com.bloodlink.model.User;
import com.bloodlink.service.AuthService;
import com.bloodlink.service.RequestService;
import com.bloodlink.service.ServiceException;

/** Loads SYNTHETIC demo accounts (all use password {@value #PASSWORD}). Safe to re-run. Run via ./seed.sh. */
public class SeedData {
    static final String PASSWORD = "Test1234pass";
    static final String[] CITIES = {"Pune", "Mumbai", "Delhi", "Bengaluru"};
    static final String[] GROUPS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    static final String[] FIRST = {"Aarav", "Diya", "Kabir", "Meera", "Rohan", "Isha", "Vikram", "Ananya",
            "Arjun", "Sara", "Neil", "Priya", "Dev", "Tara", "Yash", "Nisha"};

    public static void main(String[] args) throws Exception {
        AuthService auth = new AuthService();
        int donors = 0, seekers = 0;
        for (int i = 0; i < 24; i++) {
            String name = FIRST[i % FIRST.length] + " Donor" + (i + 1);
            donors += add(auth, name, "donor" + (i + 1) + "@example.test", 9000000100L + i, Role.DONOR,
                    GROUPS[i % 8], CITIES[(i / 2) % 4], i % 5 != 4);   // every 5th is unavailable
        }
        for (int i = 1; i <= 4; i++)
            seekers += add(auth, "Seeker " + i, "seeker" + i + "@example.test", 9000000200L + i, Role.SEEKER,
                    null, null, false);

        RequestService rs = new RequestService();
        User s1 = auth.login("seeker1@example.test", PASSWORD.toCharArray());
        User s2 = auth.login("seeker2@example.test", PASSWORD.toCharArray());
        if (rs.myRequests(s1).isEmpty()) {
            rs.createRequest(s1, "Patient One", "O+", "2", "Demo General Hospital", "Pune", "9000000201", "CRITICAL");
            rs.createRequest(s1, "Patient Two", "A-", "1", "Sample Care Centre", "Mumbai", "9000000201", "MEDIUM");
            rs.createRequest(s2, "Patient Three", "B+", "3", "Test City Hospital", "Delhi", "9000000202", "HIGH");
        }
        System.out.println("Added " + donors + " donors, " + seekers + " seekers (existing ones skipped).");
        System.out.println("All synthetic accounts use password: " + PASSWORD);
    }

    private static int add(AuthService auth, String name, String email, long phone, Role role, String group,
                           String city, boolean available) throws ServiceException {
        try {
            auth.register(name, email, String.valueOf(phone), PASSWORD.toCharArray(), role, group, city, available);
            return 1;
        } catch (ServiceException e) {
            if (e.getMessage().contains("already registered")) return 0;
            throw e;
        }
    }
}
