
package com.rentalconnects.backend.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles unauthorized access attempts by returning a JSON error response for unauthenticated requests.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Responds with a 401 Unauthorized status and a JSON error message when authentication fails.
     *
     * @param request       The HTTP request that resulted in an authentication error.
     * @param response      The HTTP response to send the error message.
     * @param authException The exception that caused the authentication failure.
     * @throws IOException If an I/O error occurs while writing the response.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Set HTTP status to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Set response content type to JSON
        response.setContentType("application/json;charset=UTF-8");
        // Write JSON error message with the exception details
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
    }
}