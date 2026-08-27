package com.cinema.cinema_gestion.dto.movie;

import java.time.LocalDate;
import com.cinema.cinema_gestion.enumerator.MovieGenre;

/**
 * Vue administrative d'un film (identifiant, titre, date de sortie et genre).
 */
public record MovieDTOView(Long id, String title, LocalDate releaseDate, MovieGenre genre) {

}
