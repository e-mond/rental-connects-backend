package com.rentalconnects.backend.controller;

// imports for handling files, spring web, security, and project-specific utilities
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rentalconnects.backend.dto.DocumentDTO;
import com.rentalconnects.backend.model.DocumentEntity;
import com.rentalconnects.backend.service.DocumentService;
import com.rentalconnects.backend.util.AuthUtils;

// handles document-related api requests for landlords in rentalconnects
@RestController
@RequestMapping("/api/landlord")
public class DocumentController {

    // service for document operations
    @Autowired
    private DocumentService documentService;

    // utility for authentication-related operations
    @Autowired
    private AuthUtils authUtils;

    // fetches all documents for the authenticated landlord
    @GetMapping("/documents")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<DocumentDTO>> getDocuments(@AuthenticationPrincipal UserDetails userDetails) {
        // retrieves current landlord id from authentication context
        String userId = authUtils.getCurrentUserId();
        // checks if user is authenticated
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        // fetches documents for the landlord
        List<DocumentDTO> documents = documentService.getDocumentsByLandlordId(userId);
        // returns list of documents
        return ResponseEntity.ok(documents);
    }

    // uploads a new document for the authenticated landlord
    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) throws IOException {
        // retrieves current landlord id from authentication context
        String userId = authUtils.getCurrentUserId();
        // checks if user is authenticated
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        // uploads document and returns its details
        DocumentDTO document = documentService.uploadDocument(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    // renames an existing document for the authenticated landlord
    @PutMapping("/documents/{docId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<DocumentDTO> renameDocument(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String docId,
            @RequestBody String newName) {
        // retrieves current landlord id from authentication context
        String userId = authUtils.getCurrentUserId();
        // checks if user is authenticated
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        // fetches document by id
        DocumentEntity document = documentService.getDocumentById(docId);
        // verifies document exists and belongs to the landlord
        if (document == null || !document.getLandlordId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // updates document name and saves changes
        document.setName(newName.trim());
        documentService.updateDocument(document);
        // returns updated document details
        return ResponseEntity.ok(documentService.getDocumentsByLandlordId(userId)
                .stream()
                .filter(dto -> dto.getId().equals(docId))
                .findFirst()
                .orElse(null));
    }

    // deletes a document for the authenticated landlord
    @DeleteMapping("/documents/{docId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> deleteDocument(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String docId) {
        // retrieves current landlord id from authentication context
        String userId = authUtils.getCurrentUserId();
        // checks if user is authenticated
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        // fetches document by id
        DocumentEntity document = documentService.getDocumentById(docId);
        // verifies document exists and belongs to the landlord
        if (document == null || !document.getLandlordId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // deletes the document
        documentService.deleteDocument(docId);
        // returns no content on successful deletion
        return ResponseEntity.noContent().build();
    }

    // downloads a document for the authenticated landlord
    @GetMapping("/documents/{docId}/download")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<byte[]> downloadDocument(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String docId) throws IOException {
        // retrieves current landlord id from authentication context
        String userId = authUtils.getCurrentUserId();
        // checks if user is authenticated
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        // fetches document by id
        DocumentEntity document = documentService.getDocumentById(docId);
        // verifies document exists and belongs to the landlord
        if (document == null || !document.getLandlordId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // retrieves document content
        byte[] fileContent = documentService.getDocumentContent(document.getUrl());
        // returns document as a downloadable file
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=\"" + document.getName() + "\"")
                .body(fileContent);
    }
}