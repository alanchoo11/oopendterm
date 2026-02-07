package com.sport.exception;

/**
 * Custom exception thrown when an entity is not found in the database.
 * Demonstrates custom exception handling in the application.
 */
public class EntityNotFoundException extends RuntimeException {
    
    private final String entityName;
    private final Object identifier;
    
    public EntityNotFoundException(String entityName, Object identifier) {
        super(String.format("%s not found with identifier: %s", entityName, identifier));
        this.entityName = entityName;
        this.identifier = identifier;
    }
    
    public EntityNotFoundException(String entityName, Object identifier, Throwable cause) {
        super(String.format("%s not found with identifier: %s", entityName, identifier), cause);
        this.entityName = entityName;
        this.identifier = identifier;
    }
    
    public String getEntityName() {
        return entityName;
    }
    
    public Object getIdentifier() {
        return identifier;
    }
}
