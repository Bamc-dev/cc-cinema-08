package com.cinema.cinema_gestion.service;

import com.cinema.cinema_gestion.repository.CinemaRepository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.entity.Cinema;
import com.cinema.cinema_gestion.mapper.CinemaMapper;
import com.cinema.cinema_gestion.dto.cinema.CinemaDTOCRUD;
import com.cinema.cinema_gestion.dto.cinema.CinemaDTOView;

/**
 * Service métier des cinémas : CRUD générique et recherche par nom, ville ou rue.
 */
@Service
public class CinemaService extends GenericService<Cinema, CinemaDTOCRUD, CinemaDTOView, CinemaMapper, CinemaRepository> {
    /**
     * @param repository repository des cinémas
     * @param mapper     mapper cinéma
     */
    public CinemaService(CinemaRepository repository, CinemaMapper mapper) {
        super(repository, mapper);
    }

    /**
     * Filtre sur le nom, la ville ou la rue (LIKE insensible à la casse).
     *
     * @param search texte déjà trimé
     * @return spécification JPA
     */
    @Override
    protected Specification<Cinema> buildSearchSpecification(String search) {
        String pattern = likePattern(search);
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("city")), pattern),
                cb.like(cb.lower(root.get("street")), pattern));
    }
}
