package com.rentalconnects.backend.migration;


import com.rentalconnects.backend.repository.ActivityRepository;
import com.rentalconnects.backend.repository.ViewingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ActivityMigration implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ActivityMigration.class);
    private final ActivityRepository activityRepository;
    private final ViewingRepository viewingRepository;

    public ActivityMigration(ActivityRepository activityRepository, ViewingRepository viewingRepository) {
        this.activityRepository = activityRepository;
        this.viewingRepository = viewingRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("Running Activity migration...");
        activityRepository.findAll().forEach(activity -> {
            final boolean[] updated = {false};
            if (activity.getPropertyId() == null && activity.getType().startsWith("VIEWING_")) {
                viewingRepository.findById(activity.getEntityId()).ifPresent(viewing -> {
                    activity.setPropertyId(viewing.getPropertyId());
                    updated[0] = true;
                });
            }
            if (activity.getType().equals("SCHEDULED_VIEWING") || 
                activity.getType().equals("VIEWING_RESCHEDULED") || 
                activity.getType().equals("VIEWING_CANCELLED")) {
                    viewingRepository.findById(activity.getEntityId()).ifPresent(viewing -> {
                        String propertyName = viewing.getPropertyName();
                        if (propertyName != null) {
                            String expectedMessage = switch (activity.getType()) {
                                case "SCHEDULED_VIEWING" -> "Scheduled viewing for " + propertyName;
                                case "VIEWING_RESCHEDULED" -> "Rescheduled viewing at " + propertyName;
                                case "VIEWING_CANCELLED" -> "Cancelled viewing for " + propertyName;
                                default -> activity.getMessage();
                            };
                        if (!expectedMessage.equals(activity.getMessage())) {
                            activity.setMessage(expectedMessage);
                            updated[0] = true;
                        }
                    }
                });
            }
            if (updated[0]) {
                activityRepository.save(activity);
                logger.info("Updated activity {} with propertyId: {}, message: {}", 
                    activity.getId(), activity.getPropertyId(), activity.getMessage());
            }
        });
        logger.info("Activity migration completed.");
    }
}