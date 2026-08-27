package com.cinema.cinema_gestion.dto.auth;

/**
 * Corps de la requête de réinitialisation : jeton reçu par e-mail et nouveau mot de passe.
 */
public record ResetPasswordRequest(String token, String newPassword) {
}
