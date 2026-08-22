package com.cinema.cinema_gestion.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cinema.cinema_gestion.entity.security.User;

class JwtServiceTest {

    private static final String SECRET = "f96b674b372c0f25762e7384bd6b1a0f905941c04e0de26e63f72002759f96a0";

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 900_000L, 604_800_000L);
        user = new User();
        user.setId(42L);
        user.setEmail("user@test.com");
        user.setPassword("secret");
    }

    @Test
    void generateAccessToken_andExtractSubject() {
        String token = jwtService.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractSubject(token)).isEqualTo("42");
    }

    @Test
    void isTokenValid_returnsTrueForValidToken() {
        String token = jwtService.generateAccessToken(user);

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalseForInvalidToken() {
        assertThat(jwtService.isTokenValid("invalid.token.value")).isFalse();
    }

    @Test
    void getRefreshExpirationMs_returnsConfiguredValue() {
        assertThat(jwtService.getRefreshExpirationMs()).isEqualTo(604_800_000L);
    }
}
