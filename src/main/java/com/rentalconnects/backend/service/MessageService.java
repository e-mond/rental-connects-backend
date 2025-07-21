package com.rentalconnects.backend.service;

import java.util.List;

import com.rentalconnects.backend.dto.MessageDTO;
import com.rentalconnects.backend.model.Message;

public interface MessageService {
    List<MessageDTO> getMessagesForLandlord(String landlordId);
    Message getMessageById(String messageId, String landlordId);
    MessageDTO updateMessage(String messageId, MessageDTO messageDTO);
    Message createMessage(Message message);
    List<MessageDTO> getMessagesByTenantId(String tenantId);
    MessageDTO sendMessage(Message message);
    String encryptContent(String content, String recipientId);
    String decryptContent(String encryptedContent, String recipientId);
}
