package com.cinema.cinema_gestion.dto.auth;

/**
 * Corps de la requête de déconnexion : le refresh token à invalider.
 */
public record LogoutRequest(String refreshToken) {

}
