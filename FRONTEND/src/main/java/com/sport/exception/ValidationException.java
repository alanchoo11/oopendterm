package com.sport.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom exception thrown when entity validation fails.
 * Collects multiple validation errors for comprehensive error reporting.
 */
public class ValidationException extends RuntimeException {
    
    private final List<String> errors;
    private final String entityName;
    
    public ValidationException(String entityName, String message) {
        super(message);
        this.entityName = entityName;
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }
    
    public ValidationException(String entityName, List<String> errors) {
        super(String.format("Validation failed for %s: %s", entityName, String.join(", ", errors)));
        this.entityName = entityName;
        this.errors = new ArrayList<>(errors);
    }
    
    public ValidationException(String entityName, String message, Throwable cause) {
        super(message, cause);
        this.entityName = entityName;
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }
    
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public String getEntityName() {
        return entityName;
    }
    
    public void addError(String error) {
        this.errors.add(error);
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
