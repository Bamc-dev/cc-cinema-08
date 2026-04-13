package com.cinema.cinema_gestion.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema_gestion.dto.movie.MovieDTOCRUD;
import com.cinema.cinema_gestion.dto.movie.MovieDTOView;
import com.cinema.cinema_gestion.service.MovieService;

@RestController
@RequestMapping("/api/movie")
public class MovieController extends GenericCRUDController<MovieDTOCRUD, MovieDTOView, MovieService> {

    public MovieController(MovieService service) {
        super(service);
    }
}
