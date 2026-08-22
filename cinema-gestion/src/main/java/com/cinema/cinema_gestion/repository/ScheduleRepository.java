package com.cinema.cinema_gestion.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cinema.cinema_gestion.entity.Schedule;

public interface ScheduleRepository extends GenericRepository<Schedule> {

    @Query("""
            SELECT s FROM Schedule s
            JOIN s.movieShow ms
            JOIN ms.room r
            WHERE r.cinema.id = :cinemaId
              AND s.startTime >= :startOfDay
              AND s.startTime < :endOfDay
            ORDER BY s.startTime
            """)
    List<Schedule> findByCinemaIdAndDay(@Param("cinemaId") Long cinemaId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Query("""
            SELECT s FROM Schedule s
            JOIN s.movieShow ms
            JOIN ms.movie m
            JOIN ms.room r
            JOIN r.cinema c
            WHERE m.id = :movieId
              AND (:startOfDay IS NULL OR s.startTime >= :startOfDay)
              AND (:endOfDay IS NULL OR s.startTime < :endOfDay)
              AND (:q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(c.city) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY s.startTime
            """)
    List<Schedule> findShowtimesByMovieId(@Param("movieId") Long movieId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("q") String q);
}
