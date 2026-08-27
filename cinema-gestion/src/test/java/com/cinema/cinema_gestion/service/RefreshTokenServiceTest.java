package com.cinema.cinema_gestion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import com.cinema.cinema_gestion.entity.security.RefreshToken;
import com.cinema.cinema_gestion.entity.security.User;
import com.cinema.cinema_gestion.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;
    private User user;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, 604_800_000L);
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
    }

    @Test
    void createOrRotate_whenNoExistingToken_createsNewToken() {
        when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = refreshTokenService.createOrRotate(user);

        assertThat(token).isNotBlank();
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        RefreshToken saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getExpiryDate())
                .isAfter(LocalDateTime.now().plusDays(6))
                .isBefore(LocalDateTime.now().plusDays(8));
    }

    @Test
    void createOrRotate_whenExistingToken_rotatesToken() {
        RefreshToken existing = new RefreshToken();
        existing.setUser(user);
        existing.setToken("old-token");
        existing.setExpiryDate(LocalDateTime.now().plusDays(1));
        when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.of(existing));
        when(refreshTokenRepository.save(existing)).thenReturn(existing);

        String token = refreshTokenService.createOrRotate(user);

        assertThat(token).isNotBlank().isNotEqualTo("old-token");
        assertThat(existing.getExpiryDate())
                .isAfter(LocalDateTime.now().plusDays(6))
                .isBefore(LocalDateTime.now().plusDays(8));
        verify(refreshTokenRepository).save(existing);
    }

    @Test
    void verifyAndGetUser_whenValid_returnsUser() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("valid-token");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        when(refreshTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(refreshToken));

        User result = refreshTokenService.verifyAndGetUser("valid-token");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void verifyAndGetUser_whenInvalid_throwsBadCredentials() {
        when(refreshTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.verifyAndGetUser("missing"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void verifyAndGetUser_whenExpired_deletesAndThrows() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("expired-token");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> refreshTokenService.verifyAndGetUser("expired-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Refresh token expired");

        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void revokeByUser_deletesByUser() {
        refreshTokenService.revokeByUser(user);

        verify(refreshTokenRepository).deleteByUser(user);
    }
}
