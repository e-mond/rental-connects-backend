package com.rentalconnects.backend.dto;

public class RegisterResponse {
    private final String message;
    private final String userId;
    private final String role;
    private final String accessToken;

    public RegisterResponse(String message, String userId, String role, String accessToken) {
        this.message = message;
        this.userId = userId;
        this.role = role;
        this.accessToken = accessToken;
    }

    public RegisterResponse(String message, String userId, String role) {
        this(message, userId, role, null);
    }

    public String getMessage() { return message; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getAccessToken() { return accessToken; }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", role='" + role + '\'' +
                ", accessToken='" + (accessToken != null ? "[PROTECTED]" : null) + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterResponse that = (RegisterResponse) o;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;
        return accessToken != null ? accessToken.equals(that.accessToken) : that.accessToken == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
        return result;
    }
}