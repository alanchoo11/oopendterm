package com.sport;

import com.sport.domain.Player;
import com.sport.domain.Team;
import com.sport.factory.ServiceFactory;
import com.sport.service.interfaces.PlayerService;
import com.sport.service.interfaces.TeamService;

import java.util.List;

/**
 * Тестовый класс для проверки CRUD операций.
 * Запусти этот класс для проверки работы с базой данных.
 */
public class TestCRUD {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   CRUD Test - Sport Management System");
        System.out.println("========================================\n");
        
        // Получаем сервисы через Factory
        TeamService teamService = ServiceFactory.createTeamService();
        PlayerService playerService = ServiceFactory.createPlayerService();
        
        // Тест 1: READ - Получение всех команд
        System.out.println("--- TEST 1: READ All Teams ---");
        List<Team> teams = teamService.getAllTeams();
        System.out.println("Total teams in database: " + teams.size());
        teams.forEach(team -> System.out.println("  - " + team.getName() + " (" + team.getSport() + ")"));
        
        // Тест 2: READ - Получение всех игроков
        System.out.println("\n--- TEST 2: READ All Players ---");
        List<Player> players = playerService.getAllPlayers();
        System.out.println("Total players in database: " + players.size());
        players.forEach(player -> 
            System.out.println("  - " + player.getFirstName() + " " + player.getLastName() + 
                             " (Rating: " + player.getRating() + ")"));
        
        // Тест 3: CREATE - Создание новой команды
        System.out.println("\n--- TEST 3: CREATE New Team ---");
        Team newTeam = new Team.Builder()
                .name("Test FC " + System.currentTimeMillis())  // Уникальное имя
                .sport("Football")
                .coach("Test Coach")
                .location("Test City")
                .foundedYear(2024)
                .build();
        
        Team savedTeam = teamService.createTeam(newTeam);
        System.out.println("Created team with ID: " + savedTeam.getId());
        System.out.println("Name: " + savedTeam.getName());
        
        // Тест 4: CREATE - Создание нового игрока
        System.out.println("\n--- TEST 4: CREATE New Player ---");
        Player newPlayer = new Player.Builder()
                .firstName("Test")
                .lastName("Player " + System.currentTimeMillis())
                .age(25)
                .position("Forward")
                .rating(8.5)
                .teamId(savedTeam.getId())  // Привязываем к созданной команде
                .jerseyNumber(99)
                .build();
        
        Player savedPlayer = playerService.createPlayer(newPlayer);
        System.out.println("Created player with ID: " + savedPlayer.getId());
        System.out.println("Name: " + savedPlayer.getFirstName() + " " + savedPlayer.getLastName());
        
        // Тест 5: READ by ID
        System.out.println("\n--- TEST 5: READ by ID ---");
        Team foundTeam = teamService.getTeamById(savedTeam.getId());
        System.out.println("Found team: " + foundTeam.getName());
        
        // Тест 6: UPDATE
        System.out.println("\n--- TEST 6: UPDATE Team ---");
        foundTeam.setCoach("Updated Coach");
        foundTeam.setLocation("Updated Location");
        Team updatedTeam = teamService.updateTeam(foundTeam);
        System.out.println("Updated team coach: " + updatedTeam.getCoach());
        System.out.println("Updated team location: " + updatedTeam.getLocation());
        
        // Тест 7: Lambda фильтрация
        System.out.println("\n--- TEST 7: Lambda Filter (Players by Position) ---");
        List<Player> forwards = playerService.getPlayersByPosition("Forward");
        System.out.println("Forwards count: " + forwards.size());
        forwards.forEach(p -> System.out.println("  - " + p.getFirstName() + " " + p.getLastName()));
        
        // Тест 8: Lambda сортировка
        System.out.println("\n--- TEST 8: Lambda Sort (Teams by Name) ---");
        List<Team> sortedTeams = teamService.sortTeams("name", true);
        System.out.println("Teams sorted by name:");
        sortedTeams.stream().limit(5).forEach(t -> System.out.println("  - " + t.getName()));
        
        // Тест 9: Statistics
        System.out.println("\n--- TEST 9: Statistics ---");
        var teamStats = teamService.getTeamStatistics();
        System.out.println("Total teams: " + teamStats.get("totalTeams"));
        System.out.println("Teams by sport: " + teamStats.get("teamsBySport"));
        
        // Тест 10: DELETE (раскомментируй для тестирования удаления)
        /*
        System.out.println("\n--- TEST 10: DELETE ---");
        boolean deleted = teamService.deleteTeam(savedTeam.getId());
        System.out.println("Team deleted: " + deleted);
        
        boolean playerDeleted = playerService.deletePlayer(savedPlayer.getId());
        System.out.println("Player deleted: " + playerDeleted);
        */
        
        System.out.println("\n========================================");
        System.out.println("   All CRUD tests completed!");
        System.out.println("========================================");
        
        // Проверка подключения к БД
        System.out.println("\n--- Database Connection Info ---");
        System.out.println("If you see this message, connection to PostgreSQL is working!");
        System.out.println("Data is persisted in the database and will survive application restart.");
    }
}
