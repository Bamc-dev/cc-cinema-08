package com.cinema.cinema_gestion.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.dto.publicapi.CinemaPublicView;
import com.cinema.cinema_gestion.dto.publicapi.MoviePublicView;
import com.cinema.cinema_gestion.dto.publicapi.MovieShowtimePublicView;
import com.cinema.cinema_gestion.dto.publicapi.MovieTodayView;
import com.cinema.cinema_gestion.dto.publicapi.ShowtimePublicView;
import com.cinema.cinema_gestion.entity.Cinema;
import com.cinema.cinema_gestion.entity.Movie;
import com.cinema.cinema_gestion.entity.MovieShow;
import com.cinema.cinema_gestion.entity.Room;
import com.cinema.cinema_gestion.entity.Schedule;
import com.cinema.cinema_gestion.enumerator.MovieGenre;
import com.cinema.cinema_gestion.repository.CinemaRepository;
import com.cinema.cinema_gestion.repository.MovieRepository;
import com.cinema.cinema_gestion.repository.ScheduleRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class PublicService {

    private final CinemaRepository cinemaRepository;
    private final MovieRepository movieRepository;
    private final ScheduleRepository scheduleRepository;

    public PublicService(CinemaRepository cinemaRepository, MovieRepository movieRepository,
            ScheduleRepository scheduleRepository) {
        this.cinemaRepository = cinemaRepository;
        this.movieRepository = movieRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<CinemaPublicView> findCinemas(String query) {
        List<Cinema> cinemas;
        if (query == null || query.isBlank()) {
            cinemas = cinemaRepository.findAll();
        } else {
            String pattern = "%" + query.toLowerCase() + "%";
            Specification<Cinema> spec = (root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("city")), pattern));
            cinemas = cinemaRepository.findAll(spec);
        }
        return cinemas.stream()
                .map(c -> new CinemaPublicView(c.getId(), c.getName(), c.getCity(), c.getStreet(), c.getNumber()))
                .toList();
    }

    public List<MoviePublicView> findMovies(String query) {
        List<Movie> movies;
        if (query == null || query.isBlank()) {
            movies = movieRepository.findAll();
        } else {
            String pattern = "%" + query.toLowerCase() + "%";
            Specification<Movie> spec = (root, q, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.like(cb.lower(root.get("title")), pattern));
                try {
                    MovieGenre genre = MovieGenre.valueOf(query.trim().toUpperCase().replace(' ', '_'));
                    predicates.add(cb.equal(root.get("genre"), genre));
                } catch (IllegalArgumentException ignored) {
                    // not a valid genre enum name
                }
                return cb.or(predicates.toArray(Predicate[]::new));
            };
            movies = movieRepository.findAll(spec);
        }
        return movies.stream()
                .map(m -> new MoviePublicView(m.getId(), m.getTitle(), m.getReleaseDate(), m.getGenre()))
                .toList();
    }

    public List<MovieTodayView> findCinemaToday(Long cinemaId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<Schedule> schedules = scheduleRepository.findByCinemaIdAndDay(cinemaId, startOfDay, endOfDay);
        Map<Long, MovieTodayViewBuilder> grouped = new LinkedHashMap<>();

        for (Schedule schedule : schedules) {
            MovieShow movieShow = schedule.getMovieShow();
            Movie movie = movieShow.getMovie();
            Room room = movieShow.getRoom();

            grouped.computeIfAbsent(movie.getId(), id -> new MovieTodayViewBuilder(movie))
                    .addShowtime(toShowtimePublicView(schedule, movieShow, room));
        }

        return grouped.values().stream().map(MovieTodayViewBuilder::build).toList();
    }

    public List<MovieShowtimePublicView> findMovieShowtimes(Long movieId, LocalDate date, String query) {
        LocalDateTime startOfDay = null;
        LocalDateTime endOfDay = null;
        if (date != null) {
            startOfDay = date.atStartOfDay();
            endOfDay = date.plusDays(1).atStartOfDay();
        }

        String cinemaQuery = (query == null || query.isBlank()) ? null : query.trim();
        boolean hasDate = date != null;
        boolean hasCinemaQuery = cinemaQuery != null;

        List<Schedule> schedules;
        if (hasDate && hasCinemaQuery) {
            schedules = scheduleRepository.findShowtimesByMovieIdAndDateAndCinema(
                    movieId, startOfDay, endOfDay, cinemaQuery);
        } else if (hasDate) {
            schedules = scheduleRepository.findShowtimesByMovieIdAndDate(movieId, startOfDay, endOfDay);
        } else if (hasCinemaQuery) {
            schedules = scheduleRepository.findShowtimesByMovieIdAndCinema(movieId, cinemaQuery);
        } else {
            schedules = scheduleRepository.findShowtimesByMovieId(movieId);
        }

        List<MovieShowtimePublicView> result = new ArrayList<>();
        for (Schedule schedule : schedules) {
            MovieShow movieShow = schedule.getMovieShow();
            Room room = movieShow.getRoom();
            Cinema cinema = room.getCinema();
            result.add(new MovieShowtimePublicView(
                    schedule.getId(),
                    cinema.getId(),
                    cinema.getName(),
                    cinema.getCity(),
                    movieShow.getPrice(),
                    room.getId(),
                    room.getCapacity(),
                    schedule.getStartTime(),
                    schedule.getEndTime()));
        }
        return result;
    }

    private ShowtimePublicView toShowtimePublicView(Schedule schedule, MovieShow movieShow, Room room) {
        return new ShowtimePublicView(
                schedule.getId(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                movieShow.getPrice(),
                room.getId(),
                room.getCapacity());
    }

    private static final class MovieTodayViewBuilder {
        private final Movie movie;
        private final List<ShowtimePublicView> showtimes = new ArrayList<>();

        private MovieTodayViewBuilder(Movie movie) {
            this.movie = movie;
        }

        private void addShowtime(ShowtimePublicView showtime) {
            showtimes.add(showtime);
        }

        private MovieTodayView build() {
            return new MovieTodayView(movie.getId(), movie.getTitle(), movie.getGenre(), List.copyOf(showtimes));
        }
    }
}
