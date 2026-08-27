package com.cinema.cinema_gestion.entity.security;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;

/**
 * Entité représentant un jeton de rafraîchissement JWT.
 * Relation un-à-un avec {@link User} : un utilisateur ne possède qu'un jeton actif à la fois.
 */
@Entity
public class RefreshToken {
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

    /** Utilisateur propriétaire du jeton. */
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
