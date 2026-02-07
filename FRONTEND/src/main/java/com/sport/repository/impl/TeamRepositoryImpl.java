package com.sport.repository.impl;

import com.sport.domain.Team;
import com.sport.exception.EntityNotFoundException;
import com.sport.repository.interfaces.TeamRepository;
import com.sport.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of TeamRepository.
 * Demonstrates CRUD operations using PreparedStatement.
 * This class is in the repository layer - all database logic is here.
 */
public class TeamRepositoryImpl implements TeamRepository {
    
    @Override
    public Team save(Team entity) {
        String sql = "INSERT INTO teams (name, sport, coach, location, founded_year, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) RETURNING id";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getSport());
            stmt.setString(3, entity.getCoach());
            stmt.setString(4, entity.getLocation());
            stmt.setObject(5, entity.getFoundedYear(), Types.INTEGER);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                entity.setId(rs.getLong("id"));
            }
            
            return entity;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save team: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Team> findById(Long id) {
        String sql = "SELECT * FROM teams WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToTeam(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find team by id: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Team> findAll() {
        String sql = "SELECT * FROM teams ORDER BY name";
        List<Team> teams = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                teams.add(mapResultSetToTeam(rs));
            }
            
            return teams;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all teams: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Team update(Team entity) {
        String sql = "UPDATE teams SET name = ?, sport = ?, coach = ?, location = ?, " +
                     "founded_year = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getSport());
            stmt.setString(3, entity.getCoach());
            stmt.setString(4, entity.getLocation());
            stmt.setObject(5, entity.getFoundedYear(), Types.INTEGER);
            stmt.setLong(6, entity.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new EntityNotFoundException("Team", entity.getId());
            }
            
            return entity;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update team: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM teams WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete team: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM teams WHERE id = ?";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check team existence: " + e.getMessage(), e);
        }
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM teams";
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count teams: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Team> findBySport(String sport) {
        String sql = "SELECT * FROM teams WHERE sport = ? ORDER BY name";
        List<Team> teams = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, sport);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                teams.add(mapResultSetToTeam(rs));
            }
            
            return teams;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find teams by sport: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Team> findByLocation(String location) {
        String sql = "SELECT * FROM teams WHERE location = ? ORDER BY name";
        List<Team> teams = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, location);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                teams.add(mapResultSetToTeam(rs));
            }
            
            return teams;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find teams by location: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Team> findByCoach(String coach) {
        String sql = "SELECT * FROM teams WHERE coach = ? ORDER BY name";
        List<Team> teams = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, coach);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                teams.add(mapResultSetToTeam(rs));
            }
            
            return teams;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find teams by coach: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Team> searchByName(String namePart) {
        String sql = "SELECT * FROM teams WHERE name ILIKE ? ORDER BY name";
        List<Team> teams = new ArrayList<>();
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + namePart + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                teams.add(mapResultSetToTeam(rs));
            }
            
            return teams;
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search teams by name: " + e.getMessage(), e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Team object.
     * Helper method to avoid code duplication.
     */
    private Team mapResultSetToTeam(ResultSet rs) throws SQLException {
        return new Team.Builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .sport(rs.getString("sport"))
                .coach(rs.getString("coach"))
                .location(rs.getString("location"))
                .foundedYear(rs.getObject("founded_year") != null ? 
                    rs.getInt("founded_year") : null)
                .createdAt(rs.getTimestamp("created_at") != null ? 
                    rs.getTimestamp("created_at").toLocalDateTime() : null)
                .updatedAt(rs.getTimestamp("updated_at") != null ? 
                    rs.getTimestamp("updated_at").toLocalDateTime() : null)
                .build();
    }
}
