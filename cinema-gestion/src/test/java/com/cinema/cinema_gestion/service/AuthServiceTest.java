package com.cinema.cinema_gestion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.cinema.cinema_gestion.dto.auth.AuthResponse;
import com.cinema.cinema_gestion.dto.auth.LoginRequest;
import com.cinema.cinema_gestion.dto.auth.RegisterRequest;
import com.cinema.cinema_gestion.entity.security.User;
import com.cinema.cinema_gestion.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;
    private User user;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                authenticationManager,
                customUserDetailsService,
                jwtService,
                refreshTokenService,
                userRepository,
                passwordEncoder);
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setPassword("encoded");
    }

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest("user@test.com", "password");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token");
        when(refreshTokenService.createOrRotate(any(User.class))).thenReturn("refresh-token");

        AuthResponse response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_emptyEmail_throwsBadRequest() {
        RegisterRequest request = new RegisterRequest("", "password");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void register_duplicateEmail_throwsConflict() {
        RegisterRequest request = new RegisterRequest("user@test.com", "password");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("user@test.com", "password");
        when(customUserDetailsService.findEntityByEmail("user@test.com")).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(refreshTokenService.createOrRotate(user)).thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void refresh_success() {
        when(refreshTokenService.verifyAndGetUser("refresh-token")).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("new-access");
        when(refreshTokenService.createOrRotate(user)).thenReturn("new-refresh");

        AuthResponse response = authService.refresh("refresh-token");

        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isEqualTo("new-refresh");
    }

    @Test
    void logout_revokesRefreshToken() {
        when(refreshTokenService.verifyAndGetUser("refresh-token")).thenReturn(user);

        authService.logout("refresh-token");

        verify(refreshTokenService).revokeByUser(user);
    }
}
