package com.rentalconnects.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rentalconnects.backend.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    
    // Custom query method to find a user by email
    User findByEmail(String email);
}
