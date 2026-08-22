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

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final PublicService publicService;

    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    @GetMapping("/cinemas")
    public ResponseEntity<List<CinemaPublicView>> listCinemas(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(publicService.findCinemas(q));
    }

    @GetMapping("/movies")
    public ResponseEntity<List<MoviePublicView>> listMovies(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(publicService.findMovies(q));
    }

    @GetMapping("/cinemas/{cinemaId}/today")
    public ResponseEntity<List<MovieTodayView>> cinemaToday(@PathVariable Long cinemaId) {
        return ResponseEntity.ok(publicService.findCinemaToday(cinemaId));
    }

    @GetMapping("/movies/{movieId}/showtimes")
    public ResponseEntity<List<MovieShowtimePublicView>> movieShowtimes(
            @PathVariable Long movieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(publicService.findMovieShowtimes(movieId, date, q));
    }
}
