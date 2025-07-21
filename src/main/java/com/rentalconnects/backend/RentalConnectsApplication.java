package com.rentalconnects.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.rentalconnects.backend.config.JwtConfig;

@SpringBootApplication
@EnableMongoRepositories
@EnableConfigurationProperties(JwtConfig.class) // Enable JwtConfig properties
public class RentalConnectsApplication {
    public static void main(String[] args) {
        SpringApplication.run(RentalConnectsApplication.class, args);
    }
}