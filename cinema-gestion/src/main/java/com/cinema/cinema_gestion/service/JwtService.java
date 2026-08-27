package com.cinema.cinema_gestion.service;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import com.cinema.cinema_gestion.entity.security.User;

/**
 * Émission et validation des JWT d'accès (sujet = identifiant utilisateur).
 */
@Service
public class JwtService {
    private final String secret;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    /**
     * @param secret              secret HMAC (Base64 ou brut)
     * @param accessExpirationMs  durée de vie de l'access token
     * @param refreshExpirationMs durée de vie du refresh token (exposé aux autres services)
     */
    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${security.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.secret = secret;
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    /**
     * @param user utilisateur authentifié
     * @return JWT d'accès signé
     */
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * @param token JWT
     * @return sujet (id utilisateur) ou lève si le token est invalide
     */
    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * @param token JWT
     * @return {@code true} si signature et expiration sont valides
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return durée de vie du refresh token en millisecondes
     */
    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }
}
