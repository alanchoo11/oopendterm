package com.sport.repository.impl;

import com.sport.domain.Player;
import com.sport.exception.EntityNotFoundException;
import com.sport.repository.interfaces.PlayerRepository;
import com.sport.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerRepositoryImpl implements PlayerRepository {

    // SQL запросы
    private static final String INSERT_SQL =
            "INSERT INTO players (first_name, last_name, age, position, rating, team_id, jersey_number, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) RETURNING id";

    private static final String FIND_ALL = "SELECT * FROM players ORDER BY last_name, first_name";
    private static final String FIND_BY_ID = "SELECT * FROM players WHERE id = ?";
    private static final String UPDATE_SQL =
            "UPDATE players SET first_name=?, last_name=?, age=?, position=?, rating=?, team_id=?, jersey_number=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
    private static final String DELETE_SQL = "DELETE FROM players WHERE id=?";

    // SQL для поиска
    private static final String FIND_BY_TEAM = "SELECT * FROM players WHERE team_id = ? ORDER BY last_name";
    private static final String FIND_BY_POS = "SELECT * FROM players WHERE position = ? ORDER BY rating DESC";
    private static final String FIND_BY_RATING = "SELECT * FROM players WHERE rating >= ? ORDER BY rating DESC";
    private static final String FIND_AGE_BETWEEN = "SELECT * FROM players WHERE age BETWEEN ? AND ? ORDER BY age";
    private static final String SEARCH_NAME = "SELECT * FROM players WHERE first_name ILIKE ? OR last_name ILIKE ? ORDER BY last_name";
    private static final String FIND_FREE_AGENTS = "SELECT * FROM players WHERE team_id IS NULL ORDER BY rating DESC";

    @Override
    public Player save(Player player) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            setPlayerParams(stmt, player);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                player.setId(rs.getInt("id")); // Используем Integer!
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save player", e);
        }
        return player;
    }

    @Override
    public Optional<Player> findById(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToPlayer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Player> findAll() {
        return executeQuery(FIND_ALL);
    }

    @Override
    public Player update(Player player) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            setPlayerParams(stmt, player);
            stmt.setInt(8, player.getId()); // ID последний

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new EntityNotFoundException("Player", player.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update player", e);
        }
        return player;
    }

    @Override
    public boolean deleteById(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Реализация методов поиска ---

    @Override
    public List<Player> findByTeamId(Integer teamId) {
        List<Player> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_TEAM)) {
            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToPlayer(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Player> findByPosition(String position) {
        return executeQueryWithParam(FIND_BY_POS, position);
    }

    @Override
    public List<Player> findByRatingGreaterThan(Double minRating) {
        List<Player> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_RATING)) {
            stmt.setDouble(1, minRating);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToPlayer(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Player> findByAgeBetween(Integer minAge, Integer maxAge) {
        List<Player> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_AGE_BETWEEN)) {
            stmt.setInt(1, minAge);
            stmt.setInt(2, maxAge);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToPlayer(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Player> searchByName(String namePart) {
        List<Player> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_NAME)) {
            String pattern = "%" + namePart + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToPlayer(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Player> findFreeAgents() {
        return executeQuery(FIND_FREE_AGENTS);
    }

    // --- Вспомогательные методы ---

    private void setPlayerParams(PreparedStatement stmt, Player player) throws SQLException {
        stmt.setString(1, player.getFirstName());
        stmt.setString(2, player.getLastName());
        stmt.setInt(3, player.getAge());
        stmt.setString(4, player.getPosition());
        stmt.setDouble(5, player.getRating());

        // Обработка NULL для team_id (если игрок без команды)
        if (player.getTeamId() != null) {
            stmt.setInt(6, player.getTeamId());
        } else {
            stmt.setNull(6, Types.INTEGER);
        }

        stmt.setInt(7, player.getJerseyNumber());
    }

    private List<Player> executeQuery(String sql) {
        List<Player> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToPlayer(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private List<Player> executeQueryWithParam(String sql, String param) {
        List<Player> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, param);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToPlayer(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        // Убрали createdAt и updatedAt, так как их нет в классе Player
        return new Player.Builder()
                .id(rs.getInt("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .age(rs.getInt("age"))
                .position(rs.getString("position"))
                .rating(rs.getDouble("rating"))
                .teamId(rs.getObject("team_id") != null ? rs.getInt("team_id") : null)
                .jerseyNumber(rs.getInt("jersey_number"))
                .build();
    }

    // Заглушки
    @Override public boolean existsById(Integer id) { return findById(id).isPresent(); }
    @Override public long count() { return findAll().size(); }
}