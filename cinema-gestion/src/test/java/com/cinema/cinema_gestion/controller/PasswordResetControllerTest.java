package com.cinema.cinema_gestion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.cinema.cinema_gestion.dto.auth.ForgotPasswordRequest;
import com.cinema.cinema_gestion.dto.auth.ForgotPasswordResponse;
import com.cinema.cinema_gestion.dto.auth.ResetPasswordRequest;
import com.cinema.cinema_gestion.service.PasswordResetService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PasswordResetService passwordResetService;

    private final ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse(
            "A password reset email has been sent.", LocalDateTime.now().plusMinutes(15));

    @Test
    void forgotPassword_returnsMessageWithoutToken() throws Exception {
        when(passwordResetService.forgotPassword(any(ForgotPasswordRequest.class))).thenReturn(forgotPasswordResponse);

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ForgotPasswordRequest("user@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("A password reset email has been sent."))
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andExpect(jsonPath("$.resetToken").doesNotExist());
    }

    @Test
    void resetPassword_returnsNoContent() throws Exception {
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ResetPasswordRequest("reset-token", "newpass"))))
                .andExpect(status().isNoContent());

        verify(passwordResetService).resetPassword(any(ResetPasswordRequest.class));
    }
}
