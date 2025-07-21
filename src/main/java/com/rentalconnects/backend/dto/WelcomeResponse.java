
package com.rentalconnects.backend.dto;

/**
 * DTO for the response returned by the /api/auth/welcome endpoint.
 */
public class WelcomeResponse {
    private String userId;
    private String username;
    private String role;
    private String firstName;
    private String lastName;
    private String fullName;

    // Default constructor
    public WelcomeResponse() {}

    // Parameterized constructor
    public WelcomeResponse(String userId, String username, String role, String firstName, String lastName, String fullName) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
