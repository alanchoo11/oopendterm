package com.sport.repository.interfaces;

import com.sport.domain.Player;
import java.util.List;

/**
 * Player-specific repository interface.
 * Extends generic CrudRepository and adds player-specific operations.
 */
public interface PlayerRepository extends CrudRepository<Player, Long> {
    
    /**
     * Finds players by team ID.
     * 
     * @param teamId the team ID
     * @return list of players in this team
     */
    List<Player> findByTeamId(Long teamId);
    
    /**
     * Finds players by position.
     * 
     * @param position the position (e.g., "Forward", "Guard")
     * @return list of players in this position
     */
    List<Player> findByPosition(String position);
    
    /**
     * Finds players with rating above specified value.
     * 
     * @param minRating minimum rating
     * @return list of players with rating >= minRating
     */
    List<Player> findByRatingGreaterThan(Double minRating);
    
    /**
     * Finds players by age range.
     * 
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return list of players in age range
     */
    List<Player> findByAgeBetween(Integer minAge, Integer maxAge);
    
    /**
     * Searches players by name (partial match on first or last name).
     * 
     * @param namePart part of the name
     * @return list of matching players
     */
    List<Player> searchByName(String namePart);
    
    /**
     * Finds all free agents (players without a team).
     * 
     * @return list of free agent players
     */
    List<Player> findFreeAgents();
}
