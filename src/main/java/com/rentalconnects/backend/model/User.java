package com.rentalconnects.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String fullName;
    @Indexed(unique = true)
    private String username;
    private String role;
    private String phoneNumber;
    private String language;
    private String timeZone;
    private String currency;
    private boolean emailNotifications;
    private boolean appNotifications;
    private boolean smsNotifications;
    private String businessName;
    private String propertyManagement;
    private String employment;
    private String rentalHistory;
    private String address;
    private String profilePic;
    private String publicKey;
    @Indexed(unique = true)
    private String customId;
    private String resetPasswordToken;
    private Long resetPasswordExpires;

    // Constructor
    public User() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { 
        this.firstName = firstName;
        updateFullName();
    }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { 
        this.lastName = lastName;
        updateFullName();
    }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    private void updateFullName() {
        this.fullName = (firstName != null ? firstName : "") + 
                        (firstName != null && lastName != null ? " " : "") + 
                        (lastName != null ? lastName : "");
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
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
    public String getPropertyManagementExperience() { return propertyManagement; }
    public void setPropertyManagementExperience(String propertyManagement) { this.propertyManagement = propertyManagement; }
    public String getEmployment() { return employment; }
    public void setEmployment(String employment) { this.employment = employment; }
    public String getRentalHistory() { return rentalHistory; }
    public void setRentalHistory(String rentalHistory) { this.rentalHistory = rentalHistory; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    public String getCustomId() { return customId; }
    public void setCustomId(String customId) { this.customId = customId; }
    public String getResetPasswordToken() { return resetPasswordToken; }
    public void setResetPasswordToken(String resetPasswordToken) { this.resetPasswordToken = resetPasswordToken; }
    public Long getResetPasswordExpires() { return resetPasswordExpires; }
    public void setResetPasswordExpires(Long resetPasswordExpires) { this.resetPasswordExpires = resetPasswordExpires; }
}