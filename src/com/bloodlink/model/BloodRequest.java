package com.bloodlink.model;

import java.time.LocalDateTime;

public class BloodRequest {
    private int requestId;
    private int requesterUserId;
    private String patientName;
    private String bloodGroup;
    private int unitsRequired;
    private String hospitalName;
    private String city;
    private String contactPhone;
    private String urgency;
    private RequestStatus status = RequestStatus.PENDING;
    private LocalDateTime createdAt;

    public int getRequestId() { return requestId; }
    public void setRequestId(int v) { requestId = v; }
    public int getRequesterUserId() { return requesterUserId; }
    public void setRequesterUserId(int v) { requesterUserId = v; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String v) { patientName = v; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String v) { bloodGroup = v; }
    public int getUnitsRequired() { return unitsRequired; }
    public void setUnitsRequired(int v) { unitsRequired = v; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String v) { hospitalName = v; }
    public String getCity() { return city; }
    public void setCity(String v) { city = v; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String v) { contactPhone = v; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String v) { urgency = v; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus v) { status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { createdAt = v; }
}
