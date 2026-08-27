package com.cinema.cinema_gestion.dto.publicapi;

import java.util.List;

import com.cinema.cinema_gestion.enumerator.MovieGenre;

/**
 * Vue publique d'un film à l'affiche aujourd'hui, avec ses horaires de projection.
 */
public record MovieTodayView(Long id, String title, MovieGenre genre, List<ShowtimePublicView> showtimes) {
}
