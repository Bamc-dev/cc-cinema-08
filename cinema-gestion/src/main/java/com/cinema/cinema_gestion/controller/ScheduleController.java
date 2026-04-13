package com.cinema.cinema_gestion.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema_gestion.dto.movieshow.ScheduleDTOCRUD;
import com.cinema.cinema_gestion.dto.movieshow.ScheduleDTOView;
import com.cinema.cinema_gestion.service.ScheduleService;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController extends GenericCRUDController<ScheduleDTOCRUD, ScheduleDTOView, ScheduleService> {

    public ScheduleController(ScheduleService service) {
        super(service);
    }
}
