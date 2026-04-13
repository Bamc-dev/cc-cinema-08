package com.cinema.cinema_gestion.service;

import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.dto.movieshow.MovieShowDTOCRUD;
import com.cinema.cinema_gestion.dto.movieshow.MovieShowDTOView;
import com.cinema.cinema_gestion.entity.MovieShow;
import com.cinema.cinema_gestion.mapper.MovieShowMapper;
import com.cinema.cinema_gestion.repository.MovieShowRepository;

@Service
public class MovieShowService extends GenericService<MovieShow, MovieShowDTOCRUD, MovieShowDTOView, MovieShowMapper, MovieShowRepository> {
    public MovieShowService(MovieShowRepository repository, MovieShowMapper mapper) {
        super(repository, mapper);
    }
}
