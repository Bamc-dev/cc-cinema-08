package com.cinema.cinema_gestion.dto.auth;

import java.time.LocalDateTime;

public record ForgotPasswordResponse(String message, LocalDateTime expiresAt) {
}
