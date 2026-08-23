package com.cinema.cinema_gestion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import com.cinema.cinema_gestion.dto.publicapi.CinemaPublicView;
import com.cinema.cinema_gestion.dto.publicapi.MoviePublicView;
import com.cinema.cinema_gestion.dto.publicapi.MovieShowtimePublicView;
import com.cinema.cinema_gestion.dto.publicapi.MovieTodayView;
import com.cinema.cinema_gestion.entity.Cinema;
import com.cinema.cinema_gestion.entity.Movie;
import com.cinema.cinema_gestion.entity.MovieShow;
import com.cinema.cinema_gestion.entity.Room;
import com.cinema.cinema_gestion.entity.Schedule;
import com.cinema.cinema_gestion.enumerator.MovieGenre;
import com.cinema.cinema_gestion.repository.CinemaRepository;
import com.cinema.cinema_gestion.repository.MovieRepository;
import com.cinema.cinema_gestion.repository.ScheduleRepository;

@ExtendWith(MockitoExtension.class)
class PublicServiceTest {

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    private PublicService publicService;

    private Cinema cinema;
    private Movie movie;
    private Room room;
    private MovieShow movieShow;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        publicService = new PublicService(cinemaRepository, movieRepository, scheduleRepository);

        cinema = new Cinema();
        cinema.setId(1L);
        cinema.setName("Grand Rex");
        cinema.setCity("Paris");
        cinema.setStreet("Boulevard");
        cinema.setNumber("1");

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setGenre(MovieGenre.SCIENCE_FICTION);
        movie.setReleaseDate(LocalDate.of(2010, 7, 16));

        room = new Room();
        room.setId(2L);
        room.setCapacity(350);
        room.setCinema(cinema);

        movieShow = new MovieShow();
        movieShow.setId(3L);
        movieShow.setPrice(12.5);
        movieShow.setMovie(movie);
        movieShow.setRoom(room);

        schedule = new Schedule();
        schedule.setId(10L);
        schedule.setStartTime(LocalDateTime.of(2026, 8, 22, 14, 0));
        schedule.setEndTime(LocalDateTime.of(2026, 8, 22, 16, 30));
        schedule.setMovieShow(movieShow);
    }

    @Test
    void findCinemas_withoutQuery_returnsAll() {
        when(cinemaRepository.findAll()).thenReturn(List.of(cinema));

        List<CinemaPublicView> result = publicService.findCinemas(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Grand Rex");
    }

    @Test
    void findCinemas_withQuery_usesSpecification() {
        when(cinemaRepository.findAll(any(Specification.class))).thenReturn(List.of(cinema));

        List<CinemaPublicView> result = publicService.findCinemas("paris");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).city()).isEqualTo("Paris");
    }

    @Test
    void findMovies_withQuery_usesSpecification() {
        when(movieRepository.findAll(any(Specification.class))).thenReturn(List.of(movie));

        List<MoviePublicView> result = publicService.findMovies("incep");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Inception");
    }

    @Test
    void findCinemaToday_groupsByMovie() {
        when(scheduleRepository.findByCinemaIdAndDay(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(schedule));

        List<MovieTodayView> result = publicService.findCinemaToday(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Inception");
        assertThat(result.get(0).showtimes()).hasSize(1);
        assertThat(result.get(0).showtimes().get(0).price()).isEqualTo(12.5);
    }

    @Test
    void findMovieShowtimes_mapsScheduleDetails() {
        when(scheduleRepository.findShowtimesByMovieId(eq(1L)))
                .thenReturn(List.of(schedule));

        List<MovieShowtimePublicView> result = publicService.findMovieShowtimes(1L, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).cinemaName()).isEqualTo("Grand Rex");
        assertThat(result.get(0).roomCapacity()).isEqualTo(350);
    }
}
