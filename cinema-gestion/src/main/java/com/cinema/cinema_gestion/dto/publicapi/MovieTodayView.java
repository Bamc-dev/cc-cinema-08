package com.cinema.cinema_gestion.dto.publicapi;

import java.util.List;

import com.cinema.cinema_gestion.enumerator.MovieGenre;

public record MovieTodayView(Long id, String title, MovieGenre genre, List<ShowtimePublicView> showtimes) {
}
