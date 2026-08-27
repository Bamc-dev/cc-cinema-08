package com.cinema.cinema_gestion.dto.auth;

/**
 * Corps de la requête « mot de passe oublié » : e-mail du compte à réinitialiser.
 */
public record ForgotPasswordRequest(String email) {
}
