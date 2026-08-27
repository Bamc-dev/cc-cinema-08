package com.cinema.cinema_gestion.dto.auth;

/**
 * Corps de la requête de connexion (identifiants e-mail / mot de passe).
 */
public record LoginRequest(String email, String password) {
}
