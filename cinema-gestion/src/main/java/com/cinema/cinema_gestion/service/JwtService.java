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

@Service
public class JwtService {
    private final String secret;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${security.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.secret = secret;
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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
