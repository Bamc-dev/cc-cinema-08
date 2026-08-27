package com.cinema.cinema_gestion.mapper;

import org.mapstruct.Mapper;

import com.cinema.cinema_gestion.dto.cinema.CinemaDTOCRUD;
import com.cinema.cinema_gestion.dto.cinema.CinemaDTOView;
import com.cinema.cinema_gestion.entity.Cinema;
import org.mapstruct.Mapping;
import java.util.Set;
import com.cinema.cinema_gestion.entity.Room;

/**
 * Mapping MapStruct entre {@link Cinema}, DTO d'écriture et vue.
 * Les salles sont représentées par leurs identifiants.
 */
@Mapper(componentModel = "spring")
public interface CinemaMapper extends GenericMapper<Cinema, CinemaDTOCRUD, CinemaDTOView> {

    @Override
    @Mapping(target = "roomIds", expression = "java(entitiesToIds(entity.getRooms()))")
    CinemaDTOCRUD toDTO(Cinema entity);

    @Override
    @Mapping(target = "rooms", expression = "java(mapRoomIdsToRooms(dto.getRoomIds()))")
    Cinema toEntity(CinemaDTOCRUD dto);

    @Override
    CinemaDTOView toView(Cinema entity);

    @Override
    CinemaDTOView toView(CinemaDTOCRUD dto);

    /**
     * Reconstruit des références {@link Room} à partir d'identifiants.
     *
     * @param ids identifiants de salles (peut être null)
     * @return salles id-only
     */
    default Set<Room> mapRoomIdsToRooms(Set<Long> ids) {
        return idsToEntities(ids, Room::new);
    }
}
