package com.cinema.cinema_gestion.service;

import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.dto.movie.MovieDTOCRUD;
import com.cinema.cinema_gestion.dto.movie.MovieDTOView;
import com.cinema.cinema_gestion.entity.Movie;
import com.cinema.cinema_gestion.mapper.MovieMapper;
import com.cinema.cinema_gestion.repository.MovieRepository;

@Service
public class MovieService extends GenericService<Movie, MovieDTOCRUD, MovieDTOView, MovieMapper, MovieRepository> {

    public MovieService(MovieRepository repository, MovieMapper mapper) {
        super(repository, mapper);
    }
}
