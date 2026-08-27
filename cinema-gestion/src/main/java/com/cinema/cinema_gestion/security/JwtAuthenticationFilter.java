package com.cinema.cinema_gestion.security;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cinema.cinema_gestion.service.CustomUserDetailsService;
import com.cinema.cinema_gestion.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;

import com.cinema.cinema_gestion.config.SecurityConfig;

/**
 * Filtre HTTP : valide le JWT Bearer et peuple le {@link SecurityContextHolder}.
 * Les chemins publics (propriété {@code security.route.public.paths}) sont ignorés.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    @Value("${security.route.public.paths}")
    private final String[] AUTH_PUBLIC_PATHS;

    /**
     * @param jwtService                validation et extraction du JWT
     * @param customUserDetailsService  chargement de l'utilisateur par id (sujet du token)
     * @param AUTH_PUBLIC_PATHS         motifs Ant des routes publiques
     */
    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService, @Value("${security.route.public.paths}") String[] AUTH_PUBLIC_PATHS) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.AUTH_PUBLIC_PATHS = AUTH_PUBLIC_PATHS;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isPublicPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String subject = jwtService.extractSubject(token);
        if (subject == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = customUserDetailsService.loadUserById(Long.valueOf(subject));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    /**
     * @param uri URI de la requête
     * @return {@code true} si l'URI correspond à un chemin public
     */
    private boolean isPublicPath(String uri) {
        for (String pattern : AUTH_PUBLIC_PATHS) {
            if (PATH_MATCHER.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }
}
