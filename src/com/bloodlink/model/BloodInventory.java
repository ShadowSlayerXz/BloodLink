package com.bloodlink.model;

import java.time.LocalDateTime;

public class BloodInventory {
    private final int bankId;
    private final String bloodGroup;
    private final int availableUnits;
    private final String source;
    private final LocalDateTime updatedAt;

    public BloodInventory(int bankId, String bloodGroup, int availableUnits, String source,
                          LocalDateTime updatedAt) {
        this.bankId = bankId; this.bloodGroup = bloodGroup; this.availableUnits = availableUnits;
        this.source = source; this.updatedAt = updatedAt;
    }
    public int getBankId() { return bankId; }
    public String getBloodGroup() { return bloodGroup; }
    public int getAvailableUnits() { return availableUnits; }
    public String getSource() { return source; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
