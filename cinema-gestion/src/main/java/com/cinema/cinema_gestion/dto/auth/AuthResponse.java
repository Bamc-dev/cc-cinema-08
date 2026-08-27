package com.cinema.cinema_gestion.dto.auth;

/**
 * Réponse d'authentification : jeton d'accès JWT et jeton de rafraîchissement.
 */
public record AuthResponse(String accessToken, String refreshToken) {
}
