package com.cinema.cinema_gestion.dto;

import java.time.LocalDateTime;

/**
 * Superclasse des DTO administratifs.
 * Porte l'identifiant et les horodatages d'audit transmis par l'API.
 */
public abstract class BaseDTO {
    /** Identifiant de la ressource, {@code null} à la création. */
    private Long id;
    /** Date et heure de création. */
    private LocalDateTime createdAt;
    /** Date et heure de la dernière mise à jour. */
    private LocalDateTime updatedAt;

    protected BaseDTO() {
        this(null);
    }

    protected BaseDTO(Long id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

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
}
