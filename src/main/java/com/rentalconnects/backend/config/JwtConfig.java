
package com.rentalconnects.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration class for JWT settings, using ConfigurationProperties to bind properties.
 */
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {

    private String secret;
    private long expiration;
    private long refreshExpiration; // Added for refresh token expiration

    /**
     * Gets the JWT secret.
     * @return The JWT secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Sets the JWT secret.
     * @param secret The JWT secret
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Gets the JWT expiration time in milliseconds.
     * @return The expiration time
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * Sets the JWT expiration time in milliseconds.
     * @param expiration The expiration time
     */
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    /**
     * Gets the refresh token expiration time in milliseconds.
     * @return The refresh token expiration time
     */
    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    /**
     * Sets the refresh token expiration time in milliseconds.
     * @param refreshExpiration The refresh token expiration time
     */
    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}
