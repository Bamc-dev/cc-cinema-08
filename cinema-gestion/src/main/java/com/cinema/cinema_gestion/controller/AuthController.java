package com.cinema.cinema_gestion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema_gestion.dto.auth.AuthResponse;
import com.cinema.cinema_gestion.dto.auth.LoginRequest;
import com.cinema.cinema_gestion.dto.auth.LogoutRequest;
import com.cinema.cinema_gestion.dto.auth.RefreshTokenRequest;
import com.cinema.cinema_gestion.dto.auth.RegisterRequest;
import com.cinema.cinema_gestion.service.AuthService;

/**
 * Endpoints d'authentification JWT : inscription, connexion, refresh et déconnexion.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * @param authService service d'authentification
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Crée un compte et renvoie une paire access / refresh token.
     *
     * @param request e-mail et mot de passe
     * @return tokens JWT
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authentifie un utilisateur existant.
     *
     * @param request identifiants
     * @return tokens JWT
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Émet un nouvel access token à partir d'un refresh token valide.
     *
     * @param request refresh token
     * @return nouvelle paire de tokens
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    /**
     * Révoque le refresh token de l'utilisateur.
     *
     * @param request refresh token à invalider
     * @return 204 No Content
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
