package com.cinema.cinema_gestion.dto.publicapi;

import java.time.LocalDateTime;

/**
 * Vue publique d'un horaire de projection (créneau, prix, salle et capacité).
 */
public record ShowtimePublicView(Long scheduleId, LocalDateTime startTime, LocalDateTime endTime, Double price,
        Long roomId, Integer roomCapacity) {
}
