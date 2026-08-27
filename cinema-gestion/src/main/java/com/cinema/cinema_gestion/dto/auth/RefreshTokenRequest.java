package com.cinema.cinema_gestion.dto.auth;

/**
 * Corps de la requête de renouvellement du jeton d'accès à partir d'un refresh token.
 */
public record RefreshTokenRequest(String refreshToken) {
}
