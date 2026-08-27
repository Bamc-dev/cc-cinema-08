package com.cinema.cinema_gestion.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Envoi d'e-mails transactionnels. Si {@code app.mail.enabled} est faux, le lien est seulement loggué.
 */
@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;
    private final String frontendBaseUrl;

    /**
     * @param mailSender      client SMTP Spring
     * @param enabled         active réellement l'envoi
     * @param from            adresse expéditeur
     * @param frontendBaseUrl URL du SPA (lien de reset)
     */
    public MailService(
            JavaMailSender mailSender,
            @Value("${app.mail.enabled:false}") boolean enabled,
            @Value("${app.mail.from:noreply@cinema.local}") String from,
            @Value("${app.frontend.base-url:http://localhost:5173}") String frontendBaseUrl) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    /**
     * Envoie (ou loggue) le lien {@code /reset-password?token=...}.
     *
     * @param to        destinataire
     * @param token     token opaque
     * @param expiresAt date d'expiration affichée dans le corps
     */
    public void sendPasswordResetEmail(String to, String token, LocalDateTime expiresAt) {
        String resetLink = buildResetLink(token);
        String body = """
                A password reset was requested for your Cinema Gestion account.

                Open this link to choose a new password (valid until %s):
                %s

                If you did not request this, you can ignore this email.
                """.formatted(expiresAt, resetLink);

        if (!enabled) {
            log.info("Mail disabled. Password reset link for {}: {}", to, resetLink);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Password reset — Cinema Gestion");
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.error("Failed to send password reset email to {}", to, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send reset email");
        }
    }

    /**
     * @param token token de reset
     * @return URL frontend complète
     */
    String buildResetLink(String token) {
        String base = frontendBaseUrl.endsWith("/")
                ? frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1)
                : frontendBaseUrl;
        return base + "/reset-password?token=" + token;
    }
}
