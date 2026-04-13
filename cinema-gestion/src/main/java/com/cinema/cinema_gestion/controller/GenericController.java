package com.cinema.cinema_gestion.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema_gestion.dto.BaseDTO;
import com.cinema.cinema_gestion.service.GenericService;

@RestController
@RequestMapping("/api")
public abstract class GenericController<D extends BaseDTO,V extends Record,S extends GenericService<?, D, V, ?, ?>> {
    protected S service;

    protected GenericController(S service) {
        this.service = service;
    }
}
