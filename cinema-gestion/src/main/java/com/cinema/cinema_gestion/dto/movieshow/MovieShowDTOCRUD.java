package com.cinema.cinema_gestion.dto.movieshow;

import com.cinema.cinema_gestion.dto.BaseDTO;
import java.util.Set;

public class MovieShowDTOCRUD extends BaseDTO {

    private Double price;
    private Long movieId;
    private Long roomId;
    private Set<Long> scheduleIds;

    public MovieShowDTOCRUD(Long id, Double price, Long movieId, Long roomId, Set<Long> scheduleIds) {
        super(id);
        this.price = price;
        this.movieId = movieId;
        this.roomId = roomId;
        this.scheduleIds = scheduleIds;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Set<Long> getScheduleIds() {
        return scheduleIds;
    }

    public void setScheduleIds(Set<Long> scheduleIds) {
        this.scheduleIds = scheduleIds;
    }
}
