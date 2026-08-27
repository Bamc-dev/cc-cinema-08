package com.cinema.cinema_gestion.dto.movieshow;

import java.util.Set;

/**
 * Vue administrative d'une séance (prix, film, salle et identifiants d'horaires).
 */
public record MovieShowDTOView(Long id, Double price, Long movieId, Long roomId, Set<Long> scheduleIds) {

}
