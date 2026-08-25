package com.cinema.cinema_gestion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void sendPasswordResetEmail_whenDisabled_doesNotSend() {
        MailService mailService = new MailService(mailSender, false, "noreply@cinema.local", "http://localhost:5173/");

        mailService.sendPasswordResetEmail("user@test.com", "token-123", LocalDateTime.now().plusMinutes(15));

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_whenEnabled_sendsLink() {
        MailService mailService = new MailService(mailSender, true, "noreply@cinema.local", "http://localhost:5173");
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        mailService.sendPasswordResetEmail("user@test.com", "token-123", expiresAt);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertThat(message.getFrom()).isEqualTo("noreply@cinema.local");
        assertThat(message.getTo()).containsExactly("user@test.com");
        assertThat(message.getSubject()).contains("Password reset");
        assertThat(message.getText()).contains("http://localhost:5173/reset-password?token=token-123");
        assertThat(message.getText()).contains(expiresAt.toString());
    }

    @Test
    void sendPasswordResetEmail_whenSmtpFails_throwsInternalError() {
        MailService mailService = new MailService(mailSender, true, "noreply@cinema.local", "http://localhost:5173");
        doThrow(new MailSendException("smtp down")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> mailService.sendPasswordResetEmail(
                        "user@test.com", "token-123", LocalDateTime.now().plusMinutes(15)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR))
                .hasMessageContaining("Unable to send reset email");
    }

    @Test
    void buildResetLink_stripsTrailingSlash() {
        MailService mailService = new MailService(mailSender, false, "noreply@cinema.local", "http://localhost:5173/");

        assertThat(mailService.buildResetLink("abc")).isEqualTo("http://localhost:5173/reset-password?token=abc");
    }
}
