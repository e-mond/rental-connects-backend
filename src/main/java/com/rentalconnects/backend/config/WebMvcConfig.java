package com.rentalconnects.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configures web MVC settings for RentalConnects, including static resources and CORS
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // Loads allowed origins for CORS from application properties
    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    // Maps /images/** requests to the uploads/images directory for serving static image files
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/images/**")
            .addResourceLocations("file:uploads/images/");
    }

    // Configures CORS settings to allow cross-origin requests for API endpoints
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/api/**")
            .allowedOrigins(allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("Authorization", "Content-Type", "responseType")
            .allowCredentials(true);
    }
}