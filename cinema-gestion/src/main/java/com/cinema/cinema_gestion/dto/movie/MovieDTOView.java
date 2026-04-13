package com.cinema.cinema_gestion.dto.movie;

import java.time.LocalDate;
import com.cinema.cinema_gestion.enumerator.MovieGenre;

public record MovieDTOView(Long id, String title, LocalDate releaseDate, MovieGenre genre) {

}
