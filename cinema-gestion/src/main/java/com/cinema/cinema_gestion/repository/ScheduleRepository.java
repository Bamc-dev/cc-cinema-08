package com.cinema.cinema_gestion.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cinema.cinema_gestion.entity.Schedule;

/**
 * Accès persistance des {@link Schedule}, avec requêtes pour l'API publique (cinéma, film, date).
 */
public interface ScheduleRepository extends GenericRepository<Schedule> {

    /**
     * Horaires d'un cinéma sur une journée, triés par heure de début.
     *
     * @param cinemaId   identifiant du cinéma
     * @param startOfDay début de journée (inclus)
     * @param endOfDay   fin de journée (exclus)
     * @return horaires du jour
     */
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

    /**
     * Tous les horaires d'un film, triés par heure de début.
     *
     * @param movieId identifiant du film
     * @return horaires du film
     */
    @Query("""
            SELECT s FROM Schedule s
            JOIN s.movieShow ms
            JOIN ms.movie m
            WHERE m.id = :movieId
            ORDER BY s.startTime
            """)
    List<Schedule> findShowtimesByMovieId(@Param("movieId") Long movieId);

    /**
     * Horaires d'un film sur une journée, triés par heure de début.
     *
     * @param movieId    identifiant du film
     * @param startOfDay début de journée (inclus)
     * @param endOfDay   fin de journée (exclus)
     * @return horaires du jour
     */
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

    /**
     * Horaires d'un film filtrés par nom ou ville de cinéma (LIKE insensible à la casse).
     *
     * @param movieId identifiant du film
     * @param q       fragment de nom ou de ville
     * @return horaires correspondants
     */
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

    /**
     * Horaires d'un film sur une journée, filtrés par nom ou ville de cinéma.
     *
     * @param movieId    identifiant du film
     * @param startOfDay début de journée (inclus)
     * @param endOfDay   fin de journée (exclus)
     * @param q          fragment de nom ou de ville
     * @return horaires correspondants
     */
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
