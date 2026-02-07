package com.sport.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Player entity representing a sports player.
 * Implements Builder pattern for flexible object construction.
 */
public class Player {
    
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String position;
    private Double rating;
    private Long teamId;
    private Integer jerseyNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Private constructor - only Builder can create instances
    private Player(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.position = builder.position;
        this.rating = builder.rating;
        this.teamId = builder.teamId;
        this.jerseyNumber = builder.jerseyNumber;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }
    
    // Default constructor for JDBC
    public Player() {}
    
    // Convenience method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public String getPosition() {
        return position;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public Long getTeamId() {
        return teamId;
    }
    
    public Integer getJerseyNumber() {
        return jerseyNumber;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
    
    public void setJerseyNumber(Integer jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", position='" + position + '\'' +
                ", rating=" + rating +
                ", teamId=" + teamId +
                ", jerseyNumber=" + jerseyNumber +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id) &&
                Objects.equals(firstName, player.firstName) &&
                Objects.equals(lastName, player.lastName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }
    
    /**
     * Builder pattern implementation for Player class.
     * Provides flexible and readable object construction.
     */
    public static class Builder {
        private Long id;
        private String firstName;
        private String lastName;
        private Integer age;
        private String position;
        private Double rating;
        private Long teamId;
        private Integer jerseyNumber;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        
        public Builder age(Integer age) {
            this.age = age;
            return this;
        }
        
        public Builder position(String position) {
            this.position = position;
            return this;
        }
        
        public Builder rating(Double rating) {
            this.rating = rating;
            return this;
        }
        
        public Builder teamId(Long teamId) {
            this.teamId = teamId;
            return this;
        }
        
        public Builder jerseyNumber(Integer jerseyNumber) {
            this.jerseyNumber = jerseyNumber;
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
        
        public Player build() {
            return new Player(this);
        }
    }
}
