package com.cinema.cinema_gestion.service;

import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.mapper.RoomMapper;
import com.cinema.cinema_gestion.repository.RoomRepository;
import com.cinema.cinema_gestion.entity.Room;
import com.cinema.cinema_gestion.dto.room.RoomDTOCRUD;
import com.cinema.cinema_gestion.dto.room.RoomDTOView;

@Service
public class RoomService extends GenericService<Room, RoomDTOCRUD, RoomDTOView, RoomMapper, RoomRepository> {

    public RoomService(RoomRepository repository, RoomMapper mapper) {
        super(repository, mapper);
    }

}
