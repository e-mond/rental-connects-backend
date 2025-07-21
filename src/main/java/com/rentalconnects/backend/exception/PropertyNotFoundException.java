package com.rentalconnects.backend.exception;

/**
 * Custom exception for handling cases where a property is not found in the database.
 * This exception is thrown by the PropertyService when a property with the specified ID does not exist.
 */
public class PropertyNotFoundException extends RuntimeException {

    /**
     * Constructs a new PropertyNotFoundException with the specified message.
     *
     * @param message The detail message explaining why the exception was thrown.
     */
    public PropertyNotFoundException(String message) {
        super(message);
    }
}