package com.gym.enums;

public enum AccountCycleEnrollmentStatus {
    ACTIVE,
    PENDING,
    COMPLETED,
    CANCELLED;

    public static AccountCycleEnrollmentStatus fromString(String status) {
        for (AccountCycleEnrollmentStatus s : AccountCycleEnrollmentStatus.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
