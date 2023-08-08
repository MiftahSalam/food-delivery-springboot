package com.example.fooddeliverysystem.model;

public enum UserStatus {
    INACTIVE(0),
    ACTIVE(1),
    DELETED(2),
    BANNED(3);

    private final int value;

    private UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
