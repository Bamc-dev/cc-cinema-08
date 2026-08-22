package com.cinema.cinema_gestion.controller;

import com.cinema.cinema_gestion.dto.BaseDTO;
import com.cinema.cinema_gestion.service.GenericService;

public abstract class GenericController<D extends BaseDTO, V extends Record, S extends GenericService<?, D, V, ?, ?>> {
    protected S service;

    protected GenericController(S service) {
        this.service = service;
    }
}
