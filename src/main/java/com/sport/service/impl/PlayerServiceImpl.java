package com.sport.service.impl;

import com.sport.domain.Player;
import com.sport.exception.EntityNotFoundException;
import com.sport.exception.ValidationException;
import com.sport.repository.interfaces.PlayerRepository;
import com.sport.repository.interfaces.TeamRepository;
import com.sport.service.interfaces.PlayerService;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of PlayerService.
 */
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository; // Добавили поле

    // In-memory data pool
    private final List<Player> playerDataPool;

    // ОБНОВЛЕННЫЙ КОНСТРУКТОР: принимает ДВА репозитория
    public PlayerServiceImpl(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.playerDataPool = new ArrayList<>();
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
    public Player getPlayerById(Integer id) {
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
        if (player.getId() == null || player.getId() == 0) {
            throw new ValidationException("Player", "Player ID cannot be 0 for update");
        }
        if (!playerRepository.existsById(player.getId())) {
            throw new EntityNotFoundException("Player", player.getId());
        }

        Player updated = playerRepository.update(player);
        refreshDataPool();
        return updated;
    }

    @Override
    public void deletePlayer(Integer id) {
        if (!playerRepository.deleteById(id)) {
            throw new EntityNotFoundException("Player", id);
        }
        refreshDataPool();
    }

    // --- STREAM API & LAMBDA DEMO ---

    @Override
    public List<Player> getPlayersByTeam(Integer teamId) {
        return playerDataPool.stream()
                .filter(player -> {
                    Integer tid = player.getTeamId();
                    return tid != null && tid.equals(teamId);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> getPlayersByPosition(String position) {
        return playerDataPool.stream()
                .filter(player -> player.getPosition().equalsIgnoreCase(position))
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> getTopRatedPlayers(int limit) {
        return playerDataPool.stream()
                .sorted(Comparator.comparingDouble(Player::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> getFreeAgents() {
        return playerDataPool.stream()
                .filter(player -> player.getTeamId() == null || player.getTeamId() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> filterPlayers(Predicate<Player> predicate) {
        return playerDataPool.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> sortPlayers(String sortBy, boolean ascending) {
        Comparator<Player> comparator;

        switch (sortBy.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(Player::getLastName)
                        .thenComparing(Player::getFirstName);
                break;
            case "age":
                comparator = Comparator.comparingInt(Player::getAge);
                break;
            case "position":
                comparator = Comparator.comparing(Player::getPosition, String.CASE_INSENSITIVE_ORDER);
                break;
            case "rating":
                comparator = Comparator.comparingDouble(Player::getRating);
                break;
            default:
                comparator = Comparator.comparingInt(Player::getId);
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

        stats.put("totalPlayers", (long) playerDataPool.size());
        stats.put("averageRating", calculateAverageRating());
        stats.put("averageAge", playerDataPool.stream()
                .mapToInt(Player::getAge).average().orElse(0.0));

        Map<String, Long> playersByPosition = playerDataPool.stream()
                .collect(Collectors.groupingBy(Player::getPosition, Collectors.counting()));
        stats.put("playersByPosition", playersByPosition);

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
        return playerDataPool.size();
    }

    // --- VALIDATION ---

    private void validatePlayer(Player player) {
        List<String> errors = new ArrayList<>();

        if (player.getFirstName() == null || player.getFirstName().trim().isEmpty()) {
            errors.add("First name is required");
        }
        if (player.getLastName() == null || player.getLastName().trim().isEmpty()) {
            errors.add("Last name is required");
        }
        if (player.getAge() < 16 || player.getAge() > 50) {
            errors.add("Age must be between 16 and 50");
        }
        if (player.getPosition() == null || player.getPosition().trim().isEmpty()) {
            errors.add("Position is required");
        }
        if (player.getRating() < 0.0 || player.getRating() > 10.0) {
            errors.add("Rating must be between 0.0 and 10.0");
        }

        // Валидация команды (используем второй репозиторий)
        if (player.getTeamId() != null && player.getTeamId() != 0) {
            if (!teamRepository.existsById(player.getTeamId())) {
                errors.add("Team with ID " + player.getTeamId() + " does not exist");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Player", errors);
        }
    }
}