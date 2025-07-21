package com.rentalconnects.backend.service.impl;

import com.rentalconnects.backend.model.*;
import com.rentalconnects.backend.repository.*;
import com.rentalconnects.backend.service.MaintenanceService;
import com.rentalconnects.backend.service.PropertyService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final ViewingRepository viewingRepository;
    private final RentalApplicationRepository rentalApplicationRepository;
    private final MaintenanceService maintenanceService;

    private static final String UPLOAD_DIR = "Uploads/images/";
    @Value("${image.base-url:http://localhost:8080/images/}")
    private String IMAGE_BASE_URL;

    public PropertyServiceImpl(
            PropertyRepository propertyRepository,
            ViewingRepository viewingRepository,
            RentalApplicationRepository rentalApplicationRepository,
            MaintenanceService maintenanceService) {
        this.propertyRepository = propertyRepository;
        this.viewingRepository = viewingRepository;
        this.rentalApplicationRepository = rentalApplicationRepository;
        this.maintenanceService = maintenanceService;
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                Files.setPosixFilePermissions(uploadPath, PosixFilePermissions.fromString("rwxr-xr-x"));
            }
        } catch (IOException e) {
            System.err.println("[PropertyServiceImpl] Error creating upload directory: " + e.getMessage());
            throw new RuntimeException("Failed to initialize upload directory", e);
        }
    }

    private void validateProperty(Property property) {
        if (property.getTitle() == null || property.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (property.getLocation() == null || property.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }
        if (property.getBedrooms() < 0) {
            throw new IllegalArgumentException("Bedrooms must be non-negative");
        }
        if (property.getBathrooms() < 0) {
            throw new IllegalArgumentException("Bathrooms must be non-negative");
        }
        if (property.getRent() <= 0) {
            throw new IllegalArgumentException("Rent must be positive");
        }
        if (!List.of("GHS", "USD", "EUR").contains(property.getCurrency())) {
            throw new IllegalArgumentException("Currency must be GHS, USD, or EUR");
        }
        if (!List.of("Active", "Vacant", "Under Maintenance").contains(property.getStatus())) {
            throw new IllegalArgumentException("Invalid status");
        }
        if (property.getPropertyType() == null || property.getPropertyType().trim().isEmpty()) {
            throw new IllegalArgumentException("Property type is required");
        }
    }

    private List<String> processImages(List<MultipartFile> images, Integer primaryImageIndex) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            return imageUrls;
        }
        int index = 0;
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                String originalFilename = image.getOriginalFilename();
                if (originalFilename != null && !originalFilename.trim().isEmpty()) {
                    String sanitizedFilename = UUID.randomUUID() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
                    try {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Thumbnails.of(image.getInputStream())
                                .size(1024, 1024)
                                .outputQuality(0.8)
                                .outputFormat("jpg")
                                .toOutputStream(outputStream);
                        byte[] imageData = outputStream.toByteArray();

                        Path filePath = Paths.get(UPLOAD_DIR, sanitizedFilename);
                        Files.write(filePath, imageData);
                        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                            Files.setPosixFilePermissions(filePath, PosixFilePermissions.fromString("rw-r--r--"));
                        }
                        String imageUrl = IMAGE_BASE_URL.endsWith("/") ? IMAGE_BASE_URL + sanitizedFilename : IMAGE_BASE_URL + "/" + sanitizedFilename;
                        imageUrls.add(imageUrl);
                        System.out.println("[PropertyServiceImpl] Processed and compressed image: " + sanitizedFilename);

                        if (primaryImageIndex != null && primaryImageIndex.equals(index)) {
                            System.out.println("[PropertyServiceImpl] Set primary image: " + sanitizedFilename);
                        }
                        index++;
                    } catch (IOException e) {
                        System.err.println("[PropertyServiceImpl] Error processing image: " + sanitizedFilename + ", Error: " + e.getMessage());
                        throw new IOException("Failed to process image: " + sanitizedFilename, e);
                    }
                }
            }
        }
        return imageUrls;
    }

    private void deleteImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        for (String imageUrl : imageUrls) {
            try {
                String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(UPLOAD_DIR, filename);
                Files.deleteIfExists(filePath);
                System.out.println("[PropertyServiceImpl] Deleted image: " + filename);
            } catch (IOException e) {
                System.err.println("[PropertyServiceImpl] Error deleting image: " + imageUrl + ", Error: " + e.getMessage());
            }
        }
    }

    @Override
    public Property createProperty(Property property) {
        try {
            if (property == null) {
                throw new IllegalArgumentException("Property cannot be null");
            }
            validateProperty(property);
            Property savedProperty = propertyRepository.save(property);
            System.out.println("[PropertyServiceImpl] Created property with ID: " + savedProperty.getId());
            return savedProperty;
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error creating property: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error creating property: " + e.getMessage());
            throw new RuntimeException("Failed to create property", e);
        }
    }

    @Override
    public Property createPropertyWithLandlordId(Property property, List<MultipartFile> images, String landlordId) {
        return createPropertyWithLandlordId(property, images, landlordId, null);
    }

    @Override
    public Property createPropertyWithLandlordId(Property property, List<MultipartFile> images, String landlordId, Integer primaryImageIndex) {
        try {
            if (property == null || landlordId == null) {
                throw new IllegalArgumentException("Property and landlord ID cannot be null");
            }
            validateProperty(property);
            property.setLandlordId(landlordId);
            List<String> imageUrls = processImages(images, primaryImageIndex);
            property.setImageUrls(imageUrls);
            property.setPrimaryImageUrl(imageUrls.isEmpty() ? null : imageUrls.get(
                    primaryImageIndex != null && primaryImageIndex >= 0 && primaryImageIndex < imageUrls.size() ? primaryImageIndex : 0));
            Property savedProperty = propertyRepository.save(property);
            System.out.println("[PropertyServiceImpl] Created property with ID: " + savedProperty.getId() + " for landlord: " + landlordId);
            return savedProperty;
        } catch (IOException e) {
            System.err.println("[PropertyServiceImpl] Error creating property due to IO issue: " + e.getMessage());
            throw new RuntimeException("Failed to create property due to IO error", e);
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error creating property for landlord ID: " + landlordId + ", Error: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error creating property for landlord ID: " + landlordId + ", Error: " + e.getMessage());
            throw new RuntimeException("Failed to create property", e);
        }
    }

    @Override
    public Property createProperty(Property property, List<MultipartFile> images, Integer primaryImageIndex) throws IOException {
        try {
            if (property == null) {
                throw new IllegalArgumentException("Property cannot be null");
            }
            validateProperty(property);
            List<String> imageUrls = processImages(images, primaryImageIndex);
            property.setImageUrls(imageUrls);
            property.setPrimaryImageUrl(imageUrls.isEmpty() ? null : imageUrls.get(
                    primaryImageIndex != null && primaryImageIndex >= 0 && primaryImageIndex < imageUrls.size() ? primaryImageIndex : 0));
            Property savedProperty = propertyRepository.save(property);
            System.out.println("[PropertyServiceImpl] Created property with ID: " + savedProperty.getId());
            return savedProperty;
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error creating property: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            System.err.println("[PropertyServiceImpl] Error creating property due to IO issue: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error creating property: " + e.getMessage());
            throw new RuntimeException("Failed to create property", e);
        }
    }

    @Override
    public Optional<Property> getPropertyById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Property ID cannot be null or empty");
            }
            Optional<Property> property = propertyRepository.findById(id);
            if (property.isPresent()) {
                System.out.println("[PropertyServiceImpl] Retrieved property with ID: " + id);
            } else {
                System.out.println("[PropertyServiceImpl] Property not found with ID: " + id);
            }
            return property;
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving property: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving property: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve property", e);
        }
    }

    @Override
    public List<Property> getAllActiveProperties() {
        try {
            List<Property> properties = propertyRepository.findByStatus("Active");
            System.out.println("[PropertyServiceImpl] Retrieved " + properties.size() + " active properties");
            return properties;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving active properties: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve active properties", e);
        }
    }

    @Override
    public List<Property> getAllProperties() {
        try {
            List<Property> properties = propertyRepository.findAll();
            System.out.println("[PropertyServiceImpl] Retrieved " + properties.size() + " properties");
            return properties;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving all properties: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve all properties", e);
        }
    }

    @Override
    public Property findById(String propertyId) {
        try {
            if (propertyId == null || propertyId.trim().isEmpty()) {
                throw new IllegalArgumentException("Property ID cannot be null or empty");
            }
            return propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + propertyId));
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error finding property: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error finding property: " + e.getMessage());
            throw new RuntimeException("Failed to find property", e);
        }
    }

    @Override
    public List<Property> findAllAvailable() {
        try {
            List<Property> properties = propertyRepository.findByStatusIn(List.of("Active", "Vacant"));
            System.out.println("[PropertyServiceImpl] Retrieved " + properties.size() + " available properties");
            return properties;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving available properties: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve available properties", e);
        }
    }

    @Override
    public List<Property> getPropertiesByLandlordId(String landlordId) {
        try {
            if (landlordId == null || landlordId.trim().isEmpty()) {
                throw new IllegalArgumentException("Landlord ID cannot be null or empty");
            }
            List<Property> properties = propertyRepository.findByLandlordId(landlordId);
            System.out.println("[PropertyServiceImpl] Retrieved " + properties.size() + " properties for landlord: " + landlordId);
            return properties;
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving properties for landlord: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving properties for landlord: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve properties", e);
        }
    }

    @Override
    public Property updatePropertyWithLandlordId(Property property, List<MultipartFile> images, String landlordId) {
        try {
            if (property == null || landlordId == null || property.getId() == null) {
                throw new IllegalArgumentException("Property, landlord ID, and property ID cannot be null");
            }
            Property existingProperty = propertyRepository.findById(property.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + property.getId()));
            if (!existingProperty.getLandlordId().equals(landlordId)) {
                throw new IllegalArgumentException("Unauthorized to update this property");
            }
            validateProperty(property);
            List<String> newImageUrls = processImages(images, null);
            List<String> existingImageUrls = existingProperty.getImageUrls() != null ? existingProperty.getImageUrls() : new ArrayList<>();
            existingImageUrls.addAll(newImageUrls);
            property.setImageUrls(existingImageUrls);
            property.setPrimaryImageUrl(existingImageUrls.isEmpty() ? null : existingImageUrls.get(0));
            property.setLandlordId(landlordId);
            Property updatedProperty = propertyRepository.save(property);
            System.out.println("[PropertyServiceImpl] Updated property with ID: " + updatedProperty.getId());
            return updatedProperty;
        } catch (IOException e) {
            System.err.println("[PropertyServiceImpl] Error updating property due to IO issue: " + e.getMessage());
            throw new RuntimeException("Failed to update property due to IO error", e);
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error updating property: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error updating property: " + e.getMessage());
            throw new RuntimeException("Failed to update property", e);
        }
    }

    @Override
    public Property updateProperty(Property property, List<MultipartFile> images, Integer primaryImageIndex) throws IOException {
        try {
            if (property == null || property.getId() == null) {
                throw new IllegalArgumentException("Property and property ID cannot be null");
            }
            Property existingProperty = propertyRepository.findById(property.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + property.getId()));
            validateProperty(property);
            List<String> newImageUrls = processImages(images, primaryImageIndex);
            List<String> existingImageUrls = existingProperty.getImageUrls() != null ? existingProperty.getImageUrls() : new ArrayList<>();
            existingImageUrls.addAll(newImageUrls);
            property.setImageUrls(existingImageUrls);
            property.setPrimaryImageUrl(existingImageUrls.isEmpty() ? null : existingImageUrls.get(
                    primaryImageIndex != null && primaryImageIndex >= 0 && primaryImageIndex < existingImageUrls.size() ? primaryImageIndex : 0));
            Property updatedProperty = propertyRepository.save(property);
            System.out.println("[PropertyServiceImpl] Updated property with ID: " + updatedProperty.getId());
            return updatedProperty;
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error updating property: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            System.err.println("[PropertyServiceImpl] Error updating property due to IO issue: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error updating property: " + e.getMessage());
            throw new RuntimeException("Failed to update property", e);
        }
    }

    @Override
    public Property updateProperty(Property property, List<MultipartFile> images, String landlordId, Integer primaryImageIndex, List<String> removedImages) throws IOException {
        try {
            if (property == null || property.getId() == null || landlordId == null) {
                throw new IllegalArgumentException("Property, property ID, and landlord ID cannot be null");
            }
            Property existingProperty = propertyRepository.findById(property.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + property.getId()));
            if (!existingProperty.getLandlordId().equals(landlordId)) {
                throw new SecurityException("Unauthorized to update this property");
            }
            validateProperty(property);
            List<String> newImageUrls = processImages(images, primaryImageIndex);
            List<String> existingImageUrls = existingProperty.getImageUrls() != null ? new ArrayList<>(existingProperty.getImageUrls()) : new ArrayList<>();
            existingImageUrls.addAll(newImageUrls);
            if (removedImages != null && !removedImages.isEmpty()) {
                existingImageUrls.removeAll(removedImages);
                deleteImages(removedImages);
            }
            property.setImageUrls(existingImageUrls);
            property.setPrimaryImageUrl(existingImageUrls.isEmpty() ? null : existingImageUrls.get(
                    primaryImageIndex != null && primaryImageIndex >= 0 && primaryImageIndex < existingImageUrls.size() ? primaryImageIndex : 0));
            property.setLandlordId(landlordId);
            Property updatedProperty = propertyRepository.save(property);
            System.out.println("[PropertyServiceImpl] Updated property with ID: " + updatedProperty.getId());
            return updatedProperty;
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error updating property: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            System.err.println("[PropertyServiceImpl] Error updating property due to IO issue: " + e.getMessage());
            throw e;
        } catch (SecurityException e) {
            System.err.println("[PropertyServiceImpl] Security error updating property: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error updating property: " + e.getMessage());
            throw new RuntimeException("Failed to update property", e);
        }
    }

    @Override
    public void deleteProperty(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Property ID cannot be null or empty");
            }
            Property property = propertyRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));
            deleteImages(property.getImageUrls());
            propertyRepository.deleteById(id);
            System.out.println("[PropertyServiceImpl] Deleted property with ID: " + id);
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error deleting property: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error deleting property: " + e.getMessage());
            throw new RuntimeException("Failed to delete property", e);
        }
    }

    @Override
    public void deleteLandlordProperty(String id, String landlordId) {
        try {
            if (id == null || id.trim().isEmpty() || landlordId == null || landlordId.trim().isEmpty()) {
                throw new IllegalArgumentException("Property ID and landlord ID cannot be null or empty");
            }
            Property property = propertyRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));
            if (!property.getLandlordId().equals(landlordId)) {
                throw new SecurityException("Unauthorized to delete this property");
            }
            deleteImages(property.getImageUrls());
            propertyRepository.deleteById(id);
            System.out.println("[PropertyServiceImpl] Deleted property with ID: " + id + " for landlord: " + landlordId);
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error deleting property: " + e.getMessage());
            throw e;
        } catch (SecurityException e) {
            System.err.println("[PropertyServiceImpl] Security error deleting property: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error deleting property: " + e.getMessage());
            throw new RuntimeException("Failed to delete property", e);
        }
    }

    @Override
    public List<Property> getPropertiesByMaxRent(double rent) {
        try {
            if (rent <= 0) {
                throw new IllegalArgumentException("Rent must be positive");
            }
            List<Property> properties = propertyRepository.findByRentLessThanEqual(rent);
            System.out.println("[PropertyServiceImpl] Retrieved " + properties.size() + " properties with rent <= " + rent);
            return properties;
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving properties by rent: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving properties by rent: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve properties by rent", e);
        }
    }

    @Override
    public List<Property> searchPropertiesByAddress(String address) {
        try {
            if (address == null || address.trim().isEmpty()) {
                throw new IllegalArgumentException("Address cannot be null or empty");
            }
            List<Property> properties = propertyRepository.findByAddressContainingIgnoreCase(address);
            System.out.println("[PropertyServiceImpl] Retrieved " + properties.size() + " properties matching address: " + address);
            return properties;
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error searching properties by address: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error searching properties by address: " + e.getMessage());
            throw new RuntimeException("Failed to search properties by address", e);
        }
    }

    @Override
    public List<Property> getPropertiesByType(String type) {
        try {
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Property type cannot be null or empty");
            }
            List<Property> properties = propertyRepository.findByPropertyType(type);
            System.out.println("[PropertyServiceImpl] Retrieved " + properties.size() + " properties of type: " + type);
            return properties;
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving properties by type: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving properties by type: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve properties by type", e);
        }
    }

    @Override
    public List<Property> getFilteredProperties(String location, Double priceMin, Double priceMax, String propertyType, Integer bedrooms) {
        try {
            List<Property> properties = propertyRepository.findAll();
            if (location != null && !location.trim().isEmpty()) {
                properties = properties.stream()
                        .filter(p -> p.getLocation() != null && p.getLocation().toLowerCase().contains(location.toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (priceMin != null) {
                properties = properties.stream()
                        .filter(p -> p.getRent() >= priceMin)
                        .collect(Collectors.toList());
            }
            if (priceMax != null) {
                properties = properties.stream()
                        .filter(p -> p.getRent() <= priceMax)
                        .collect(Collectors.toList());
            }
            if (propertyType != null && !propertyType.trim().isEmpty()) {
                properties = properties.stream()
                        .filter(p -> p.getPropertyType() != null && p.getPropertyType().equalsIgnoreCase(propertyType))
                        .collect(Collectors.toList());
            }
            if (bedrooms != null) {
                properties = properties.stream()
                        .filter(p -> Objects.equals(p.getBedrooms(), bedrooms))
                        .collect(Collectors.toList());
            }
            System.out.println("[PropertyServiceImpl] Retrieved " + properties.size() + " filtered properties");
            return properties;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error filtering properties: " + e.getMessage());
            throw new RuntimeException("Failed to filter properties", e);
        }
    }

    @Override
    public void scheduleViewing(Viewing viewing) {
        try {
            if (viewing == null || viewing.getPropertyId() == null || viewing.getUserId() == null) {
                throw new IllegalArgumentException("Viewing, property ID, and user ID cannot be null");
            }
            viewing.setScheduledTime(LocalDateTime.now());
            viewingRepository.save(viewing);
            System.out.println("[PropertyServiceImpl] Scheduled viewing for property ID: " + viewing.getPropertyId());
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error scheduling viewing: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error scheduling viewing: " + e.getMessage());
            throw new RuntimeException("Failed to schedule viewing", e);
        }
    }

    @Override
    public void applyForProperty(RentalApplication application) {
        try {
            if (application == null || application.getPropertyId() == null || application.getUserId() == null) {
                throw new IllegalArgumentException("Application, property ID, and user ID cannot be null");
            }
            rentalApplicationRepository.save(application);
            System.out.println("[PropertyServiceImpl] Applied for property ID: " + application.getPropertyId());
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error applying for property: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error applying for property: " + e.getMessage());
            throw new RuntimeException("Failed to apply for property", e);
        }
    }

    @Override
    public void submitMaintenanceRequest(MaintenanceRequest request) {
        try {
            if (request == null || request.getPropertyId() == null || request.getUserId() == null) {
                throw new IllegalArgumentException("Maintenance request, property ID, and user ID cannot be null");
            }
            maintenanceService.submitMaintenanceRequest(request.getUserId(), request.getPropertyId(), request.getDescription());
            System.out.println("[PropertyServiceImpl] Submitted maintenance request for property ID: " + request.getPropertyId());
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error submitting maintenance request: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error submitting maintenance request: " + e.getMessage());
            throw new RuntimeException("Failed to submit maintenance request", e);
        }
    }

    @Override
    public void fixImageUrls() {
        try {
            List<Property> properties = propertyRepository.findAll();
            for (Property property : properties) {
                List<String> imageUrls = property.getImageUrls();
                if (imageUrls != null) {
                    List<String> updatedUrls = imageUrls.stream()
                            .map(url -> {
                                if (url != null && !url.startsWith("http")) {
                                    return IMAGE_BASE_URL.endsWith("/") ? IMAGE_BASE_URL + url : IMAGE_BASE_URL + "/" + url;
                                }
                                return url;
                            })
                            .collect(Collectors.toList());
                    property.setImageUrls(updatedUrls);
                    String primaryImageUrl = property.getPrimaryImageUrl();
                    if (primaryImageUrl != null && !primaryImageUrl.startsWith("http")) {
                        property.setPrimaryImageUrl(IMAGE_BASE_URL.endsWith("/") ? IMAGE_BASE_URL + primaryImageUrl : IMAGE_BASE_URL + "/" + primaryImageUrl);
                    }
                    propertyRepository.save(property);
                }
            }
            System.out.println("[PropertyServiceImpl] Fixed image URLs for all properties");
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error fixing image URLs: " + e.getMessage());
            throw new RuntimeException("Failed to fix image URLs", e);
        }
    }

    @Override
    public String getPropertyNameById(String propertyId) {
        try {
            if (propertyId == null || propertyId.trim().isEmpty()) {
                throw new IllegalArgumentException("Property ID cannot be null or empty");
            }
            Optional<Property> property = propertyRepository.findById(propertyId);
            if (property.isPresent()) {
                System.out.println("[PropertyServiceImpl] Retrieved property name for ID: " + propertyId);
                return property.get().getTitle();
            } else {
                System.out.println("[PropertyServiceImpl] Property not found with ID: " + propertyId);
                return "Unknown Property";
            }
        } catch (IllegalArgumentException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving property name: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("[PropertyServiceImpl] Error retrieving property name: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve property name", e);
        }
    }
}