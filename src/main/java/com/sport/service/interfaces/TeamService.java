package com.sport.service.interfaces;

import com.sport.domain.Team;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface TeamService {

    Team createTeam(Team team);

    Team getTeamById(Integer id); // Integer!

    List<Team> getAllTeams();

    Team updateTeam(Team team);

    void deleteTeam(Integer id); // void, так удобнее для API

    List<Team> getTeamsBySport(String sport);

    List<Team> getTeamsByLocation(String location);

    // --- Functional Programming (Lambda & Streams) ---

    List<Team> filterTeams(Predicate<Team> predicate);

    List<Team> sortTeams(String sortBy, boolean ascending);

    Map<String, Object> getTeamStatistics();

    long countTeams();
}