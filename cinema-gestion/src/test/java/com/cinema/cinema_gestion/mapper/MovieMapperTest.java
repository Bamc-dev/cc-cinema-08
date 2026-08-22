package com.cinema.cinema_gestion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.cinema.cinema_gestion.dto.movie.MovieDTOCRUD;
import com.cinema.cinema_gestion.entity.Movie;
import com.cinema.cinema_gestion.entity.MovieShow;
import com.cinema.cinema_gestion.enumerator.MovieGenre;

class MovieMapperTest {

    private MovieMapper movieMapper;

    @BeforeEach
    void setUp() {
        movieMapper = Mappers.getMapper(MovieMapper.class);
    }

    @Test
    void mapMovieShowIdsToMovieShows_mapsIds() {
        Set<MovieShow> movieShows = movieMapper.mapMovieShowIdsToMovieShows(Set.of(3L, 4L));

        assertThat(movieShows).extracting(MovieShow::getId).containsExactlyInAnyOrder(3L, 4L);
    }

    @Test
    void linkMovieShowsOwningSide_setsMovieReference() {
        MovieDTOCRUD dto = new MovieDTOCRUD(null, "Inception", null, MovieGenre.SCIENCE_FICTION);
        dto.setMovieShowIds(Set.of(1L));
        Movie movie = new Movie();

        movieMapper.linkMovieShowsOwningSide(dto, movieMapper.toEntity(dto));

        Movie mapped = movieMapper.toEntity(dto);
        movieMapper.linkMovieShowsOwningSide(dto, mapped);

        assertThat(mapped.getMovieShows()).allSatisfy(ms -> assertThat(ms.getMovie()).isSameAs(mapped));
    }
}
