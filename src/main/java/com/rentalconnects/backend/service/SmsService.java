package com.rentalconnects.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    public void sendPasswordResetSms(String phoneNumber, String resetLink) {
        // TODO: Integrate with SMS provider (e.g., Twilio)
        logger.info("Simulating sending password reset SMS to {} with link: {}", phoneNumber, resetLink);
        // Example: twilio.sendSms(phoneNumber, "Reset your password: " + resetLink);
    }
}