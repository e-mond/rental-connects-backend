package com.rentalconnects.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendPasswordResetEmail(String email, String resetLink) {
        // TODO: Integrate with email provider (e.g., AWS SES, SendGrid)
        logger.info("Simulating sending password reset email to {} with link: {}", email, resetLink);
        // Example: sendGrid.sendEmail(email, "Password Reset", "Click here to reset your password: " + resetLink);
    }
}