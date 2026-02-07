package com.sport.repository.impl;

import com.sport.domain.Team;
import com.sport.exception.EntityNotFoundException;
import com.sport.repository.interfaces.TeamRepository;
import com.sport.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamRepositoryImpl implements TeamRepository {

    // SQL запросы
    private static final String INSERT_SQL = "INSERT INTO teams (name, sport, coach, location, founded_year) VALUES (?, ?, ?, ?, ?) RETURNING id";
    private static final String FIND_ALL = "SELECT * FROM teams ORDER BY name";
    private static final String FIND_BY_ID = "SELECT * FROM teams WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE teams SET name=?, sport=?, coach=?, location=?, founded_year=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
    private static final String DELETE_SQL = "DELETE FROM teams WHERE id=?";

    // Поиск
    private static final String FIND_BY_SPORT = "SELECT * FROM teams WHERE sport = ?";
    private static final String FIND_BY_LOCATION = "SELECT * FROM teams WHERE location = ?";
    private static final String FIND_BY_COACH = "SELECT * FROM teams WHERE coach = ?";
    private static final String SEARCH_BY_NAME = "SELECT * FROM teams WHERE name ILIKE ?";

    @Override
    public Team save(Team team) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, team.getName());
            stmt.setString(2, team.getSport());
            stmt.setString(3, team.getCoach());
            stmt.setString(4, team.getLocation());
            stmt.setInt(5, team.getFoundedYear()); // Используем getFoundedYear

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                team.setId(rs.getInt("id")); // ВАЖНО: getInt, а не getLong
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving team", e);
        }
        return team;
    }

    @Override
    public Optional<Team> findById(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Team> findAll() {
        List<Team> teams = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(FIND_ALL)) {
            while (rs.next()) {
                teams.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }

    @Override
    public Team update(Team team) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, team.getName());
            stmt.setString(2, team.getSport());
            stmt.setString(3, team.getCoach());
            stmt.setString(4, team.getLocation());
            stmt.setInt(5, team.getFoundedYear());
            stmt.setInt(6, team.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("Team", team.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return team;
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

    // Реализация методов поиска
    @Override public List<Team> findBySport(String sport) { return executeQuery(FIND_BY_SPORT, sport); }
    @Override public List<Team> findByLocation(String location) { return executeQuery(FIND_BY_LOCATION, location); }
    @Override public List<Team> findByCoach(String coach) { return executeQuery(FIND_BY_COACH, coach); }

    @Override
    public List<Team> searchByName(String namePart) {
        return executeQuery(SEARCH_BY_NAME, "%" + namePart + "%");
    }

    private List<Team> executeQuery(String sql, String param) {
        List<Team> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, param);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Team mapRow(ResultSet rs) throws SQLException {
        return new Team.Builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .sport(rs.getString("sport"))
                .coach(rs.getString("coach"))
                .location(rs.getString("location"))
                .foundedYear(rs.getInt("founded_year"))
                .build();
    }

    @Override public boolean existsById(Integer id) { return findById(id).isPresent(); }
    @Override public long count() { return findAll().size(); }
}