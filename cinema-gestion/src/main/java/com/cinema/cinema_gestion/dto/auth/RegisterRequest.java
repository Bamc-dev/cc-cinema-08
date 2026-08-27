package com.cinema.cinema_gestion.dto.auth;

/**
 * Corps de la requête d'inscription d'un nouvel utilisateur.
 */
public record RegisterRequest(String email, String password) {
}
