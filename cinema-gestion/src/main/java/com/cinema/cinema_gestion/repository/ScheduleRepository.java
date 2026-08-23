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
            WHERE m.id = :movieId
            ORDER BY s.startTime
            """)
    List<Schedule> findShowtimesByMovieId(@Param("movieId") Long movieId);

    @Query("""
            SELECT s FROM Schedule s
            JOIN s.movieShow ms
            JOIN ms.movie m
            WHERE m.id = :movieId
              AND s.startTime >= :startOfDay
              AND s.startTime < :endOfDay
            ORDER BY s.startTime
            """)
    List<Schedule> findShowtimesByMovieIdAndDate(@Param("movieId") Long movieId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Query("""
            SELECT s FROM Schedule s
            JOIN s.movieShow ms
            JOIN ms.movie m
            JOIN ms.room r
            JOIN r.cinema c
            WHERE m.id = :movieId
              AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(c.city) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY s.startTime
            """)
    List<Schedule> findShowtimesByMovieIdAndCinema(@Param("movieId") Long movieId,
            @Param("q") String q);

    @Query("""
            SELECT s FROM Schedule s
            JOIN s.movieShow ms
            JOIN ms.movie m
            JOIN ms.room r
            JOIN r.cinema c
            WHERE m.id = :movieId
              AND s.startTime >= :startOfDay
              AND s.startTime < :endOfDay
              AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(c.city) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY s.startTime
            """)
    List<Schedule> findShowtimesByMovieIdAndDateAndCinema(@Param("movieId") Long movieId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("q") String q);
}
