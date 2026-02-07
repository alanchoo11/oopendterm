package com.sport.repository.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD Repository interface.
 * Demonstrates use of Generics in Java.
 * 
 * @param <T> the entity type
 * @param <ID> the identifier type
 */
public interface CrudRepository<T, ID> {
    
    /**
     * Saves an entity to the database.
     * 
     * @param entity the entity to save
     * @return the saved entity with generated ID
     */
    T save(T entity);
    
    /**
     * Finds an entity by its ID.
     * 
     * @param id the entity ID
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);
    
    /**
     * Retrieves all entities from the database.
     * 
     * @return list of all entities
     */
    List<T> findAll();
    
    /**
     * Updates an existing entity.
     * 
     * @param entity the entity to update
     * @return the updated entity
     */
    T update(T entity);
    
    /**
     * Deletes an entity by its ID.
     * 
     * @param id the entity ID
     * @return true if deleted successfully
     */
    boolean deleteById(ID id);
    
    /**
     * Checks if an entity exists by its ID.
     * 
     * @param id the entity ID
     * @return true if exists
     */
    boolean existsById(ID id);
    
    /**
     * Counts total number of entities.
     * 
     * @return the count
     */
    long count();
}
