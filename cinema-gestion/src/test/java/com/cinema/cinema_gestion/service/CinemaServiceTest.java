package com.cinema.cinema_gestion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.cinema.cinema_gestion.dto.PageDTO;
import com.cinema.cinema_gestion.dto.cinema.CinemaDTOCRUD;
import com.cinema.cinema_gestion.dto.cinema.CinemaDTOView;
import com.cinema.cinema_gestion.entity.Cinema;
import com.cinema.cinema_gestion.mapper.CinemaMapper;
import com.cinema.cinema_gestion.repository.CinemaRepository;

@ExtendWith(MockitoExtension.class)
class CinemaServiceTest {

    @Mock
    private CinemaRepository repository;

    @Mock
    private CinemaMapper mapper;

    private CinemaService service;

    private Cinema cinema;
    private CinemaDTOCRUD cinemaDto;
    private CinemaDTOView cinemaView;

    @BeforeEach
    void setUp() {
        service = new CinemaService(repository, mapper);
        cinema = new Cinema();
        cinema.setId(1L);
        cinema.setName("Grand Rex");
        cinema.setCity("Paris");
        cinemaDto = new CinemaDTOCRUD(1L, "Grand Rex", "Paris", "Grands Boulevards", "1", Set.of());
        cinemaView = new CinemaDTOView(1L, "Grand Rex", "Paris");
    }

    @Test
    void findAll_returnsPageDto() {
        Page<Cinema> page = new PageImpl<>(List.of(cinema), PageRequest.of(0, 10), 1);
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        when(mapper.toView(cinema)).thenReturn(cinemaView);

        PageDTO<CinemaDTOView> result = service.findAll(0, 10);

        assertThat(result.getContent()).containsExactly(cinemaView);
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getSorted()).isFalse();
    }

    @Test
    void findAll_withSpecification_returnsPageDto() {
        Specification<Cinema> spec = (root, query, cb) -> cb.conjunction();
        Page<Cinema> page = new PageImpl<>(List.of(cinema), PageRequest.of(0, 5), 1);
        when(repository.findAll(eq(spec), eq(PageRequest.of(0, 5)))).thenReturn(page);
        when(mapper.toView(cinema)).thenReturn(cinemaView);

        PageDTO<CinemaDTOView> result = service.findAll(spec, 0, 5);

        assertThat(result.getContent()).containsExactly(cinemaView);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void findById_whenExists_returnsView() {
        when(repository.findById(1L)).thenReturn(Optional.of(cinema));
        when(mapper.toView(cinema)).thenReturn(cinemaView);

        CinemaDTOView result = service.findById(1L);

        assertThat(result).isEqualTo(cinemaView);
    }

    @Test
    void findById_whenNotFound_returnsNull() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.findById(99L)).isNull();
    }

    @Test
    void save_persistsAndReturnsView() {
        when(mapper.toEntity(cinemaDto)).thenReturn(cinema);
        when(repository.save(cinema)).thenReturn(cinema);
        when(mapper.toView(cinema)).thenReturn(cinemaView);

        CinemaDTOView result = service.save(cinemaDto);

        assertThat(result).isEqualTo(cinemaView);
        verify(repository).save(cinema);
    }

    @Test
    void update_savesEntity() {
        when(mapper.toEntity(cinemaDto)).thenReturn(cinema);

        service.update(cinemaDto);

        verify(repository).save(cinema);
    }

    @Test
    void deleteById_delegatesToRepository() {
        service.deleteById(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void findAll_withSearch_delegatesToSpecification() {
        Page<Cinema> page = new PageImpl<>(List.of(cinema), PageRequest.of(0, 10), 1);
        when(repository.findAll(any(Specification.class), eq(PageRequest.of(0, 10)))).thenReturn(page);
        when(mapper.toView(cinema)).thenReturn(cinemaView);

        PageDTO<CinemaDTOView> result = service.findAll("paris", 0, 10);

        assertThat(result.getContent()).containsExactly(cinemaView);
    }
}
