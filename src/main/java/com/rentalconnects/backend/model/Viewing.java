package com.rentalconnects.backend.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a viewing appointment scheduled by a tenant to visit a property in the RentalConnects application.
 * This model is mapped to the "viewings" collection in MongoDB.
 */
@Document(collection = "viewings")
@Getter
@Setter
public class Viewing {

    @Id
    private String id;

    @NotBlank(message = "Tenant ID cannot be blank")
    private String tenantId;

    @NotBlank(message = "Property ID cannot be blank")
    private String propertyId;

    @NotBlank(message = "Property name cannot be blank")
    private String propertyName;

    @NotNull(message = "Viewing date cannot be null")
    private LocalDateTime viewingDate;

    @NotBlank(message = "Status cannot be blank")
    private String status;

    private String notes;

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotNull(message = "Scheduled time cannot be null")
    private LocalDateTime scheduledTime;

    private boolean important;

    private static final AtomicInteger counter = new AtomicInteger(0);

    public Viewing() {
    }

    public Viewing(String tenantId, String propertyId, String propertyName, LocalDateTime viewingDate,
                   String status, String notes, String userId, LocalDateTime scheduledTime, boolean important) {
        this.tenantId = tenantId;
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.viewingDate = viewingDate;
        this.status = status != null ? status : "Scheduled";
        this.notes = notes;
        this.userId = userId;
        this.scheduledTime = scheduledTime != null ? scheduledTime : LocalDateTime.now();
        this.important = important;
        this.id = generateId(this.scheduledTime);
    }

    private String generateId(LocalDateTime timestamp) {
        String prefix = "RC";
        String typeShorthand = "SCHV";
        String date = timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String uniqueCode = "A" + counter.incrementAndGet();
        return String.format("%s/%s/%s/%s", prefix, typeShorthand, date, uniqueCode);
    }

    public boolean isUpcoming() { return viewingDate != null && viewingDate.isAfter(LocalDateTime.now()); }
    public boolean isCompleted() { return "Completed".equalsIgnoreCase(status); }

    // --- Manually added getters and setters for all used fields ---

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public LocalDateTime getViewingDate() {
        return viewingDate;
    }

    public void setViewingDate(LocalDateTime viewingDate) {
        this.viewingDate = viewingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    // --- End of manually added getters and setters ---

    @Override
    public String toString() {
        return "Viewing{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", propertyId='" + propertyId + '\'' +
                ", propertyName='" + propertyName + '\'' +
                ", viewingDate=" + viewingDate +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                ", userId='" + userId + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", important=" + important +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Viewing viewing = (Viewing) o;
        return id != null ? id.equals(viewing.id) : viewing.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}