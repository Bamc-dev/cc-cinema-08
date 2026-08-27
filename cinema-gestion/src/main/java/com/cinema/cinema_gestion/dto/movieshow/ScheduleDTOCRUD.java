package com.cinema.cinema_gestion.dto.movieshow;

import com.cinema.cinema_gestion.dto.BaseDTO;
import java.time.LocalDateTime;

/**
 * DTO CRUD d'un créneau horaire : payload d'entrée/sortie pour créer ou modifier un horaire.
 */
public class ScheduleDTOCRUD extends BaseDTO {
    /** Début de la projection. */
    private LocalDateTime startTime;
    /** Fin de la projection. */
    private LocalDateTime endTime;
    /** Identifiant de la séance parente. */
    private Long movieShowId;

    public ScheduleDTOCRUD() {
        super(null);
    }

    public ScheduleDTOCRUD(Long id, LocalDateTime startTime, LocalDateTime endTime, Long movieShowId) {
        super(id);
        this.startTime = startTime;
        this.endTime = endTime;
        this.movieShowId = movieShowId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getMovieShowId() {
        return movieShowId;
    }

    public void setMovieShowId(Long movieShowId) {
        this.movieShowId = movieShowId;
    }
}
