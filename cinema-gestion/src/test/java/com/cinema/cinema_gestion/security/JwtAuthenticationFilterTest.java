package com.cinema.cinema_gestion.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.cinema.cinema_gestion.service.CustomUserDetailsService;
import com.cinema.cinema_gestion.service.JwtService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(
                jwtService,
                customUserDetailsService,
                new String[] { "/auth/register", "/auth/login", "/auth/refresh" });
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void publicPath_bypassesAuthentication() throws Exception {
        request.setRequestURI("/auth/login");

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).isTokenValid(org.mockito.ArgumentMatchers.anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void invalidToken_doesNotSetAuthentication() throws Exception {
        request.setRequestURI("/api/cinema/list/0/10");
        request.addHeader("Authorization", "Bearer invalid-token");
        when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void validToken_setsAuthentication() throws Exception {
        request.setRequestURI("/api/cinema/list/0/10");
        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractSubject("valid-token")).thenReturn("1");
        UserDetails userDetails = new User("user@test.com", "password",
                Collections.singletonList(() -> "ROLE_USER"));
        when(customUserDetailsService.loadUserById(1L)).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user@test.com");
    }
}
