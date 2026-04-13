package com.cinema.cinema_gestion.mapper;

import org.mapstruct.Mapper;

import com.cinema.cinema_gestion.dto.cinema.CinemaDTOCRUD;
import com.cinema.cinema_gestion.dto.cinema.CinemaDTOView;
import com.cinema.cinema_gestion.entity.Cinema;
import org.mapstruct.Mapping;
import java.util.Set;
import com.cinema.cinema_gestion.entity.Room;

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

    default Set<Room> mapRoomIdsToRooms(Set<Long> ids) {
        return idsToEntities(ids, Room::new);
    }
}
