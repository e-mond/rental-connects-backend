package com.rentalconnects.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rentalconnects.backend.model.Notification;

/**
 * Repository interface for managing {@link Notification} entities in the MongoDB database.
 * Provides methods to perform CRUD operations and custom queries for notification-related data.
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    /**
     * Retrieves a list of notifications for a specific landlord.
     *
     * @param landlordId The ID of the landlord to query notifications for
     * @return List of {@link Notification} entities for the landlord
     */
    List<Notification> findByLandlordId(String landlordId);

    /**
     * Retrieves a list of notifications for a specific tenant.
     *
     * @param tenantId The ID of the tenant to query notifications for
     * @return List of {@link Notification} entities for the tenant
     */
    List<Notification> findByTenantId(String tenantId);

    /**
     * Retrieves a list of notifications for a specific landlord with a given read status.
     *
     * @param landlordId The ID of the landlord to query notifications for
     * @param isRead The read status of the notifications (true for read, false for unread)
     * @return List of {@link Notification} entities for the landlord with the specified read status
     */
    List<Notification> findByLandlordIdAndIsRead(String landlordId, Boolean isRead);

    /**
     * Retrieves a list of notifications for a specific tenant with a given read status.
     *
     * @param tenantId The ID of the tenant to query notifications for
     * @param isRead The read status of the notifications (true for read, false for unread)
     * @return List of {@link Notification} entities for the tenant with the specified read status
     */
    List<Notification> findByTenantIdAndIsRead(String tenantId, Boolean isRead);
}