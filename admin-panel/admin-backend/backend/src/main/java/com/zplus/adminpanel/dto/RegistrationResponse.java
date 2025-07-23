package com.zplus.adminpanel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;

    
    public RegistrationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.userId = null;
        this.email = null;
    }
    
    public RegistrationResponse(boolean success, String message, String userId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.email = null;
    }
    
    public RegistrationResponse(boolean success, String message, String userId, String email) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
     
