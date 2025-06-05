package com.gym.enums;

public enum RoleType {
    ADMIN,
    CLIENT,
    TRAINER;

    public static RoleType fromString(String role) {
        try {
            return RoleType.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role type: " + role);
        }
    }
}
