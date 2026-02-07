package com.sport.repository.interfaces;

import com.sport.domain.Player;
import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

    /**
     * Найти игроков по ID команды.
     */
    List<Player> findByTeamId(Integer teamId);

    /**
     * Найти игроков по позиции.
     */
    List<Player> findByPosition(String position);

    /**
     * Найти игроков с рейтингом выше указанного.
     */
    List<Player> findByRatingGreaterThan(Double minRating);

    /**
     * Найти игроков в диапазоне возраста.
     */
    List<Player> findByAgeBetween(Integer minAge, Integer maxAge);

    /**
     * Поиск по имени или фамилии (частичное совпадение).
     */
    List<Player> searchByName(String namePart);

    /**
     * Найти свободных агентов (без команды).
     */
    List<Player> findFreeAgents();
}