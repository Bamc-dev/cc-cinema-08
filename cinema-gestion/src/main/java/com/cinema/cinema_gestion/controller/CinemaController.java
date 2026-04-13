package com.cinema.cinema_gestion.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema_gestion.service.CinemaService;
import com.cinema.cinema_gestion.dto.cinema.CinemaDTOCRUD;
import com.cinema.cinema_gestion.dto.cinema.CinemaDTOView;

@RestController
@RequestMapping("/api/cinema")
public class CinemaController extends GenericCRUDController<CinemaDTOCRUD, CinemaDTOView, CinemaService> {
    public CinemaController(CinemaService service) {
        super(service);
    }

}
