package com.sport.service.interfaces;

import com.sport.domain.Team;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Team service interface.
 * Defines business operations for team management.
 * Service layer depends on repository interface (DIP principle).
 */
public interface TeamService {
    
    Team createTeam(Team team);
    
    Team getTeamById(Long id);
    
    List<Team> getAllTeams();
    
    Team updateTeam(Team team);
    
    boolean deleteTeam(Long id);
    
    List<Team> getTeamsBySport(String sport);
    
    List<Team> getTeamsByLocation(String location);
    
    /**
     * Filters teams using a custom predicate (Lambda expression).
     * Demonstrates functional programming in Java.
     * 
     * @param predicate the filter condition
     * @return filtered list of teams
     */
    List<Team> filterTeams(Predicate<Team> predicate);
    
    /**
     * Sorts teams by specified field.
     * 
     * @param sortBy field name to sort by
     * @param ascending true for ascending, false for descending
     * @return sorted list
     */
    List<Team> sortTeams(String sortBy, boolean ascending);
    
    /**
     * Gets team statistics for dashboard.
     * 
     * @return map with statistics
     */
    Map<String, Object> getTeamStatistics();
    
    long countTeams();
}
