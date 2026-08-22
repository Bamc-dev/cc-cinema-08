package com.cinema.cinema_gestion.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cinema.cinema_gestion.dto.auth.AuthResponse;
import com.cinema.cinema_gestion.dto.auth.LoginRequest;
import com.cinema.cinema_gestion.dto.auth.RegisterRequest;
import com.cinema.cinema_gestion.entity.security.User;
import com.cinema.cinema_gestion.repository.UserRepository;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            CustomUserDetailsService customUserDetailsService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email() == null ? "" : request.email().trim();
        String password = request.password() == null ? "" : request.password();
        if (email.isEmpty() || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createOrRotate(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = customUserDetailsService.findEntityByEmail(request.email());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createOrRotate(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refresh(String token) {
        User user = refreshTokenService.verifyAndGetUser(token);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createOrRotate(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        User user = refreshTokenService.verifyAndGetUser(refreshToken);
        refreshTokenService.revokeByUser(user);
    }
}
