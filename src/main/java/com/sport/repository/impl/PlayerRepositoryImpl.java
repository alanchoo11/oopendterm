package com.sport.repository.impl;

import com.sport.domain.Player;
import com.sport.exception.EntityNotFoundException;
import com.sport.repository.interfaces.PlayerRepository;
import com.sport.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of PlayerRepository.
 * Demonstrates CRUD operations using PreparedStatement.
 * This class is in the repository layer - all database logic is here.
 */
public class PlayerRepositoryImpl implements PlayerRepository {
    
    @Override
    public Player save(Player entity) {
        String sql = "INSERT INTO players (first_name, last_name, age, position, rating, team_id, jersey_number, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) RETURNING id";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, entity.getFirstName());
            stmt.setString(2, entity.getLastName());
            stmt.setInt(3, entity.getAge());
            stmt.setString(4, entity.getPosition());
            stmt.setDouble(5, entity.getRating());
            stmt.setObject(6, entity.getTeamId(), Types.BIGINT);
            stmt.setObject(7, entity.getJerseyNumber(), Types.INTEGER);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                entity.setId(rs.getLong("id"));
            }
            
            return entity;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save player: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Player> findById(Long id) {
        String sql = "SELECT * FROM players WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPlayer(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find player by id: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Player> findAll() {
        String sql = "SELECT * FROM players ORDER BY last_name, first_name";
        List<Player> players = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
            
            return players;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all players: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Player update(Player entity) {
        String sql = "UPDATE players SET first_name = ?, last_name = ?, age = ?, position = ?, " +
                     "rating = ?, team_id = ?, jersey_number = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, entity.getFirstName());
            stmt.setString(2, entity.getLastName());
            stmt.setInt(3, entity.getAge());
            stmt.setString(4, entity.getPosition());
            stmt.setDouble(5, entity.getRating());
            stmt.setObject(6, entity.getTeamId(), Types.BIGINT);
            stmt.setObject(7, entity.getJerseyNumber(), Types.INTEGER);
            stmt.setLong(8, entity.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new EntityNotFoundException("Player", entity.getId());
            }
            
            return entity;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update player: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM players WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete player: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM players WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check player existence: " + e.getMessage(), e);
        }
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM players";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count players: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Player> findByTeamId(Long teamId) {
        String sql = "SELECT * FROM players WHERE team_id = ? ORDER BY last_name, first_name";
        List<Player> players = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, teamId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
            
            return players;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find players by team: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Player> findByPosition(String position) {
        String sql = "SELECT * FROM players WHERE position = ? ORDER BY rating DESC";
        List<Player> players = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, position);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
            
            return players;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find players by position: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Player> findByRatingGreaterThan(Double minRating) {
        String sql = "SELECT * FROM players WHERE rating >= ? ORDER BY rating DESC";
        List<Player> players = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setDouble(1, minRating);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
            
            return players;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find players by rating: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Player> findByAgeBetween(Integer minAge, Integer maxAge) {
        String sql = "SELECT * FROM players WHERE age BETWEEN ? AND ? ORDER BY age";
        List<Player> players = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, minAge);
            stmt.setInt(2, maxAge);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
            
            return players;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find players by age range: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Player> searchByName(String namePart) {
        String sql = "SELECT * FROM players WHERE first_name ILIKE ? OR last_name ILIKE ? ORDER BY last_name, first_name";
        List<Player> players = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            String searchPattern = "%" + namePart + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
            
            return players;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search players by name: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Player> findFreeAgents() {
        String sql = "SELECT * FROM players WHERE team_id IS NULL ORDER BY rating DESC";
        List<Player> players = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
            
            return players;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find free agents: " + e.getMessage(), e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Player object.
     * Helper method to avoid code duplication.
     */
    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        return new Player.Builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .age(rs.getInt("age"))
                .position(rs.getString("position"))
                .rating(rs.getDouble("rating"))
                .teamId(rs.getObject("team_id") != null ? rs.getLong("team_id") : null)
                .jerseyNumber(rs.getObject("jersey_number") != null ? rs.getInt("jersey_number") : null)
                .createdAt(rs.getTimestamp("created_at") != null ? 
                    rs.getTimestamp("created_at").toLocalDateTime() : null)
                .updatedAt(rs.getTimestamp("updated_at") != null ? 
                    rs.getTimestamp("updated_at").toLocalDateTime() : null)
                .build();
    }
}
