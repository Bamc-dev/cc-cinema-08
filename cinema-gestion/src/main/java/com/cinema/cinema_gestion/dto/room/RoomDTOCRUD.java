package com.cinema.cinema_gestion.dto.room;

import java.time.LocalDate;

import com.cinema.cinema_gestion.dto.BaseDTO;
import java.util.Set;

/**
 * DTO CRUD d'une salle : payload d'entrée/sortie pour créer ou modifier une salle.
 */
public class RoomDTOCRUD extends BaseDTO {

    public RoomDTOCRUD() {
        super(null);
    }

    public RoomDTOCRUD(Long id, Integer capacity, LocalDate constructionDate, Long cinemaId) {
        super(id);
        this.capacity = capacity;
        this.constructionDate = constructionDate;
        this.cinemaId = cinemaId;
    }

    /** Nombre de places assises. */
    private Integer capacity;
    /** Date de construction. */
    private LocalDate constructionDate;
    /** Identifiant du cinéma propriétaire. */
    private Long cinemaId;
    /** Identifiants des séances programmées dans cette salle. */
    private Set<Long> movieShowIds;

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LocalDate getConstructionDate() {
        return constructionDate;
    }

    public void setConstructionDate(LocalDate constructionDate) {
        this.constructionDate = constructionDate;
    }

    public Long getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Long cinemaId) {
        this.cinemaId = cinemaId;
    }

    public Set<Long> getMovieShowIds() {
        return movieShowIds;
    }

    public void setMovieShowIds(Set<Long> movieShowIds) {
        this.movieShowIds = movieShowIds;
    }

}
