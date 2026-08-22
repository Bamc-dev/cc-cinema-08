package com.cinema.cinema_gestion.dto.publicapi;

import java.time.LocalDateTime;

public record ShowtimePublicView(Long scheduleId, LocalDateTime startTime, LocalDateTime endTime, Double price,
        Long roomId, Integer roomCapacity) {
}
