package com.cinema.cinema_gestion.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.Set;
import java.util.HashSet;

@Entity
public class Room extends BaseEntity {
    private Integer capacity;
    private LocalDate constructionDate;
    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MovieShow> movieShows;
    
    public Integer getCapacity() {
        return capacity;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    public LocalDate getConstructionDate() {
        return constructionDate;
    }
    public void setConstructionDate(LocalDate constructionDate) {
        this.constructionDate = constructionDate;
    }
    public Cinema getCinema() {
        return cinema;
    }
    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }
    public Set<MovieShow> getMovieShows() {
        return movieShows;
    }
    public void setMovieShows(Set<MovieShow> movieShows) {
        this.movieShows = new HashSet<>(movieShows);
    }
}
