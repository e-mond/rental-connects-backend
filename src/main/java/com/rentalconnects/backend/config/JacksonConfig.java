package com.rentalconnects.backend.config;

// imports for jackson objectmapper, java time module, and logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// marks this class as a configuration class for spring
@Configuration
public class JacksonConfig {

    // creates a logger instance for this class to log messages
    private static final Logger logger = LoggerFactory.getLogger(JacksonConfig.class);

    // defines a spring bean for configuring the objectmapper
    @Bean
    public ObjectMapper objectMapper() {
        // logs a message when objectmapper is initialized
        logger.info("[JacksonConfig] Initializing ObjectMapper with JavaTimeModule");
        // creates a new objectmapper instance
        ObjectMapper mapper = new ObjectMapper();
        // registers the javatimemodule to handle java 8 date and time types
        mapper.registerModule(new JavaTimeModule());
        // returns the configured objectmapper
        return mapper;
    }
}