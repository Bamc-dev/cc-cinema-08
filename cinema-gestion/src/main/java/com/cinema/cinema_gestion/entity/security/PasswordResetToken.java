package com.cinema.cinema_gestion.entity.security;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

/**
 * Entité représentant un jeton de réinitialisation de mot de passe.
 * Relation un-à-un avec {@link User} : un utilisateur ne possède qu'un jeton de reset à la fois.
 */
@Entity
public class PasswordResetToken {
    /** Identifiant technique. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Valeur opaque du jeton, unique. */
    @Column(nullable = false, unique = true)
    private String token;

    /** Date d'expiration du jeton. */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /** Utilisateur concerné par la réinitialisation. */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
