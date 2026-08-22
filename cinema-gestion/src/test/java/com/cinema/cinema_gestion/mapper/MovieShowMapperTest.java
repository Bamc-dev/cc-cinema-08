package com.cinema.cinema_gestion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.cinema.cinema_gestion.dto.movieshow.MovieShowDTOCRUD;
import com.cinema.cinema_gestion.entity.Movie;
import com.cinema.cinema_gestion.entity.MovieShow;
import com.cinema.cinema_gestion.entity.Room;
import com.cinema.cinema_gestion.entity.Schedule;

class MovieShowMapperTest {

    private MovieShowMapper movieShowMapper;

    @BeforeEach
    void setUp() {
        movieShowMapper = Mappers.getMapper(MovieShowMapper.class);
    }

    @Test
    void mapMovieIdToMovie_whenNull_returnsNull() {
        assertThat(movieShowMapper.mapMovieIdToMovie(null)).isNull();
    }

    @Test
    void mapRoomIdToRoom_setsIdOnly() {
        Room room = movieShowMapper.mapRoomIdToRoom(9L);

        assertThat(room.getId()).isEqualTo(9L);
    }

    @Test
    void mapScheduleIdsToSchedules_mapsIds() {
        Set<Schedule> schedules = movieShowMapper.mapScheduleIdsToSchedules(Set.of(11L, 12L));

        assertThat(schedules).extracting(Schedule::getId).containsExactlyInAnyOrder(11L, 12L);
    }

    @Test
    void linkSchedulesOwningSide_setsMovieShowReference() {
        MovieShowDTOCRUD dto = new MovieShowDTOCRUD(null, 10.0, 1L, 2L, Set.of(5L));
        MovieShow movieShow = movieShowMapper.toEntity(dto);
        movieShowMapper.linkSchedulesOwningSide(dto, movieShow);

        assertThat(movieShow.getSchedules()).allSatisfy(s -> assertThat(s.getMovieShow()).isSameAs(movieShow));
    }

    @Test
    void mapMovieIdToMovie_setsIdOnly() {
        Movie movie = movieShowMapper.mapMovieIdToMovie(3L);

        assertThat(movie.getId()).isEqualTo(3L);
    }
}
