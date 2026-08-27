package com.cinema.cinema_gestion.dto.room;

/**
 * Vue administrative simplifiée d'une salle (identifiant, capacité et cinéma).
 */
public record RoomDTOView(Long id, Integer capacity, Long cinemaId) {

}
