package com.rentalconnects.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // No-args constructor for serialization
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String authorId;
    private String targetId;
    private int rating;
    private String comment;
}
