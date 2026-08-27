package com.cinema.cinema_gestion.dto.movieshow;

import java.time.LocalDateTime;

/**
 * Vue administrative d'un créneau horaire (séance, début et fin).
 */
public record ScheduleDTOView(Long id, Long movieShowId, LocalDateTime startTime, LocalDateTime endTime) {

}
