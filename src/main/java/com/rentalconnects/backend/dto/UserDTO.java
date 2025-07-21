package com.rentalconnects.backend.dto;

import com.rentalconnects.backend.model.User;

public class UserDTO {
    private String id;
    private String email;
    private String username;
    private String customId;
    private String role;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String profilePic;
    private String language;
    private String timeZone;
    private String currency;
    private boolean emailNotifications;
    private boolean appNotifications;
    private boolean smsNotifications;
    private String businessName;
    private String propertyManagement; // Aligned with User.java
    private String employment;
    private String rentalHistory;
    private String publicKey;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.customId = user.getCustomId();
        this.role = user.getRole();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFullName();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.profilePic = user.getProfilePic();
        this.language = user.getLanguage();
        this.timeZone = user.getTimeZone();
        this.currency = user.getCurrency();
        this.emailNotifications = user.isEmailNotifications();
        this.appNotifications = user.isAppNotifications();
        this.smsNotifications = user.isSmsNotifications();
        this.businessName = user.getBusinessName();
        this.propertyManagement = user.getPropertyManagementExperience();
        this.employment = user.getEmployment();
        this.rentalHistory = user.getRentalHistory();
        this.publicKey = user.getPublicKey();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; } // Ensure this exists
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCustomId() { return customId; }
    public void setCustomId(String customId) { this.customId = customId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }
    public boolean isAppNotifications() { return appNotifications; }
    public void setAppNotifications(boolean appNotifications) { this.appNotifications = appNotifications; }
    public boolean isSmsNotifications() { return smsNotifications; }
    public void setSmsNotifications(boolean smsNotifications) { this.smsNotifications = smsNotifications; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getPropertyManagement() { return propertyManagement; }
    public void setPropertyManagement(String propertyManagement) { this.propertyManagement = propertyManagement; }
    public String getEmployment() { return employment; }
    public void setEmployment(String employment) { this.employment = employment; }
    public String getRentalHistory() { return rentalHistory; }
    public void setRentalHistory(String rentalHistory) { this.rentalHistory = rentalHistory; }
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
}