package com.sport.service.impl;

import com.sport.domain.Team;
import com.sport.exception.EntityNotFoundException;
import com.sport.exception.ValidationException;
import com.sport.repository.interfaces.TeamRepository;
import com.sport.service.interfaces.TeamService;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of TeamService.
 * Demonstrates DIP: depends on TeamRepository interface, not implementation.
 * Uses Lambda expressions and Streams for in-memory data processing.
 */
public class TeamServiceImpl implements TeamService {
    
    // DIP: Service depends on Repository interface, not implementation
    private final TeamRepository teamRepository;
    
    // In-memory data pool for demonstration
    private final List<Team> teamDataPool;
    
    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
        this.teamDataPool = new ArrayList<>();
        // Load initial data into memory pool
        refreshDataPool();
    }
    
    private void refreshDataPool() {
        teamDataPool.clear();
        teamDataPool.addAll(teamRepository.findAll());
    }
    
    @Override
    public Team createTeam(Team team) {
        validateTeam(team);
        Team saved = teamRepository.save(team);
        refreshDataPool();
        return saved;
    }
    
    @Override
    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team", id));
    }
    
    @Override
    public List<Team> getAllTeams() {
        return new ArrayList<>(teamDataPool);
    }
    
    @Override
    public Team updateTeam(Team team) {
        validateTeam(team);
        if (team.getId() == null) {
            throw new ValidationException("Team", "Team ID cannot be null for update");
        }
        Team updated = teamRepository.update(team);
        refreshDataPool();
        return updated;
    }
    
    @Override
    public boolean deleteTeam(Long id) {
        boolean deleted = teamRepository.deleteById(id);
        if (deleted) {
            refreshDataPool();
        }
        return deleted;
    }
    
    @Override
    public List<Team> getTeamsBySport(String sport) {
        // Using Lambda expression to filter in memory
        return teamDataPool.stream()
                .filter(team -> team.getSport().equalsIgnoreCase(sport))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Team> getTeamsByLocation(String location) {
        // Using Lambda expression
        return teamDataPool.stream()
                .filter(team -> team.getLocation().toLowerCase().contains(location.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Team> filterTeams(Predicate<Team> predicate) {
        // Accepts any custom predicate (Lambda) for flexible filtering
        return teamDataPool.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Team> sortTeams(String sortBy, boolean ascending) {
        // Using Lambda expressions for dynamic sorting
        Comparator<Team> comparator;
        
        switch (sortBy.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(Team::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            case "sport":
                comparator = Comparator.comparing(Team::getSport, String.CASE_INSENSITIVE_ORDER);
                break;
            case "coach":
                comparator = Comparator.comparing(Team::getCoach, String.CASE_INSENSITIVE_ORDER);
                break;
            case "location":
                comparator = Comparator.comparing(Team::getLocation, String.CASE_INSENSITIVE_ORDER);
                break;
            case "foundedyear":
                comparator = Comparator.comparing(Team::getFoundedYear, 
                    Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            default:
                comparator = Comparator.comparing(Team::getId);
        }
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return teamDataPool.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getTeamStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total count
        long totalTeams = teamDataPool.size();
        stats.put("totalTeams", totalTeams);
        
        // Teams by sport using groupingBy with Lambda
        Map<String, Long> teamsBySport = teamDataPool.stream()
                .collect(Collectors.groupingBy(
                        Team::getSport, 
                        Collectors.counting()
                ));
        stats.put("teamsBySport", teamsBySport);
        
        // Teams by location
        Map<String, Long> teamsByLocation = teamDataPool.stream()
                .collect(Collectors.groupingBy(
                        Team::getLocation, 
                        Collectors.counting()
                ));
        stats.put("teamsByLocation", teamsByLocation);
        
        // Average founded year
        OptionalDouble avgFoundedYear = teamDataPool.stream()
                .filter(team -> team.getFoundedYear() != null)
                .mapToInt(Team::getFoundedYear)
                .average();
        stats.put("averageFoundedYear", avgFoundedYear.orElse(0.0));
        
        // Oldest team
        teamDataPool.stream()
                .filter(team -> team.getFoundedYear() != null)
                .min(Comparator.comparing(Team::getFoundedYear))
                .ifPresent(team -> stats.put("oldestTeam", team.getName()));
        
        return stats;
    }
    
    @Override
    public long countTeams() {
        return teamRepository.count();
    }
    
    private void validateTeam(Team team) {
        List<String> errors = new ArrayList<>();
        
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            errors.add("Team name is required");
        }
        if (team.getSport() == null || team.getSport().trim().isEmpty()) {
            errors.add("Sport is required");
        }
        if (team.getCoach() == null || team.getCoach().trim().isEmpty()) {
            errors.add("Coach name is required");
        }
        if (team.getLocation() == null || team.getLocation().trim().isEmpty()) {
            errors.add("Location is required");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Team", errors);
        }
    }
}
