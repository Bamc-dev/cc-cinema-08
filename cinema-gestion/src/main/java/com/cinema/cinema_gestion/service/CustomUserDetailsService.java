package com.cinema.cinema_gestion.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.entity.security.User;
import com.cinema.cinema_gestion.repository.UserRepository;

/**
 * Pont Spring Security : charge un {@link UserDetails} depuis l'entité {@link User}.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * @param userRepository persistance des comptes
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge l'utilisateur par e-mail (login).
     *
     * @param email identifiant
     * @return détails Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return toUserDetails(user);
    }

    /**
     * Charge l'utilisateur par identifiant (sujet du JWT).
     *
     * @param userId identifiant
     * @return détails Spring Security
     */
    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return toUserDetails(user);
    }

    /**
     * @param email identifiant
     * @return entité JPA
     */
    public User findEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    /**
     * @param user entité persistée
     * @return {@link UserDetails} avec autorité {@code ROLE_*}
     */
    private UserDetails toUserDetails(User user) {
        String role = user.getRole() == null || user.getRole().isBlank() ? "USER" : user.getRole();
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(() -> "ROLE_" + role));
    }
}
