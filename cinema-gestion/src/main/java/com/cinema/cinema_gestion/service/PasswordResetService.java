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

/**
 * Demande et application d'une réinitialisation de mot de passe par e-mail.
 */
@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final long resetExpirationMs;

    /**
     * @param userRepository                 comptes
     * @param passwordResetTokenRepository   tokens de reset
     * @param refreshTokenService            révocation des sessions après reset
     * @param passwordEncoder                hachage du nouveau mot de passe
     * @param mailService                    envoi de l'e-mail
     * @param resetExpirationMs              durée de vie du token
     */
    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            @Value("${security.jwt.reset-expiration-ms:900000}") long resetExpirationMs) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.resetExpirationMs = resetExpirationMs;
    }

    /**
     * Génère un token, l'associe à l'utilisateur et envoie le lien de reset.
     *
     * @param request e-mail du compte
     * @return message de confirmation et date d'expiration
     */
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

        mailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken(), expiresAt);

        return new ForgotPasswordResponse("A password reset email has been sent.", expiresAt);
    }

    /**
     * Applique le nouveau mot de passe, consomme le token et révoque les refresh tokens.
     *
     * @param request token et nouveau mot de passe
     */
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
