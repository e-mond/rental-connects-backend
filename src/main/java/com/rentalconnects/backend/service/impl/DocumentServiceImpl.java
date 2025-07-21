package com.rentalconnects.backend.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rentalconnects.backend.dto.DocumentDTO;
import com.rentalconnects.backend.model.DocumentEntity;
import com.rentalconnects.backend.repository.DocumentRepository;
import com.rentalconnects.backend.service.DocumentService;

/**
 * Service implementation for managing document-related operations in the RentalConnects application.
 * Handles document uploads, retrieval, updates, and deletions for landlords.
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final Path uploadDir;

    /**
     * Constructor for DocumentServiceImpl.
     * Initializes the document repository and creates the upload directory if it doesn't exist.
     *
     * @param documentRepository The repository for document persistence.
     * @throws IOException If directory creation fails.
     */
    public DocumentServiceImpl(DocumentRepository documentRepository) throws IOException {
        this.documentRepository = documentRepository;
        this.uploadDir = Paths.get("uploads/documents");
        Files.createDirectories(this.uploadDir);
    }

    /**
     * Retrieves all documents associated with a specific landlord by their ID.
     *
     * @param landlordId The ID of the landlord.
     * @return A list of DocumentDTO objects representing the landlord's documents.
     */
    @Override
    public List<DocumentDTO> getDocumentsByLandlordId(String landlordId) {
        List<DocumentEntity> documents = documentRepository.findByLandlordId(landlordId);
        return documents.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Uploads a new document for a landlord, validating file type and size.
     *
     * @param landlordId The ID of the landlord uploading the document.
     * @param file The MultipartFile to upload.
     * @return A DocumentDTO representing the uploaded document.
     * @throws IOException If file writing fails.
     */
    @Override
    public DocumentDTO uploadDocument(String landlordId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, file.getBytes());

        DocumentEntity document = new DocumentEntity();
        document.setLandlordId(landlordId);
        document.setName(file.getOriginalFilename());
        document.setUrl("/uploads/documents/" + fileName);
        document.setCategory("Uncategorized"); // Default category
        document.setUpdated(LocalDateTime.now());

        DocumentEntity savedDocument = documentRepository.save(document);
        return convertToDTO(savedDocument);
    }

    /**
     * Retrieves a document by its ID.
     *
     * @param docId The ID of the document.
     * @return The DocumentEntity if found, or null if not found.
     */
    @Override
    public DocumentEntity getDocumentById(String docId) {
        return documentRepository.findById(docId).orElse(null);
    }

    /**
     * Updates an existing document.
     *
     * @param document The updated DocumentEntity.
     * @return The updated DocumentEntity.
     */
    @Override
    public DocumentEntity updateDocument(DocumentEntity document) {
        return documentRepository.save(document);
    }

    /**
     * Deletes a document by its ID.
     *
     * @param docId The ID of the document to delete.
     */
    @Override
    public void deleteDocument(String docId) {
        documentRepository.deleteById(docId);
    }

    /**
     * Retrieves the content of a document from the filesystem.
     *
     * @param url The relative URL/path of the document.
     * @return A byte array containing the document content.
     * @throws IOException If file reading fails.
     */
    @Override
    public byte[] getDocumentContent(String url) throws IOException {
        Path filePath = uploadDir.resolve(Paths.get(url).getFileName());
        return Files.readAllBytes(filePath);
    }

    /**
     * Converts a DocumentEntity to a DocumentDTO for API responses.
     *
     * @param document The DocumentEntity to convert.
     * @return A DocumentDTO with the document's details.
     */
    private DocumentDTO convertToDTO(DocumentEntity document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setName(document.getName());
        dto.setUrl(document.getUrl());
        dto.setCategory(document.getCategory());
        dto.setUpdated(document.getUpdated() != null ? document.getUpdated().toString() : null);
        return dto;
    }
}