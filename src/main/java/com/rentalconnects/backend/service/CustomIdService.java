package com.rentalconnects.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Year;
import java.time.LocalDate;
import java.util.Random;

/**
 * Service for generating unique custom IDs for users in the format:
 * RC/ROLE/YYMM/XXN
 * - RC: Application code
 * - ROLE: TN (Tenant) or LL (Landlord)
 * - YYMM: Last two digits of the year and two-digit month (e.g., 2506)
 * - XXN: Two letters derived from name and one random number (e.g., AE9)
 * Example: RC/TN/2506/AE9
 */
@Service
public class CustomIdService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new Random();

    /**
     * Generates a unique custom ID for a user.
     *
     * @param role      The user's role (LANDLORD or TENANT).
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     * @return An 11-character custom ID string (e.g., RC/TN/2506/AE9).
     */
    public String generateCustomId(String role, String firstName, String lastName) {
        // Define constants
        String appCode = "RC";
        String roleCode = role.equals("LANDLORD") ? "LL" : "TN";
        String yearMonth = String.valueOf(Year.now().getValue()).substring(2) +
                          String.format("%02d", LocalDate.now().getMonthValue());

        // Generate suffix based on name
        String name = (firstName != null ? firstName : "") + (lastName != null ? lastName : "");
        String suffix = generateSuffix(name);

        // Combine components
        String customId = String.format("%s/%s/%s/%s", appCode, roleCode, yearMonth, suffix);

        // Ensure uniqueness
        Query query = new Query(Criteria.where("customId").is(customId));
        while (mongoTemplate.exists(query, "users")) {
            suffix = generateRandomSuffix();
            customId = String.format("%s/%s/%s/%s", appCode, roleCode, yearMonth, suffix);
            query = new Query(Criteria.where("customId").is(customId));
        }

        return customId;
    }

    /**
     * Generates a 3-character suffix (two letters and one number) based on the user's name.
     *
     * @param name The user's full name.
     * @return A 3-character suffix (e.g., AE9).
     */
    private String generateSuffix(String name) {
        if (name == null || name.trim().isEmpty()) {
            return generateRandomSuffix();
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(name.getBytes());
            int index1 = Math.abs(hashBytes[0] % LETTERS.length());
            int index2 = Math.abs(hashBytes[1] % LETTERS.length());
            int number = Math.abs(hashBytes[2] % 10);
            return "" + LETTERS.charAt(index1) + LETTERS.charAt(index2) + number;
        } catch (NoSuchAlgorithmException e) {
            // Fallback to initials or random
            String initials = name.replaceAll("[^A-Za-z]", "").toUpperCase();
            if (initials.length() >= 2) {
                return initials.substring(0, 2) + RANDOM.nextInt(10);
            }
            return generateRandomSuffix();
        }
    }

    /**
     * Generates a random 3-character suffix (two letters and one number).
     *
     * @return A random 3-character suffix (e.g., XY7).
     */
    private String generateRandomSuffix() {
        return "" + LETTERS.charAt(RANDOM.nextInt(LETTERS.length())) +
               LETTERS.charAt(RANDOM.nextInt(LETTERS.length())) +
               RANDOM.nextInt(10);
    }
}