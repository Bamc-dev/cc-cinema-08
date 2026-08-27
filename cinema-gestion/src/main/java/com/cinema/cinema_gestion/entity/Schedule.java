package com.cinema.cinema_gestion.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

/**
 * Entité représentant un créneau horaire de projection.
 * Un horaire appartient à une séance {@link MovieShow}.
 */
@Entity
public class Schedule extends BaseEntity{
    /** Début de la projection. */
    private LocalDateTime startTime;
    /** Fin de la projection. */
    private LocalDateTime endTime;
    
    /** Séance à laquelle ce créneau est rattaché. */
    @ManyToOne
    @JoinColumn(name = "movie_show_id")
    private MovieShow movieShow;
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public MovieShow getMovieShow() {
        return movieShow;
    }
    public void setMovieShow(MovieShow movieShow) {
        this.movieShow = movieShow;
    }
    
    
}
