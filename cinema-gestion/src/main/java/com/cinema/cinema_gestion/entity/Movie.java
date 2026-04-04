package com.cinema.cinema_gestion.entity;

import java.time.LocalDate;

import com.cinema.cinema_gestion.entity.enumerator.MovieGenre;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.Set;
import java.util.HashSet;

@Entity
public class Movie extends BaseEntity{
    private String title;
    private LocalDate releaseDate;
    private MovieGenre genre;
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MovieShow> movieShows = new HashSet<>();
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    public MovieGenre getGenre() {
        return genre;
    }
    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }
    public Set<MovieShow> getMovieShows() {
        return movieShows;
    }
    public void setMovieShows(Set<MovieShow> movieShows) {
        this.movieShows = new HashSet<>(movieShows);
    }
}
