package com.bloodlink.ui;

import com.bloodlink.model.User;

public class SeekerDashboard extends DashboardBase {
    public SeekerDashboard(User user) {
        super(user, "Seeker Dashboard");
        RequestsPanel history = new RequestsPanel(user, false);
        tabs.addTab("Search donors", new DonorSearchPanel(user));
        tabs.addTab("New request", new RequestForm(user, history::reload));
        tabs.addTab("My requests", history);
        tabs.addTab("Blood banks", new BloodBankPanel(user));
    }
}
