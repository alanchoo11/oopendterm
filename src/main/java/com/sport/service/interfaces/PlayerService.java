package com.sport.service.interfaces;

import com.sport.domain.Player;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface PlayerService {

    Player createPlayer(Player player);

    Player getPlayerById(Integer id); // Integer!

    List<Player> getAllPlayers();

    Player updatePlayer(Player player);

    void deletePlayer(Integer id); // void обычно удобнее для сервиса

    List<Player> getPlayersByTeam(Integer teamId);

    List<Player> getPlayersByPosition(String position);

    List<Player> getTopRatedPlayers(int limit);

    List<Player> getFreeAgents();

    // --- Functional Programming (Requirement #8) ---

    List<Player> filterPlayers(Predicate<Player> predicate);

    List<Player> sortPlayers(String sortBy, boolean ascending);

    Map<String, Object> getPlayerStatistics();

    double calculateAverageRating();

    long countPlayers();
}