package com.cinema.cinema_gestion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cinema.cinema_gestion.entity.security.User;
import com.cinema.cinema_gestion.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;
    private User user;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setPassword("encoded");
        user.setRole("USER");
    }

    @Test
    void loadUserByUsername_whenFound_returnsUserDetails() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserByUsername("user@test.com");

        assertThat(details.getUsername()).isEqualTo("user@test.com");
        assertThat(details.getPassword()).isEqualTo("encoded");
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_whenNotFound_throws() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("missing@test.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserById_whenFound_returnsUserDetails() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserById(1L);

        assertThat(details.getUsername()).isEqualTo("user@test.com");
    }

    @Test
    void findEntityByEmail_whenFound_returnsUser() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        User result = customUserDetailsService.findEntityByEmail("user@test.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void loadUserByUsername_whenRoleBlank_defaultsToUser() {
        user.setRole("");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserByUsername("user@test.com");

        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }
}
