package com.cinema.cinema_gestion.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cinema.cinema_gestion.dto.auth.ForgotPasswordRequest;
import com.cinema.cinema_gestion.dto.auth.ForgotPasswordResponse;
import com.cinema.cinema_gestion.dto.auth.ResetPasswordRequest;
import com.cinema.cinema_gestion.entity.security.PasswordResetToken;
import com.cinema.cinema_gestion.entity.security.User;
import com.cinema.cinema_gestion.repository.PasswordResetTokenRepository;
import com.cinema.cinema_gestion.repository.UserRepository;

@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final long resetExpirationMs;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder,
            @Value("${security.jwt.reset-expiration-ms:900000}") long resetExpirationMs) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.resetExpirationMs = resetExpirationMs;
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.email() == null ? "" : request.email().trim();
        if (email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found for this email"));

        LocalDateTime expiresAt = LocalDateTime.now().plus(resetExpirationMs, ChronoUnit.MILLIS);
        PasswordResetToken resetToken = passwordResetTokenRepository.findByUser(user).orElseGet(PasswordResetToken::new);
        resetToken.setUser(user);
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiryDate(expiresAt);
        passwordResetTokenRepository.save(resetToken);

        // Pas de SMTP dans le projet : le token est renvoyé pour pouvoir tester via .http / Swagger.
        // En production, on l'enverrait par e-mail et on ne l'exposerait pas ici.
        return new ForgotPasswordResponse(
                resetToken.getToken(),
                expiresAt,
                "Password reset token generated. In production this would be sent by email.");
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.token() == null ? "" : request.token().trim();
        String newPassword = request.newPassword() == null ? "" : request.newPassword();
        if (token.isEmpty() || newPassword.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token and new password are required");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
        refreshTokenService.revokeByUser(user);
    }
}
