package com.cinema.cinema_gestion.dto.publicapi;

import java.time.LocalDateTime;

public record MovieShowtimePublicView(Long scheduleId, Long cinemaId, String cinemaName, String cinemaCity,
        Double price, Long roomId, Integer roomCapacity, LocalDateTime startTime, LocalDateTime endTime) {
}
