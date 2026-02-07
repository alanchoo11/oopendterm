package com.sport.service.interfaces;

import com.sport.domain.Player;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Player service interface.
 * Defines business operations for player management.
 * Service layer depends on repository interface (DIP principle).
 */
public interface PlayerService {
    
    Player createPlayer(Player player);
    
    Player getPlayerById(Long id);
    
    List<Player> getAllPlayers();
    
    Player updatePlayer(Player player);
    
    boolean deletePlayer(Long id);
    
    List<Player> getPlayersByTeam(Long teamId);
    
    List<Player> getPlayersByPosition(String position);
    
    List<Player> getTopRatedPlayers(int limit);
    
    List<Player> getFreeAgents();
    
    /**
     * Filters players using a custom predicate (Lambda expression).
     * Demonstrates functional programming in Java.
     * 
     * @param predicate the filter condition
     * @return filtered list of players
     */
    List<Player> filterPlayers(Predicate<Player> predicate);
    
    /**
     * Sorts players by specified field.
     * 
     * @param sortBy field name to sort by
     * @param ascending true for ascending, false for descending
     * @return sorted list
     */
    List<Player> sortPlayers(String sortBy, boolean ascending);
    
    /**
     * Gets player statistics for dashboard.
     * 
     * @return map with statistics
     */
    Map<String, Object> getPlayerStatistics();
    
    /**
     * Calculates average rating of players.
     * 
     * @return average rating
     */
    double calculateAverageRating();
    
    long countPlayers();
}
