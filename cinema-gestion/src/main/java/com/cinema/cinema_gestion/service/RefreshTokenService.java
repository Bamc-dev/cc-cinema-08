package com.cinema.cinema_gestion.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.cinema_gestion.entity.security.RefreshToken;
import com.cinema.cinema_gestion.entity.security.User;
import com.cinema.cinema_gestion.repository.RefreshTokenRepository;

/**
 * Cycle de vie des refresh tokens opaques (un par utilisateur, rotation à chaque émission).
 */
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpirationMs;

    /**
     * @param refreshTokenRepository persistance des tokens
     * @param refreshExpirationMs    durée de vie en millisecondes
     */
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
            @Value("${security.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    /**
     * Crée ou remplace le refresh token de l'utilisateur.
     *
     * @param user titulaire
     * @return nouvelle valeur opaque
     */
    @Transactional
    public String createOrRotate(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElseGet(RefreshToken::new);
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plus(refreshExpirationMs, ChronoUnit.MILLIS));
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    /**
     * Vérifie existence et expiration, puis retourne l'utilisateur associé.
     *
     * @param token valeur opaque
     * @return utilisateur
     * @throws org.springframework.security.authentication.BadCredentialsException si invalide ou expiré
     */
    @Transactional
    public User verifyAndGetUser(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadCredentialsException("Refresh token expired");
        }
        return refreshToken.getUser();
    }

    /**
     * Supprime le refresh token de l'utilisateur (logout).
     *
     * @param user titulaire
     */
    @Transactional
    public void revokeByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
