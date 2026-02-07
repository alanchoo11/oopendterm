package com.sport.factory;

import com.sport.repository.interfaces.PlayerRepository;
import com.sport.repository.interfaces.TeamRepository;
import com.sport.service.impl.PlayerServiceImpl;
import com.sport.service.impl.TeamServiceImpl;
import com.sport.service.interfaces.PlayerService;
import com.sport.service.interfaces.TeamService;

/**
 * Factory pattern implementation for creating service instances.
 * Demonstrates Dependency Injection through Factory pattern.
 * Services are created with their dependencies (repositories) injected.
 */
public class ServiceFactory {
    
    // Service instances (lazy initialization via factory methods)
    private static TeamService teamService;
    private static PlayerService playerService;
    
    // Private constructor to prevent instantiation
    private ServiceFactory() {
        throw new UnsupportedOperationException("Factory class cannot be instantiated");
    }
    
    /**
     * Creates and returns a TeamService instance.
     * Injects TeamRepository dependency.
     * 
     * @return TeamService implementation
     */
    public static TeamService createTeamService() {
        if (teamService == null) {
            // Get repository from factory (DIP: service depends on interface)
            TeamRepository teamRepository = RepositoryFactory.createTeamRepository();
            teamService = new TeamServiceImpl(teamRepository);
        }
        return teamService;
    }
    
    /**
     * Creates and returns a PlayerService instance.
     * Injects PlayerRepository dependency.
     * 
     * @return PlayerService implementation
     */
    public static PlayerService createPlayerService() {
        if (playerService == null) {
            // Get repository from factory (DIP: service depends on interface)
            PlayerRepository playerRepository = RepositoryFactory.createPlayerRepository();
            playerService = new PlayerServiceImpl(playerRepository);
        }
        return playerService;
    }
    
    /**
     * Creates service with explicit dependency injection.
     * Useful for testing with mock repositories.
     * 
     * @param teamRepository the repository to inject
     * @return TeamService with injected repository
     */
    public static TeamService createTeamServiceWithRepository(TeamRepository teamRepository) {
        return new TeamServiceImpl(teamRepository);
    }
    
    /**
     * Creates service with explicit dependency injection.
     * Useful for testing with mock repositories.
     * 
     * @param playerRepository the repository to inject
     * @return PlayerService with injected repository
     */
    public static PlayerService createPlayerServiceWithRepository(PlayerRepository playerRepository) {
        return new PlayerServiceImpl(playerRepository);
    }
}
