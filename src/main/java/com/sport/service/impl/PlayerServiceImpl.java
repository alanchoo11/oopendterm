package com.sport.service.impl;

import com.sport.domain.Player;
import com.sport.exception.EntityNotFoundException;
import com.sport.exception.ValidationException;
import com.sport.repository.interfaces.PlayerRepository;
import com.sport.service.interfaces.PlayerService;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of PlayerService.
 * Demonstrates DIP: depends on PlayerRepository interface, not implementation.
 * Uses Lambda expressions and Streams for in-memory data processing.
 */
public class PlayerServiceImpl implements PlayerService {
    
    // DIP: Service depends on Repository interface, not implementation
    private final PlayerRepository playerRepository;
    
    // In-memory data pool for demonstration
    private final List<Player> playerDataPool;
    
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.playerDataPool = new ArrayList<>();
        // Load initial data into memory pool
        refreshDataPool();
    }
    
    private void refreshDataPool() {
        playerDataPool.clear();
        playerDataPool.addAll(playerRepository.findAll());
    }
    
    @Override
    public Player createPlayer(Player player) {
        validatePlayer(player);
        Player saved = playerRepository.save(player);
        refreshDataPool();
        return saved;
    }
    
    @Override
    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Player", id));
    }
    
    @Override
    public List<Player> getAllPlayers() {
        return new ArrayList<>(playerDataPool);
    }
    
    @Override
    public Player updatePlayer(Player player) {
        validatePlayer(player);
        if (player.getId() == null) {
            throw new ValidationException("Player", "Player ID cannot be null for update");
        }
        Player updated = playerRepository.update(player);
        refreshDataPool();
        return updated;
    }
    
    @Override
    public boolean deletePlayer(Long id) {
        boolean deleted = playerRepository.deleteById(id);
        if (deleted) {
            refreshDataPool();
        }
        return deleted;
    }
    
    @Override
    public List<Player> getPlayersByTeam(Long teamId) {
        // Using Lambda expression
        return playerDataPool.stream()
                .filter(player -> teamId.equals(player.getTeamId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> getPlayersByPosition(String position) {
        // Using Lambda expression with case-insensitive comparison
        return playerDataPool.stream()
                .filter(player -> player.getPosition().equalsIgnoreCase(position))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> getTopRatedPlayers(int limit) {
        // Using Lambda for sorting and limiting
        return playerDataPool.stream()
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating())) // Descending
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> getFreeAgents() {
        // Using Lambda to find players without a team
        return playerDataPool.stream()
                .filter(player -> player.getTeamId() == null)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> filterPlayers(Predicate<Player> predicate) {
        // Accepts any custom predicate (Lambda) for flexible filtering
        return playerDataPool.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> sortPlayers(String sortBy, boolean ascending) {
        // Using Lambda expressions for dynamic sorting
        Comparator<Player> comparator;
        
        switch (sortBy.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(Player::getFullName, String.CASE_INSENSITIVE_ORDER);
                break;
            case "age":
                comparator = Comparator.comparing(Player::getAge);
                break;
            case "position":
                comparator = Comparator.comparing(Player::getPosition, String.CASE_INSENSITIVE_ORDER);
                break;
            case "rating":
                comparator = Comparator.comparing(Player::getRating);
                break;
            case "jersey":
                comparator = Comparator.comparing(Player::getJerseyNumber, 
                    Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            default:
                comparator = Comparator.comparing(Player::getId);
        }
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return playerDataPool.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getPlayerStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total count
        long totalPlayers = playerDataPool.size();
        stats.put("totalPlayers", totalPlayers);
        
        // Average rating using mapToDouble and average
        double avgRating = playerDataPool.stream()
                .mapToDouble(Player::getRating)
                .average()
                .orElse(0.0);
        stats.put("averageRating", Math.round(avgRating * 100.0) / 100.0);
        
        // Highest rated player
        playerDataPool.stream()
                .max(Comparator.comparing(Player::getRating))
                .ifPresent(player -> {
                    stats.put("highestRatedPlayer", player.getFullName());
                    stats.put("highestRating", player.getRating());
                });
        
        // Players by position using groupingBy with Lambda
        Map<String, Long> playersByPosition = playerDataPool.stream()
                .collect(Collectors.groupingBy(
                        Player::getPosition, 
                        Collectors.counting()
                ));
        stats.put("playersByPosition", playersByPosition);
        
        // Average age
        double avgAge = playerDataPool.stream()
                .mapToInt(Player::getAge)
                .average()
                .orElse(0.0);
        stats.put("averageAge", Math.round(avgAge * 10.0) / 10.0);
        
        // Free agents count
        long freeAgentsCount = playerDataPool.stream()
                .filter(player -> player.getTeamId() == null)
                .count();
        stats.put("freeAgentsCount", freeAgentsCount);
        
        // Rating distribution
        Map<String, Long> ratingDistribution = playerDataPool.stream()
                .collect(Collectors.groupingBy(
                        player -> {
                            double rating = player.getRating();
                            if (rating >= 9.0) return "Excellent (9.0+)";
                            else if (rating >= 8.0) return "Good (8.0-8.9)";
                            else if (rating >= 7.0) return "Average (7.0-7.9)";
                            else return "Below Average (<7.0)";
                        },
                        Collectors.counting()
                ));
        stats.put("ratingDistribution", ratingDistribution);
        
        return stats;
    }
    
    @Override
    public double calculateAverageRating() {
        return playerDataPool.stream()
                .mapToDouble(Player::getRating)
                .average()
                .orElse(0.0);
    }
    
    @Override
    public long countPlayers() {
        return playerRepository.count();
    }
    
    private void validatePlayer(Player player) {
        List<String> errors = new ArrayList<>();
        
        if (player.getFirstName() == null || player.getFirstName().trim().isEmpty()) {
            errors.add("First name is required");
        }
        if (player.getLastName() == null || player.getLastName().trim().isEmpty()) {
            errors.add("Last name is required");
        }
        if (player.getAge() == null || player.getAge() < 16 || player.getAge() > 50) {
            errors.add("Age must be between 16 and 50");
        }
        if (player.getPosition() == null || player.getPosition().trim().isEmpty()) {
            errors.add("Position is required");
        }
        if (player.getRating() == null || player.getRating() < 0.0 || player.getRating() > 10.0) {
            errors.add("Rating must be between 0.0 and 10.0");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Player", errors);
        }
    }
}
