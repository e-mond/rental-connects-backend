package com.rentalconnects.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO for scheduling a maintenance request.
 */
public class ScheduleRequestDTO {

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;

    /**
     * Gets the ID of the maintenance request to schedule.
     * @return The request ID
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the ID of the maintenance request to schedule.
     * @param requestId The request ID
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the scheduled date and time for the maintenance.
     * @return The scheduled date
     */
    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    /**
     * Sets the scheduled date and time for the maintenance.
     * @param scheduledDate The scheduled date
     */
    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}