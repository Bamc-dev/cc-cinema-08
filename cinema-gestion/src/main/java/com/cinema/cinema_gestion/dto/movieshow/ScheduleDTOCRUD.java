package com.cinema.cinema_gestion.dto.movieshow;

import com.cinema.cinema_gestion.dto.BaseDTO;
import java.time.LocalDateTime;

public class ScheduleDTOCRUD extends BaseDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
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
