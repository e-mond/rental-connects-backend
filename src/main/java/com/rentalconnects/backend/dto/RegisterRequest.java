
package com.rentalconnects.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for handling user registration requests.
 * Represents the data sent from the frontend during signup.
 */
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "TENANT|LANDLORD", message = "Role must be either TENANT or LANDLORD")
    private String role;

    @Pattern(regexp = "^(\\+\\d{1,3})?\\d{7,15}$", message = "Phone number must be valid (e.g., +233123456789)")
    private String phoneNumber;

    private String businessName; // Required for landlords, validated on backend

    private String propertyManagementExperience; // Optional for landlords

    private String employment; // Optional for tenants

    private String rentalHistory; // Optional for tenants

    // Default values for optional fields
    private String language = "English (UK)";
    private String timeZone = "Greenwich Mean Time (GMT)";
    private String currency = "GHS (₵)";
    private boolean emailNotifications = true;
    private boolean pushNotifications = true;
    private boolean smsNotifications = true;

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getPropertyManagementExperience() {
        return propertyManagementExperience;
    }

    public void setPropertyManagementExperience(String propertyManagementExperience) {
        this.propertyManagementExperience = propertyManagementExperience;
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getRentalHistory() {
        return rentalHistory;
    }

    public void setRentalHistory(String rentalHistory) {
        this.rentalHistory = rentalHistory;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public boolean isPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public boolean isSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }
}
