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

    private final TeamRepository teamRepository;

    // In-memory data pool for demonstration (Requirement #3)
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
    public Team getTeamById(Integer id) { // Используем Integer
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
        // Исправлено: для int проверяем на 0, а не на null
        if (team.getId() == 0) {
            throw new ValidationException("Team", "Team ID cannot be 0 for update");
        }
        Team updated = teamRepository.update(team);
        refreshDataPool();
        return updated;
    }

    @Override
    public void deleteTeam(Integer id) { // void и Integer
        if (!teamRepository.deleteById(id)) {
            throw new EntityNotFoundException("Team", id);
        }
        refreshDataPool();
    }

    @Override
    public List<Team> getTeamsBySport(String sport) {
        return teamDataPool.stream()
                .filter(team -> team.getSport().equalsIgnoreCase(sport))
                .collect(Collectors.toList());
    }

    @Override
    public List<Team> getTeamsByLocation(String location) {
        return teamDataPool.stream()
                .filter(team -> team.getLocation().toLowerCase().contains(location.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Team> filterTeams(Predicate<Team> predicate) {
        return teamDataPool.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public List<Team> sortTeams(String sortBy, boolean ascending) {
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
                // Исправлено: foundedYear это примитив int
                comparator = Comparator.comparingInt(Team::getFoundedYear);
                break;
            default:
                comparator = Comparator.comparingInt(Team::getId);
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

        long totalTeams = teamDataPool.size();
        stats.put("totalTeams", totalTeams);

        Map<String, Long> teamsBySport = teamDataPool.stream()
                .collect(Collectors.groupingBy(Team::getSport, Collectors.counting()));
        stats.put("teamsBySport", teamsBySport);

        Map<String, Long> teamsByLocation = teamDataPool.stream()
                .collect(Collectors.groupingBy(Team::getLocation, Collectors.counting()));
        stats.put("teamsByLocation", teamsByLocation);

        // Исправлено: проверяем != 0 для примитива int
        OptionalDouble avgFoundedYear = teamDataPool.stream()
                .filter(team -> team.getFoundedYear() != 0)
                .mapToInt(Team::getFoundedYear)
                .average();
        stats.put("averageFoundedYear", avgFoundedYear.orElse(0.0));

        teamDataPool.stream()
                .filter(team -> team.getFoundedYear() != 0)
                .min(Comparator.comparingInt(Team::getFoundedYear))
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