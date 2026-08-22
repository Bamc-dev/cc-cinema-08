package com.cinema.cinema_gestion.dto.movie;

import com.cinema.cinema_gestion.dto.BaseDTO;
import com.cinema.cinema_gestion.enumerator.MovieGenre;
import java.time.LocalDate;
import java.util.Set;

public class MovieDTOCRUD extends BaseDTO {
    private String title;
    private LocalDate releaseDate;
    private MovieGenre genre;
    private Set<Long> movieShowIds;

    public MovieDTOCRUD() {
        super(null);
    }

    public MovieDTOCRUD(Long id, String title, LocalDate releaseDate, MovieGenre genre) {
        super(id);
        this.title = title;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }

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

    public Set<Long> getMovieShowIds() {
        return movieShowIds;
    }

    public void setMovieShowIds(Set<Long> movieShowIds) {
        this.movieShowIds = movieShowIds;
    }

}
