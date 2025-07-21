package com.rentalconnects.backend.service;

import com.rentalconnects.backend.model.Activity;
import java.util.List;

public interface ActivityService {

    void logActivity(String tenantId, String landlordId, String type, String message, String entityId, String propertyId);

    Activity createViewingActivity(String tenantId, String viewingId, String userId, String type);

    Activity createViewingActivity(String tenantId, String viewingId, String userId);

    List<Activity> getRecentActivitiesForTenant(String tenantId);

    List<Activity> getRecentActivitiesForLandlord(String landlordId);
}