package com.cinema.cinema_gestion;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.cinema.cinema_gestion.dto.auth.LoginRequest;
import com.cinema.cinema_gestion.dto.auth.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_isPublic() throws Exception {
        RegisterRequest request = new RegisterRequest("integration@test.com", "password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void protectedEndpoint_withoutToken_isForbidden() throws Exception {
        mockMvc.perform(get("/api/cinema/list/0/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void publicEndpoint_withoutToken_isAllowed() throws Exception {
        mockMvc.perform(get("/api/public/cinemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void protectedEndpoint_withValidToken_isAllowed() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("secured@test.com", "password123");
        String registerResponse = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode tokens = objectMapper.readTree(registerResponse);
        String accessToken = tokens.get("accessToken").asText();

        mockMvc.perform(get("/api/cinema/list/0/10")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void login_afterRegister_returnsTokens() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("login@test.com", "password123");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest("login@test.com", "password123");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
