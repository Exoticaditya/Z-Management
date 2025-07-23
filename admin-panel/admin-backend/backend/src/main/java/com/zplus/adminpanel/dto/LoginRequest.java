package com.zplus.adminpanel.dto;

public class LoginRequest {
    private String selfId;
    private String password;

    // Getters and Setters
    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}