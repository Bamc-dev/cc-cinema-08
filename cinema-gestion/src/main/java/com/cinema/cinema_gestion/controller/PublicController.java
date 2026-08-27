package com.cinema.cinema_gestion.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema_gestion.dto.publicapi.CinemaPublicView;
import com.cinema.cinema_gestion.dto.publicapi.MoviePublicView;
import com.cinema.cinema_gestion.dto.publicapi.MovieShowtimePublicView;
import com.cinema.cinema_gestion.dto.publicapi.MovieTodayView;
import com.cinema.cinema_gestion.service.PublicService;

/**
 * API catalogue public (sans JWT) : cinémas, films, programme du jour et horaires.
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final PublicService publicService;

    /**
     * @param publicService service du catalogue public
     */
    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    /**
     * Liste les cinémas, filtrable par nom ou ville.
     *
     * @param q recherche optionnelle
     * @return cinémas publics
     */
    @GetMapping("/cinemas")
    public ResponseEntity<List<CinemaPublicView>> listCinemas(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(publicService.findCinemas(q));
    }

    /**
     * Liste les films, filtrable par titre ou genre.
     *
     * @param q recherche optionnelle
     * @return films publics
     */
    @GetMapping("/movies")
    public ResponseEntity<List<MoviePublicView>> listMovies(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(publicService.findMovies(q));
    }

    /**
     * Programme du jour d'un cinéma : films et séances de la date courante.
     *
     * @param cinemaId identifiant du cinéma
     * @return films avec leurs horaires du jour
     */
    @GetMapping("/cinemas/{cinemaId}/today")
    public ResponseEntity<List<MovieTodayView>> cinemaToday(@PathVariable Long cinemaId) {
        return ResponseEntity.ok(publicService.findCinemaToday(cinemaId));
    }

    /**
     * Horaires d'un film, filtrables par date et par nom/ville de cinéma.
     *
     * @param movieId identifiant du film
     * @param date    jour (ISO), optionnel
     * @param q       filtre cinéma, optionnel
     * @return horaires publics
     */
    @GetMapping("/movies/{movieId}/showtimes")
    public ResponseEntity<List<MovieShowtimePublicView>> movieShowtimes(
            @PathVariable Long movieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(publicService.findMovieShowtimes(movieId, date, q));
    }
}
