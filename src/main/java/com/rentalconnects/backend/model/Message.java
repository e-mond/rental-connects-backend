package com.rentalconnects.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Model class representing a Message entity in the RentalConnects application.
 * This model is mapped to the "messages" collection in MongoDB.
 */
@Document(collection = "messages")
public class Message {

    @Id
    private String id;
    private String senderId;
    private String recipientId;
    private String propertyId;
    private String encryptedContent; // E2EE message content
    private String encryptedKey; // E2EE symmetric key, encrypted with recipient's public key
    private String replyToId; // ID of the message being replied to
    private LocalDateTime createdAt;
    private boolean read;

    // ConstructorsK Constructor
    public Message() {
    }

    // Parameterized Constructor
    public Message(String senderId, String recipientId, String propertyId,
                    String encryptedContent, String encryptedKey, String replyToId) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.propertyId = propertyId;
        this.encryptedContent = encryptedContent;
        this.encryptedKey = encryptedKey;
        this.replyToId = replyToId;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(String replyToId) {
        this.replyToId = replyToId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}