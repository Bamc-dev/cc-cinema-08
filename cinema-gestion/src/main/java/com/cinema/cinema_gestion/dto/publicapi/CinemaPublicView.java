package com.cinema.cinema_gestion.dto.publicapi;

/**
 * Vue publique d'un cinéma : identité et adresse complète, sans données d'administration.
 */
public record CinemaPublicView(Long id, String name, String city, String street, String number) {
}
