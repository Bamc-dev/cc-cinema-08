package com.cinema.cinema_gestion.dto.auth;

import java.time.LocalDateTime;

/**
 * Réponse à une demande de réinitialisation : message informatif et date d'expiration du jeton.
 */
public record ForgotPasswordResponse(String message, LocalDateTime expiresAt) {
}
