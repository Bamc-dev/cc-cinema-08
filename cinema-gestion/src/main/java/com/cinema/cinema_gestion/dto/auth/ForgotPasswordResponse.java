package com.cinema.cinema_gestion.dto.auth;

import java.time.LocalDateTime;

public record ForgotPasswordResponse(String resetToken, LocalDateTime expiresAt, String message) {
}
