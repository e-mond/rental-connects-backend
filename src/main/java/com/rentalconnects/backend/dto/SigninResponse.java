package com.rentalconnects.backend.dto;

/**
 * DTO for returning a response after a user sign-in.
 */
public class SigninResponse {

    private String accessToken;
    private String userId;
    private String customId;
    private String role;

    // Default constructor
    public SigninResponse() {}

    // Parameterized constructor
    public SigninResponse(String accessToken, String userId, String customId, String role) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.customId = customId;
        this.role = role;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}