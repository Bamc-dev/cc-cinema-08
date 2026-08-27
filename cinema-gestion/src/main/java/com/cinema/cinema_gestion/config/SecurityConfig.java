package com.cinema.cinema_gestion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import com.cinema.cinema_gestion.security.JwtAuthenticationFilter;

/**
 * Configuration Spring Security : session stateless, JWT, chemins publics et BCrypt.
 */
@Configuration
public class SecurityConfig {
    /** Ant-style paths autorisés sans authentification ({@code security.route.public.paths}). */
    @Value("${security.route.public.paths}")
    private String[] AUTH_PUBLIC_PATHS;   

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * @param jwtAuthenticationFilter filtre Bearer exécuté avant l'auth username/password
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Chaîne de filtres : CSRF désactivé, JWT, tout le reste authentifié.
     *
     * @param http builder HTTP Security
     * @return chaîne configurée
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * @return encodeur BCrypt pour les mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @param authenticationConfiguration configuration Spring Security
     * @return manager d'authentification (login)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
