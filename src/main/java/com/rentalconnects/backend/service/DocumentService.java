package com.rentalconnects.backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.rentalconnects.backend.dto.DocumentDTO;
import com.rentalconnects.backend.model.DocumentEntity;

public interface DocumentService {
    List<DocumentDTO> getDocumentsByLandlordId(String landlordId);
    DocumentDTO uploadDocument(String landlordId, MultipartFile file) throws IOException;
    DocumentEntity getDocumentById(String docId);
    DocumentEntity updateDocument(DocumentEntity document);
    void deleteDocument(String docId);
    byte[] getDocumentContent(String url) throws IOException;
}