package com.cinema.cinema_gestion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.cinema.cinema_gestion.entity.Cinema;
import com.cinema.cinema_gestion.entity.Room;

class GenericMapperTest {

    private CinemaMapper cinemaMapper;

    @BeforeEach
    void setUp() {
        cinemaMapper = Mappers.getMapper(CinemaMapper.class);
    }

    @Test
    void entitiesToIds_whenNull_returnsEmptySet() {
        assertThat(cinemaMapper.entitiesToIds(null)).isEmpty();
    }

    @Test
    void entitiesToIds_whenEmpty_returnsEmptySet() {
        assertThat(cinemaMapper.entitiesToIds(Collections.emptySet())).isEmpty();
    }

    @Test
    void entitiesToIds_mapsEntityIds() {
        Room room1 = new Room();
        room1.setId(10L);
        Room room2 = new Room();
        room2.setId(20L);

        Set<Long> ids = cinemaMapper.entitiesToIds(Set.of(room1, room2));

        assertThat(ids).containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    void idsToEntities_whenNull_returnsEmptySet() {
        assertThat(cinemaMapper.idsToEntities(null, Room::new)).isEmpty();
    }

    @Test
    void idsToEntities_mapsIdsToEntitiesWithOnlyIdSet() {
        Set<Room> rooms = cinemaMapper.idsToEntities(new LinkedHashSet<>(Set.of(5L, 6L)), Room::new);

        assertThat(rooms).extracting(Room::getId).containsExactlyInAnyOrder(5L, 6L);
    }
}
