package com.bloodlink.model;

import java.time.LocalDate;

/** A donor "is a" user with extra donor-specific details. */
public class Donor extends User {
    private int donorId;
    private String bloodGroup;
    private String city;
    private boolean available;
    private LocalDate lastDonationDate;

    public Donor() {}

    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(LocalDate d) { this.lastDonationDate = d; }
}
