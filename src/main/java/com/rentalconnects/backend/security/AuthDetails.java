package com.rentalconnects.backend.security;

/**
 * Custom details class to hold both userId and web authentication details.
 */
public class AuthDetails {
    private final String userId;
    private final Object webDetails;

    public AuthDetails(String userId, Object webDetails) {
        this.userId = userId;
        this.webDetails = webDetails;
    }

    public String getUserId() {
        return userId;
    }

    public Object getWebDetails() {
        return webDetails;
    }
}