package com.cinema.cinema_gestion.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Schedule extends BaseEntity{
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
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
