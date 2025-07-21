package com.rentalconnects.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rentalconnects.backend.dto.MessageDTO;
import com.rentalconnects.backend.model.Message;
import com.rentalconnects.backend.model.Property;
import com.rentalconnects.backend.model.User;
import com.rentalconnects.backend.repository.MessageRepository;
import com.rentalconnects.backend.repository.PropertyRepository;
import com.rentalconnects.backend.repository.UserRepository;
import com.rentalconnects.backend.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public MessageServiceImpl(
            MessageRepository messageRepository,
            UserRepository userRepository,
            PropertyRepository propertyRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public List<MessageDTO> getMessagesForLandlord(String landlordId) {
        List<Message> messages = messageRepository.findByRecipientIdOrSenderId(landlordId, landlordId);
        return messages.stream()
                .map(msg -> {
                    MessageDTO dto = new MessageDTO();
                    dto.setId(msg.getId());
                    dto.setSenderId(msg.getSenderId());
                    dto.setRecipientId(msg.getRecipientId());
                    dto.setPropertyId(msg.getPropertyId());
                    dto.setEncryptedContent(msg.getEncryptedContent());
                    dto.setEncryptedKey(msg.getEncryptedKey());
                    dto.setReplyToId(msg.getReplyToId());
                    dto.setCreatedAt(msg.getCreatedAt());
                    dto.setRead(msg.isRead());

                    // Fetch sender details
                    User sender = userRepository.findById(msg.getSenderId()).orElse(null);
                    if (sender != null) {
                        dto.setSenderName(sender.getFullName());
                        dto.setSenderAvatar(sender.getProfilePic());
                    }

                    // Fetch property details
                    if (msg.getPropertyId() != null) {
                        Property property = propertyRepository.findById(msg.getPropertyId()).orElse(null);
                        if (property != null) {
                            MessageDTO.PropertyDTO propertyDTO = new MessageDTO.PropertyDTO();
                            propertyDTO.setId(property.getId());
                            propertyDTO.setTitle(property.getTitle());
                            dto.setProperty(propertyDTO);
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Message getMessageById(String messageId, String landlordId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with ID: " + messageId));
        if (!message.getRecipientId().equals(landlordId) && !message.getSenderId().equals(landlordId)) {
            throw new SecurityException("Unauthorized access to message");
        }
        return message;
    }

    @Override
    public MessageDTO updateMessage(String messageId, MessageDTO messageDTO) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with ID: " + messageId));
        if (!message.getRecipientId().equals(messageDTO.getRecipientId()) &&
            !message.getSenderId().equals(messageDTO.getRecipientId())) {
            throw new SecurityException("Unauthorized access to message");
        }
        message.setRead(messageDTO.isRead());
        messageRepository.save(message);
        messageDTO.setId(message.getId());
        return messageDTO;
    }

    @Override
    public Message createMessage(Message message) {
        if (message.getRecipientId() == null || message.getSenderId() == null) {
            throw new IllegalArgumentException("Recipient and sender IDs are required");
        }
        message.setCreatedAt(LocalDateTime.now());
        message.setRead(false);
        return messageRepository.save(message);
    }

    @Override
    public List<MessageDTO> getMessagesByTenantId(String tenantId) {
        List<Message> messages = messageRepository.findByRecipientIdOrSenderId(tenantId, tenantId);
        return messages.stream()
                .map(msg -> {
                    MessageDTO dto = new MessageDTO();
                    dto.setId(msg.getId());
                    dto.setSenderId(msg.getSenderId());
                    dto.setRecipientId(msg.getRecipientId());
                    dto.setPropertyId(msg.getPropertyId());
                    dto.setEncryptedContent(msg.getEncryptedContent());
                    dto.setEncryptedKey(msg.getEncryptedKey());
                    dto.setReplyToId(msg.getReplyToId());
                    dto.setCreatedAt(msg.getCreatedAt());
                    dto.setRead(msg.isRead());

                    // Fetch sender details
                    User sender = userRepository.findById(msg.getSenderId()).orElse(null);
                    if (sender != null) {
                        dto.setSenderName(sender.getFullName());
                        dto.setSenderAvatar(sender.getProfilePic());
                    }

                    // Fetch property details
                    if (msg.getPropertyId() != null) {
                        Property property = propertyRepository.findById(msg.getPropertyId()).orElse(null);
                        if (property != null) {
                            MessageDTO.PropertyDTO propertyDTO = new MessageDTO.PropertyDTO();
                            propertyDTO.setId(property.getId());
                            propertyDTO.setTitle(property.getTitle());
                            dto.setProperty(propertyDTO);
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public MessageDTO sendMessage(Message message) {
        if (message.getRecipientId() == null || message.getSenderId() == null) {
            throw new IllegalArgumentException("Recipient and sender IDs are required");
        }
        message.setCreatedAt(LocalDateTime.now());
        message.setRead(false);
        Message savedMessage = messageRepository.save(message);
        MessageDTO dto = new MessageDTO();
        dto.setId(savedMessage.getId());
        dto.setSenderId(savedMessage.getSenderId());
        dto.setRecipientId(savedMessage.getRecipientId());
        dto.setPropertyId(savedMessage.getPropertyId());
        dto.setEncryptedContent(savedMessage.getEncryptedContent());
        dto.setEncryptedKey(savedMessage.getEncryptedKey());
        dto.setReplyToId(savedMessage.getReplyToId());
        dto.setCreatedAt(savedMessage.getCreatedAt());
        dto.setRead(savedMessage.isRead());

        // Fetch sender details
        User sender = userRepository.findById(savedMessage.getSenderId()).orElse(null);
        if (sender != null) {
            dto.setSenderName(sender.getFullName());
            dto.setSenderAvatar(sender.getProfilePic());
        }

        // Fetch property details
        if (savedMessage.getPropertyId() != null) {
            Property property = propertyRepository.findById(savedMessage.getPropertyId()).orElse(null);
            if (property != null) {
                MessageDTO.PropertyDTO propertyDTO = new MessageDTO.PropertyDTO();
                propertyDTO.setId(property.getId());
                propertyDTO.setTitle(property.getTitle());
                dto.setProperty(propertyDTO);
            }
        }

        return dto;
    }

    @Override
    public String decryptContent(String encryptedContent, String encryptedKey) {
        return "Decrypted content goes here";
    }

    @Override
    public String encryptContent(String content, String key) {
        return "Encrypted content goes here";
    }
}