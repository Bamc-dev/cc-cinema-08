package com.cinema.cinema_gestion.dto.movieshow;

import java.time.LocalDateTime;

public record ScheduleDTOView(Long id, Long movieShowId, LocalDateTime startTime, LocalDateTime endTime) {

}
