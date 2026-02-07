package com.sport.factory;

import com.sport.repository.impl.PlayerRepositoryImpl;
import com.sport.repository.impl.TeamRepositoryImpl;
import com.sport.repository.interfaces.PlayerRepository;
import com.sport.repository.interfaces.TeamRepository;

/**
 * Factory pattern implementation for creating repository instances.
 * Centralizes object creation and allows for easy switching of implementations.
 */
public class RepositoryFactory {
    
    // Singleton instances (eager initialization)
    private static final TeamRepository TEAM_REPOSITORY = new TeamRepositoryImpl();
    private static final PlayerRepository PLAYER_REPOSITORY = new PlayerRepositoryImpl();
    
    // Private constructor to prevent instantiation
    private RepositoryFactory() {
        throw new UnsupportedOperationException("Factory class cannot be instantiated");
    }
    
    /**
     * Creates and returns a TeamRepository instance.
     * 
     * @return TeamRepository implementation
     */
    public static TeamRepository createTeamRepository() {
        return TEAM_REPOSITORY;
    }
    
    /**
     * Creates and returns a PlayerRepository instance.
     * 
     * @return PlayerRepository implementation
     */
    public static PlayerRepository createPlayerRepository() {
        return PLAYER_REPOSITORY;
    }
    
    /**
     * Creates repository by type using generics.
     * Demonstrates Factory pattern with generics.
     * 
     * @param <T> the repository type
     * @param repositoryClass the repository interface class
     * @return repository instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T createRepository(Class<T> repositoryClass) {
        if (repositoryClass == TeamRepository.class) {
            return (T) TEAM_REPOSITORY;
        } else if (repositoryClass == PlayerRepository.class) {
            return (T) PLAYER_REPOSITORY;
        }
        throw new IllegalArgumentException("Unknown repository type: " + repositoryClass.getName());
    }
}
