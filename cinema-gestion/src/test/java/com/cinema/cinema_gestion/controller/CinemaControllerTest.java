package com.cinema.cinema_gestion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.cinema.cinema_gestion.dto.PageDTO;
import com.cinema.cinema_gestion.dto.cinema.CinemaDTOCRUD;
import com.cinema.cinema_gestion.dto.cinema.CinemaDTOView;
import com.cinema.cinema_gestion.service.CinemaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CinemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CinemaService cinemaService;

    private final CinemaDTOView cinemaView = new CinemaDTOView(1L, "Grand Rex", "Paris");

    @Test
    void findById_returnsCinema() throws Exception {
        when(cinemaService.findById(1L)).thenReturn(cinemaView);

        mockMvc.perform(get("/api/cinema/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Grand Rex"))
                .andExpect(jsonPath("$.city").value("Paris"));
    }

    @Test
    void create_returnsCreatedCinema() throws Exception {
        CinemaDTOCRUD dto = new CinemaDTOCRUD(null, "Grand Rex", "Paris", "Grands Boulevards", "1", Set.of());
        when(cinemaService.save(any(CinemaDTOCRUD.class))).thenReturn(cinemaView);

        mockMvc.perform(post("/api/cinema/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_returnsNoContent() throws Exception {
        CinemaDTOCRUD dto = new CinemaDTOCRUD(1L, "Grand Rex", "Paris", "Grands Boulevards", "1", Set.of());

        mockMvc.perform(put("/api/cinema/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(cinemaService).update(any(CinemaDTOCRUD.class));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/cinema/admin/delete/1"))
                .andExpect(status().isNoContent());

        verify(cinemaService).deleteById(1L);
    }

    @Test
    void list_returnsPage() throws Exception {
        PageDTO<CinemaDTOView> page = new PageDTO<>(List.of(cinemaView), 1L, 1, 10, false);
        when(cinemaService.findAll(isNull(String.class), eq(0), eq(10))).thenReturn(page);

        mockMvc.perform(get("/api/cinema/list/0/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Grand Rex"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void list_withSearch_delegatesToService() throws Exception {
        PageDTO<CinemaDTOView> page = new PageDTO<>(List.of(cinemaView), 1L, 1, 10, false);
        when(cinemaService.findAll(eq("paris"), eq(0), eq(10))).thenReturn(page);

        mockMvc.perform(get("/api/cinema/list/0/10").param("search", "paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Grand Rex"));

        verify(cinemaService).findAll(eq("paris"), eq(0), eq(10));
    }
}
