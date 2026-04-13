package com.cinema.cinema_gestion.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema_gestion.dto.movieshow.MovieShowDTOCRUD;
import com.cinema.cinema_gestion.dto.movieshow.MovieShowDTOView;
import com.cinema.cinema_gestion.service.MovieShowService;

@RestController
@RequestMapping("/api/movie-show")
public class MovieShowController extends GenericCRUDController<MovieShowDTOCRUD, MovieShowDTOView, MovieShowService> {

    public MovieShowController(MovieShowService service) {
        super(service);
    }
}
