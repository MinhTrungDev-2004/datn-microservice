package com.datn.moneyai.models.entities.enums;

public enum CategoryType {
    THU("Thu Nhập"),
    CHI("Chi Tiêu");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}