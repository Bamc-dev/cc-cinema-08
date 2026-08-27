package com.cinema.cinema_gestion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema_gestion.dto.auth.ForgotPasswordRequest;
import com.cinema.cinema_gestion.dto.auth.ForgotPasswordResponse;
import com.cinema.cinema_gestion.dto.auth.ResetPasswordRequest;
import com.cinema.cinema_gestion.service.PasswordResetService;

/**
 * Endpoints publics de réinitialisation de mot de passe (e-mail + token).
 */
@RestController
@RequestMapping("/auth")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    /**
     * @param passwordResetService service de reset
     */
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * Envoie un e-mail contenant un lien de réinitialisation (si le compte existe).
     *
     * @param request adresse e-mail
     * @return message et date d'expiration du token
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.forgotPassword(request));
    }

    /**
     * Applique un nouveau mot de passe à partir du token reçu par e-mail.
     *
     * @param request token et nouveau mot de passe
     * @return 204 No Content
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}
