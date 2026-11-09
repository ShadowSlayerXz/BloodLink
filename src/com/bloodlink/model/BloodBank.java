package com.bloodlink.model;

public class BloodBank {
    private int bankId;
    private String bankName;
    private String address;
    private String city;
    private String contactNumber;

    public BloodBank() {}
    public BloodBank(int bankId, String bankName, String address, String city, String contactNumber) {
        this.bankId = bankId; this.bankName = bankName; this.address = address;
        this.city = city; this.contactNumber = contactNumber;
    }
    public int getBankId() { return bankId; }
    public void setBankId(int v) { bankId = v; }
    public String getBankName() { return bankName; }
    public void setBankName(String v) { bankName = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { address = v; }
    public String getCity() { return city; }
    public void setCity(String v) { city = v; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String v) { contactNumber = v; }
    @Override public String toString() { return bankName + " (" + city + ")"; }
}
