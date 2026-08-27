package com.cinema.cinema_gestion.dto.publicapi;

import java.time.LocalDate;

import com.cinema.cinema_gestion.enumerator.MovieGenre;

/**
 * Vue publique d'un film du catalogue (titre, date de sortie et genre).
 */
public record MoviePublicView(Long id, String title, LocalDate releaseDate, MovieGenre genre) {
}
