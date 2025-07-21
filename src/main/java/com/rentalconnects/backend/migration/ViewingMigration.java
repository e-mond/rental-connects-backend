package com.rentalconnects.backend.migration;

// import com.rentalconnects.backend.model.Viewing;
import com.rentalconnects.backend.repository.ViewingRepository;
import com.rentalconnects.backend.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ViewingMigration implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ViewingMigration.class);
    private final ViewingRepository viewingRepository;
    private final PropertyService propertyService;

    public ViewingMigration(ViewingRepository viewingRepository, PropertyService propertyService) {
        this.viewingRepository = viewingRepository;
        this.propertyService = propertyService;
    }

    @Override
    public void run(String... args) {
        logger.info("Running Viewing migration to populate propertyName...");
        viewingRepository.findAll().forEach(viewing -> {
            if (viewing.getPropertyName() == null || viewing.getPropertyName().isBlank()) {
                String propertyName = propertyService.getPropertyNameById(viewing.getPropertyId());
                if (propertyName != null) {
                    viewing.setPropertyName(propertyName);
                    viewingRepository.save(viewing);
                    logger.info("Updated viewing {} with propertyName: {}", viewing.getId(), propertyName);
                } else {
                    logger.warn("No propertyName found for propertyId: {}", viewing.getPropertyId());
                }
            }
        });
        logger.info("Viewing migration completed.");
    }
}