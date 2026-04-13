package com.cinema.cinema_gestion.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema_gestion.dto.room.RoomDTOCRUD;
import com.cinema.cinema_gestion.dto.room.RoomDTOView;
import com.cinema.cinema_gestion.service.RoomService;

@RestController
@RequestMapping("/api/room")
public class RoomController extends GenericCRUDController<RoomDTOCRUD, RoomDTOView, RoomService> {

    public RoomController(RoomService service) {
        super(service);
    }

}
