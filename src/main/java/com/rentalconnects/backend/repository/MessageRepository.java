package com.rentalconnects.backend.repository;

import com.rentalconnects.backend.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByRecipientIdOrSenderId(String recipientId, String senderId);
}