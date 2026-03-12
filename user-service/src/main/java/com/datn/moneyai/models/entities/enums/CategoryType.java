package com.datn.moneyai.models.entities.enums;

public enum CategoryType {
    EXPENSE("Chi Tiêu"),
    INCOME("Thu Nhập");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}