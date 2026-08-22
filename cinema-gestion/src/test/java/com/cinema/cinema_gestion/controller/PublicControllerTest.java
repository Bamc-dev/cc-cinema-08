package com.cinema.cinema_gestion.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.cinema.cinema_gestion.dto.publicapi.CinemaPublicView;
import com.cinema.cinema_gestion.dto.publicapi.MoviePublicView;
import com.cinema.cinema_gestion.dto.publicapi.MovieShowtimePublicView;
import com.cinema.cinema_gestion.dto.publicapi.MovieTodayView;
import com.cinema.cinema_gestion.dto.publicapi.ShowtimePublicView;
import com.cinema.cinema_gestion.enumerator.MovieGenre;
import com.cinema.cinema_gestion.service.PublicService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicService publicService;

    @Test
    void listCinemas_returnsCinemas() throws Exception {
        when(publicService.findCinemas(isNull()))
                .thenReturn(List.of(new CinemaPublicView(1L, "Grand Rex", "Paris", "Boulevard", "1")));

        mockMvc.perform(get("/api/public/cinemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Grand Rex"))
                .andExpect(jsonPath("$[0].city").value("Paris"));
    }

    @Test
    void listCinemas_withQuery_delegatesToService() throws Exception {
        when(publicService.findCinemas("paris")).thenReturn(List.of());

        mockMvc.perform(get("/api/public/cinemas").param("q", "paris"))
                .andExpect(status().isOk());

        verify(publicService).findCinemas("paris");
    }

    @Test
    void listMovies_returnsMovies() throws Exception {
        when(publicService.findMovies(isNull()))
                .thenReturn(List.of(new MoviePublicView(1L, "Inception", LocalDate.of(2010, 7, 16),
                        MovieGenre.SCIENCE_FICTION)));

        mockMvc.perform(get("/api/public/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Inception"));
    }

    @Test
    void cinemaToday_returnsFilmsWithShowtimes() throws Exception {
        ShowtimePublicView showtime = new ShowtimePublicView(10L,
                LocalDateTime.of(2026, 8, 22, 14, 0),
                LocalDateTime.of(2026, 8, 22, 16, 30),
                12.5, 2L, 350);
        MovieTodayView todayView = new MovieTodayView(1L, "Inception", MovieGenre.SCIENCE_FICTION,
                List.of(showtime));
        when(publicService.findCinemaToday(1L)).thenReturn(List.of(todayView));

        mockMvc.perform(get("/api/public/cinemas/1/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Inception"))
                .andExpect(jsonPath("$[0].showtimes[0].price").value(12.5));
    }

    @Test
    void movieShowtimes_returnsShowtimesAcrossCinemas() throws Exception {
        MovieShowtimePublicView showtime = new MovieShowtimePublicView(
                10L, 1L, "Grand Rex", "Paris", 12.5, 2L, 350,
                LocalDateTime.of(2026, 8, 22, 14, 0),
                LocalDateTime.of(2026, 8, 22, 16, 30));
        when(publicService.findMovieShowtimes(eq(1L), isNull(), isNull())).thenReturn(List.of(showtime));

        mockMvc.perform(get("/api/public/movies/1/showtimes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cinemaName").value("Grand Rex"))
                .andExpect(jsonPath("$[0].price").value(12.5));
    }
}
