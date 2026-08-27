package com.cinema.cinema_gestion.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.Set;

import com.cinema.cinema_gestion.enumerator.MovieGenre;

import java.util.HashSet;

/**
 * Entité représentant un film au catalogue.
 * Un film peut être projeté via plusieurs séances {@link MovieShow}.
 */
@Entity
public class Movie extends BaseEntity{
    /** Titre du film. */
    private String title;
    /** Date de sortie. */
    private LocalDate releaseDate;
    /** Genre cinématographique. */
    private MovieGenre genre;
    /** Séances associées à ce film (cascade et suppression des orphelins). */
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
