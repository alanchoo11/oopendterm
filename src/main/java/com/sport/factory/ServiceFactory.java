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
 */
public class ServiceFactory {

    // Service instances (lazy initialization via factory methods)
    private static TeamService teamService;
    private static PlayerService playerService;

    private ServiceFactory() {
        throw new UnsupportedOperationException("Factory class cannot be instantiated");
    }

    /**
     * Creates and returns a TeamService instance.
     */
    public static synchronized TeamService createTeamService() {
        if (teamService == null) {
            TeamRepository teamRepository = RepositoryFactory.createTeamRepository();
            teamService = new TeamServiceImpl(teamRepository);
        }
        return teamService;
    }

    /**
     * Creates and returns a PlayerService instance.
     * Injects BOTH PlayerRepository and TeamRepository.
     */
    public static synchronized PlayerService createPlayerService() {
        if (playerService == null) {
            PlayerRepository playerRepository = RepositoryFactory.createPlayerRepository();
            TeamRepository teamRepository = RepositoryFactory.createTeamRepository();

            // ИСПРАВЛЕНО: Передаем оба репозитория в конструктор
            playerService = new PlayerServiceImpl(playerRepository, teamRepository);
        }
        return playerService;
    }

    // --- Методы для тестов ---

    public static TeamService createTeamServiceWithRepository(TeamRepository teamRepository) {
        return new TeamServiceImpl(teamRepository);
    }

    public static PlayerService createPlayerServiceWithRepositories(PlayerRepository playerRepository, TeamRepository teamRepository) {
        return new PlayerServiceImpl(playerRepository, teamRepository);
    }
}