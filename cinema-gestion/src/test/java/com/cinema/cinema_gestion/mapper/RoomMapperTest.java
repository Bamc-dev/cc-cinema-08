package com.cinema.cinema_gestion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.cinema.cinema_gestion.entity.Cinema;
import com.cinema.cinema_gestion.entity.MovieShow;
import com.cinema.cinema_gestion.entity.Room;

class RoomMapperTest {

    private RoomMapper roomMapper;

    @BeforeEach
    void setUp() {
        roomMapper = Mappers.getMapper(RoomMapper.class);
    }

    @Test
    void mapCinemaIdToCinema_whenNull_returnsNull() {
        assertThat(roomMapper.mapCinemaIdToCinema(null)).isNull();
    }

    @Test
    void mapCinemaIdToCinema_setsIdOnly() {
        Cinema cinema = roomMapper.mapCinemaIdToCinema(7L);

        assertThat(cinema.getId()).isEqualTo(7L);
    }

    @Test
    void mapMovieShowIdsToMovieShows_mapsIds() {
        Set<MovieShow> movieShows = roomMapper.mapMovieShowIdsToMovieShows(Set.of(1L, 2L));

        assertThat(movieShows).extracting(MovieShow::getId).containsExactlyInAnyOrder(1L, 2L);
    }
}
