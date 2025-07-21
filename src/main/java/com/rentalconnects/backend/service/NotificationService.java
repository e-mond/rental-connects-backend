package com.rentalconnects.backend.service;

import com.rentalconnects.backend.dto.NotificationDTO;
import com.rentalconnects.backend.model.Notification;

import java.util.List;

/**
 * Interface for notification-related service operations in the RentalConnects application.
 * Provides methods to retrieve notifications for landlords and tenants and send notifications.
 */
public interface NotificationService {

    /**
     * Retrieves a list of notifications for a specific landlord by their ID.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of Notification objects associated with the landlord.
     */
    List<Notification> getNotificationsByLandlordId(String landlordId);

    /**
     * Retrieves a list of notifications for a specific landlord as DTOs.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of NotificationDTO objects associated with the landlord.
     */
    List<NotificationDTO> getNotificationsForLandlord(String landlordId);

    /**
     * Retrieves a list of notifications for a specific tenant as DTOs.
     *
     * @param tenantId The ID of the tenant.
     * @return A list of NotificationDTO objects associated with the tenant.
     */
    List<NotificationDTO> getNotificationsForTenant(String tenantId);

    /**
     * Sends a notification to a specified recipient and saves it to the database.
     *
     * @param notification The Notification object to send.
     * @return The saved Notification object.
     */
    Notification sendNotification(Notification notification);

    /**
     * Updates the status of a notification (e.g., mark as read/unread).
     *
     * @param id The ID of the notification.
     * @param landlordId The ID of the landlord.
     * @param isRead The read status to set.
     * @return The updated NotificationDTO.
     */
    NotificationDTO updateNotificationStatus(String id, String landlordId, boolean isRead);

    /**
     * Clears all notifications for a specific landlord.
     *
     * @param landlordId The ID of the landlord.
     */
    void clearNotifications(String landlordId);
}