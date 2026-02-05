package com.datn.moneyai.models.entities.enums;

public enum UserRole {
    USER("Người Dùng"),
    ADMIN("Quản Trị Viên");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

}
