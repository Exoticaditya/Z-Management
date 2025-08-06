package com.zplus.adminpanel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO for contact inquiry form submission
 */
public class ContactInquiryRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String organization;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "City is required")
    private String city;

    private String address;

    private String businessDuration;
    private String projectTimeline;

    // List of service interests
    private java.util.List<String> serviceInterests;

    @NotBlank(message = "Business challenge is required")
    private String businessChallenge;

    @NotBlank(message = "Contact method is required")
    private String contactMethod;

    private String preferredTime;
    private String howHeard;

    // Constructors
    public ContactInquiryRequest() {}

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBusinessDuration() { return businessDuration; }
    public void setBusinessDuration(String businessDuration) { this.businessDuration = businessDuration; }
    public String getProjectTimeline() { return projectTimeline; }
    public void setProjectTimeline(String projectTimeline) { this.projectTimeline = projectTimeline; }
    public List<String> getServiceInterests() { return serviceInterests; }
    public void setServiceInterests(List<String> serviceInterests) { this.serviceInterests = serviceInterests; }
    public String getBusinessChallenge() { return businessChallenge; }
    public void setBusinessChallenge(String businessChallenge) { this.businessChallenge = businessChallenge; }
    public String getContactMethod() { return contactMethod; }
    public void setContactMethod(String contactMethod) { this.contactMethod = contactMethod; }
    public String getPreferredTime() { return preferredTime; }
    public void setPreferredTime(String preferredTime) { this.preferredTime = preferredTime; }
    public String getHowHeard() { return howHeard; }
    public void setHowHeard(String howHeard) { this.howHeard = howHeard; }

    public String getName() {
        return this.fullName; 
    }

    @Override
    public String toString() {
        return "ContactInquiryRequest{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", businessChallenge='" + businessChallenge + '\'' +
                '}';
    }
}