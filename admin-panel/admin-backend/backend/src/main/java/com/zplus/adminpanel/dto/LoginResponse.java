package com.zplus.adminpanel.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Using Lombok annotations to reduce boilerplate code. 
// Make sure you have the Lombok dependency in your pom.xml.
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    @JsonProperty("token")
    private String token;
    @JsonProperty("userType")
    private String userType;
    private String selfId;
    private String name;
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("message")
    private String message;

    // The constructor now accepts both the token and the user object.
    public LoginResponse(String token, String userType, String selfId, String name) {
        this.token = token;
        this.userType = userType;
        this.selfId = selfId;
        this.name = name;
    }

    public LoginResponse(boolean success, String message, String token, String userType, String selfId, String name) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.userType = userType;
        this.selfId = selfId;
        this.name = name;
    }

    // Explicit getter and setter methods
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}