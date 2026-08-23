package com.cinema.cinema_gestion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.cinema.cinema_gestion.dto.auth.ForgotPasswordRequest;
import com.cinema.cinema_gestion.dto.auth.ForgotPasswordResponse;
import com.cinema.cinema_gestion.dto.auth.ResetPasswordRequest;
import com.cinema.cinema_gestion.entity.security.PasswordResetToken;
import com.cinema.cinema_gestion.entity.security.User;
import com.cinema.cinema_gestion.repository.PasswordResetTokenRepository;
import com.cinema.cinema_gestion.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordResetService passwordResetService;
    private User user;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(
                userRepository,
                passwordResetTokenRepository,
                refreshTokenService,
                passwordEncoder,
                900_000L);
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setPassword("old-encoded");
    }

    @Test
    void forgotPassword_whenEmailEmpty_throwsBadRequest() {
        assertThatThrownBy(() -> passwordResetService.forgotPassword(new ForgotPasswordRequest("  ")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email is required");
    }

    @Test
    void forgotPassword_whenUnknownEmail_throwsNotFound() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.forgotPassword(new ForgotPasswordRequest("missing@test.com")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No account found");
    }

    @Test
    void forgotPassword_whenUserExists_createsResetToken() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(Optional.empty());
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ForgotPasswordResponse response = passwordResetService.forgotPassword(new ForgotPasswordRequest("user@test.com"));

        assertThat(response.resetToken()).isNotBlank();
        assertThat(response.expiresAt()).isAfter(LocalDateTime.now());
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    void resetPassword_whenTokenMissing_throwsBadRequest() {
        assertThatThrownBy(() -> passwordResetService.resetPassword(new ResetPasswordRequest("", "newpass")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Token and new password are required");
    }

    @Test
    void resetPassword_whenInvalidToken_throwsBadRequest() {
        when(passwordResetTokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.resetPassword(new ResetPasswordRequest("bad-token", "newpass")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid reset token");
    }

    @Test
    void resetPassword_whenExpiredToken_deletesAndThrows() {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken("expired");
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(passwordResetTokenRepository.findByToken("expired")).thenReturn(Optional.of(resetToken));

        assertThatThrownBy(() -> passwordResetService.resetPassword(new ResetPasswordRequest("expired", "newpass")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Reset token expired");

        verify(passwordResetTokenRepository).delete(resetToken);
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_whenValid_updatesPasswordAndRevokesSessions() {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken("valid-token");
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        when(passwordResetTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("newpass")).thenReturn("new-encoded");

        passwordResetService.resetPassword(new ResetPasswordRequest("valid-token", "newpass"));

        assertThat(user.getPassword()).isEqualTo("new-encoded");
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).delete(resetToken);
        verify(refreshTokenService).revokeByUser(user);
    }
}
