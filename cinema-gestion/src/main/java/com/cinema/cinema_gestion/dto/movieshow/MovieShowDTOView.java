package com.cinema.cinema_gestion.dto.movieshow;

import java.util.Set;

public record MovieShowDTOView(Long id, Double price, Long movieId, Long roomId, Set<Long> scheduleIds) {

}
