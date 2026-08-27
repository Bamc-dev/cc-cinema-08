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

/**
 * Inscription, connexion, renouvellement et révocation des sessions JWT.
 */
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param authenticationManager     authentification e-mail / mot de passe
     * @param customUserDetailsService  chargement de l'utilisateur
     * @param jwtService                émission des access tokens
     * @param refreshTokenService       rotation des refresh tokens
     * @param userRepository            persistance des comptes
     * @param passwordEncoder           hachage BCrypt
     */
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

    /**
     * Crée un compte {@code USER} et émet une paire de tokens.
     *
     * @param request e-mail et mot de passe
     * @return access et refresh tokens
     */
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

    /**
     * Authentifie l'utilisateur et émet une nouvelle paire de tokens (rotation du refresh).
     *
     * @param request identifiants
     * @return access et refresh tokens
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = customUserDetailsService.findEntityByEmail(request.email());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createOrRotate(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    /**
     * Vérifie le refresh token et émet une nouvelle paire.
     *
     * @param token refresh token opaque
     * @return nouveaux tokens
     */
    @Transactional
    public AuthResponse refresh(String token) {
        User user = refreshTokenService.verifyAndGetUser(token);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createOrRotate(user);
        return new AuthResponse(accessToken, refreshToken);
    }

    /**
     * Révoque le refresh token de l'utilisateur (déconnexion).
     *
     * @param refreshToken token à invalider
     */
    @Transactional
    public void logout(String refreshToken) {
        User user = refreshTokenService.verifyAndGetUser(refreshToken);
        refreshTokenService.revokeByUser(user);
    }
}
