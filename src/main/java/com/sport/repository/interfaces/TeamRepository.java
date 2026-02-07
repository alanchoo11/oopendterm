package com.sport.repository.interfaces;

import com.sport.domain.Team;
import java.util.List;

// Внимание: поменял Long на Integer, так как в базе id - это int4 (Serial)
public interface TeamRepository extends CrudRepository<Team, Integer> {

    List<Team> findBySport(String sport);
    List<Team> findByLocation(String location);
    List<Team> findByCoach(String coach);
    List<Team> searchByName(String namePart);
}