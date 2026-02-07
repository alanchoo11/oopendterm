package com.sport.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Team entity representing a sports team.
 * Implements Builder pattern for flexible object construction.
 */
public class Team {

    // ВАЖНО: Integer, чтобы совпадало с базой (SERIAL) и репозиториями
    private Integer id;
    private String name;
    private String sport;
    private String coach;
    private String location;
    private Integer foundedYear;

    // Даты оставляем, они полезны
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Private constructor - only Builder can create instances
    private Team(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.sport = builder.sport;
        this.coach = builder.coach;
        this.location = builder.location;
        this.foundedYear = builder.foundedYear;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // Default constructor for JDBC
    public Team() {}

    // Getters (Обрати внимание на Integer)
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSport() {
        return sport;
    }

    public String getCoach() {
        return coach;
    }

    public String getLocation() {
        return location;
    }

    public Integer getFoundedYear() {
        return foundedYear;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sport='" + sport + '\'' +
                ", coach='" + coach + '\'' +
                ", location='" + location + '\'' +
                ", foundedYear=" + foundedYear +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id) &&
                Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    /**
     * Builder pattern implementation.
     */
    public static class Builder {
        private Integer id;
        private String name;
        private String sport;
        private String coach;
        private String location;
        private Integer foundedYear;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder sport(String sport) {
            this.sport = sport;
            return this;
        }

        public Builder coach(String coach) {
            this.coach = coach;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder foundedYear(Integer foundedYear) {
            this.foundedYear = foundedYear;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Team build() {
            return new Team(this);
        }
    }
}