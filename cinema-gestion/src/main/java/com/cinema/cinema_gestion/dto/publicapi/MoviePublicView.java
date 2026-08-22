package com.cinema.cinema_gestion.dto.publicapi;

import java.time.LocalDate;

import com.cinema.cinema_gestion.enumerator.MovieGenre;

public record MoviePublicView(Long id, String title, LocalDate releaseDate, MovieGenre genre) {
}
