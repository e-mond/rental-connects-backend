package com.rentalconnects.backend.service.impl;

import com.rentalconnects.backend.dto.NotificationDTO;
import com.rentalconnects.backend.model.Notification;
import com.rentalconnects.backend.repository.NotificationRepository;
import com.rentalconnects.backend.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the NotificationService interface for managing notifications.
 * Handles retrieval and sending of notifications for landlords and tenants.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;

    /**
     * Constructor for dependency injection of the NotificationRepository.
     *
     * @param notificationRepository The repository for accessing notification data.
     */
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
        logger.info("NotificationServiceImpl initialized");
    }

    /**
     * Retrieves a list of notifications for a specific landlord by their ID.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of Notification objects associated with the landlord.
     */
    @Override
    public List<Notification> getNotificationsByLandlordId(String landlordId) {
        logger.debug("Fetching notifications for landlord ID: {}", landlordId);
        return notificationRepository.findByLandlordId(landlordId);
    }

    /**
     * Retrieves a list of notifications for a specific landlord as DTOs.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of NotificationDTO objects associated with the landlord.
     */
    @Override
    public List<NotificationDTO> getNotificationsForLandlord(String landlordId) {
        logger.debug("Fetching notification DTOs for landlord ID: {}", landlordId);
        List<Notification> notifications = notificationRepository.findByLandlordId(landlordId);
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves a list of notifications for a specific tenant as DTOs.
     *
     * @param tenantId The ID of the tenant.
     * @return A list of NotificationDTO objects associated with the tenant.
     */
    @Override
    public List<NotificationDTO> getNotificationsForTenant(String tenantId) {
        logger.debug("Fetching notification DTOs for tenant ID: {}", tenantId);
        List<Notification> notifications = notificationRepository.findByTenantId(tenantId);
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Sends a notification by saving it to the database.
     *
     * @param notification The Notification object to send.
     * @return The saved Notification object.
     */
    @Override
    public Notification sendNotification(Notification notification) {
        logger.info("Sending notification to recipient ID: {}", notification.getRecipientId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Notification saved with ID: {}", savedNotification.getId());
        return savedNotification;
    }

    /**
     * Updates the status of a notification (e.g., mark as read/unread).
     *
     * @param id The ID of the notification.
     * @param landlordId The ID of the landlord.
     * @param isRead The read status to set.
     * @return The updated NotificationDTO.
     */
    @Override
    public NotificationDTO updateNotificationStatus(String id, String landlordId, boolean isRead) {
        logger.debug("Updating notification status for ID: {} and landlord ID: {}", id, landlordId);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
        if (!notification.getLandlordId().equals(landlordId)) {
            throw new SecurityException("Unauthorized access to notification");
        }
        notification.setIsRead(isRead);
        Notification saved = notificationRepository.save(notification);
        return convertToDTO(saved);
    }

    /**
     * Clears all notifications for a specific landlord.
     *
     * @param landlordId The ID of the landlord.
     */
    @Override
    public void clearNotifications(String landlordId) {
        logger.debug("Clearing notifications for landlord ID: {}", landlordId);
        List<Notification> notifications = notificationRepository.findByLandlordId(landlordId);
        notificationRepository.deleteAll(notifications);
    }

    /**
     * Converts a Notification entity to a NotificationDTO.
     *
     * @param notification The Notification entity to convert.
     * @return The converted NotificationDTO.
     */
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setLandlordId(notification.getLandlordId());
        dto.setTenantId(notification.getTenantId());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt().toString());
        return dto;
    }
}