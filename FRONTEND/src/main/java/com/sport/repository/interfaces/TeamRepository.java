package com.sport.repository.interfaces;

import com.sport.domain.Team;
import java.util.List;

/**
 * Team-specific repository interface.
 * Extends generic CrudRepository and adds team-specific operations.
 */
public interface TeamRepository extends CrudRepository<Team, Long> {

    /**
     * Finds teams by sport type.
     *
     * @param sport the sport type (e.g., "Football", "Basketball")
     * @return list of teams playing this sport
     */
    List<Team> findBySport(String sport);

    /**
     * Finds teams by location.
     *
     * @param location the location
     * @return list of teams from this location
     */
    List<Team> findByLocation(String location);

    /**
     * Finds teams by coach name.
     *
     * @param coach the coach name
     * @return list of teams coached by this person
     */
    List<Team> findByCoach(String coach);

    /**
     * Searches teams by name (partial match).
     *
     * @param namePart part of the team name
     * @return list of matching teams
     */
    List<Team> searchByName(String namePart);
}
