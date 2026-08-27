package com.cinema.cinema_gestion.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Superclasse JPA mappée ({@code @MappedSuperclass}) pour les entités persistantes.
 * Fournit l'identifiant technique et les horodatages d'audit (création et dernière modification).
 */
@MappedSuperclass
public abstract class BaseEntity {
    /** Identifiant technique généré par la base de données. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Date et heure de création de l'enregistrement. */
    private LocalDateTime createdAt;
    /** Date et heure de la dernière mise à jour. */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Initialise {@code createdAt} à l'instant courant avant la première persistance.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Met à jour {@code updatedAt} à l'instant courant avant chaque modification en base.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
