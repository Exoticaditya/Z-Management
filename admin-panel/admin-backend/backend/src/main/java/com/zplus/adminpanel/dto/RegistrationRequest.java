package com.zplus.adminpanel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for the simplified user registration request from the modern UI.
 */
@Getter
@Setter
public class RegistrationRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    @NotBlank(message = "Self ID is required")
    private String selfId;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    // This field will be sent from the frontend form (e.g., "CLIENT" or "EMPLOYEE")
    @NotBlank(message = "User type is required")
    private String userType;

    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDepartment() {
        return department;
    }

    public String getPassword() {
        return password;
    }
    
    public String getSelfId() {
        return selfId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getUserType() {
        return userType;
    }
}