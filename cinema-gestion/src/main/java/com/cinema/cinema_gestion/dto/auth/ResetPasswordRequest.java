package com.cinema.cinema_gestion.dto.auth;

public record ResetPasswordRequest(String token, String newPassword) {
}
