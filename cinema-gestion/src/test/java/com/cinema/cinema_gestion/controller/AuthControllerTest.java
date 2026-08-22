package com.cinema.cinema_gestion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.cinema.cinema_gestion.dto.auth.AuthResponse;
import com.cinema.cinema_gestion.dto.auth.LoginRequest;
import com.cinema.cinema_gestion.dto.auth.LogoutRequest;
import com.cinema.cinema_gestion.dto.auth.RefreshTokenRequest;
import com.cinema.cinema_gestion.dto.auth.RegisterRequest;
import com.cinema.cinema_gestion.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private final AuthResponse authResponse = new AuthResponse("access-token", "refresh-token");

    @Test
    void register_returnsAuthResponse() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest("user@test.com", "password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void login_returnsAuthResponse() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("user@test.com", "password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    void refresh_returnsAuthResponse() throws Exception {
        when(authService.refresh("refresh-token")).thenReturn(authResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RefreshTokenRequest("refresh-token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void logout_returnsNoContent() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LogoutRequest("refresh-token"))))
                .andExpect(status().isNoContent());

        verify(authService).logout("refresh-token");
    }
}
