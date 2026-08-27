package com.cinema.cinema_gestion.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cinema.cinema_gestion.dto.room.RoomDTOCRUD;
import com.cinema.cinema_gestion.dto.room.RoomDTOView;
import com.cinema.cinema_gestion.entity.Room;
import com.cinema.cinema_gestion.entity.MovieShow;
import java.util.Set;
import com.cinema.cinema_gestion.entity.Cinema;

/**
 * Mapping MapStruct entre {@link Room}, DTO d'écriture et vue.
 * Cinéma et séances sont représentés par leurs identifiants.
 */
@Mapper(componentModel = "spring")
public interface RoomMapper extends GenericMapper<Room, RoomDTOCRUD, RoomDTOView> {

    @Override
    @Mapping(target = "cinemaId", expression = "java(entity.getCinema().getId())")
    @Mapping(target = "movieShowIds", expression = "java(entitiesToIds(entity.getMovieShows()))")
    RoomDTOCRUD toDTO(Room entity);

    @Override
    @Mapping(target = "movieShows", expression = "java(mapMovieShowIdsToMovieShows(dto.getMovieShowIds()))")
    @Mapping(target = "cinema", expression = "java(mapCinemaIdToCinema(dto.getCinemaId()))")
    Room toEntity(RoomDTOCRUD dto);

    @Override
    @Mapping(target = "cinemaId", source = "cinema.id")
    RoomDTOView toView(Room entity);

    @Override
    RoomDTOView toView(RoomDTOCRUD dto);

    /**
     * Reconstruit des références {@link MovieShow} à partir d'identifiants.
     *
     * @param ids identifiants de séances (peut être null)
     * @return séances id-only
     */
    default Set<MovieShow> mapMovieShowIdsToMovieShows(Set<Long> ids) {
        return idsToEntities(ids, MovieShow::new);
    }

    /**
     * Reconstruit une référence {@link Cinema} à partir de son identifiant.
     *
     * @param cinemaId identifiant du cinéma (peut être null)
     * @return cinéma id-only, ou {@code null}
     */
    default Cinema mapCinemaIdToCinema(Long cinemaId) {
        if (cinemaId == null)
            return null;
        Cinema cinema = new Cinema();
        cinema.setId(cinemaId);
        return cinema;
    }
}
