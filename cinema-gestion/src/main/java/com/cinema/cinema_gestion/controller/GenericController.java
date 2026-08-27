package com.cinema.cinema_gestion.controller;

import com.cinema.cinema_gestion.dto.BaseDTO;
import com.cinema.cinema_gestion.service.GenericService;

/**
 * Contrôleur de base qui injecte le service générique d'une ressource.
 *
 * @param <D> DTO d'écriture (CRUD)
 * @param <V> DTO de lecture (vue / record)
 * @param <S> service métier associé
 */
public abstract class GenericController<D extends BaseDTO, V extends Record, S extends GenericService<?, D, V, ?, ?>> {
    /** Service délégué pour les opérations de la ressource. */
    protected S service;

    /**
     * @param service service métier de la ressource
     */
    protected GenericController(S service) {
        this.service = service;
    }
}
