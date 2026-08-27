package com.cinema.cinema_gestion.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.dto.movieshow.MovieShowDTOCRUD;
import com.cinema.cinema_gestion.dto.movieshow.MovieShowDTOView;
import com.cinema.cinema_gestion.entity.MovieShow;
import com.cinema.cinema_gestion.mapper.MovieShowMapper;
import com.cinema.cinema_gestion.repository.MovieShowRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service métier des séances (film + salle + tarif) : CRUD générique et recherche par titre de film ou prix.
 */
@Service
public class MovieShowService extends GenericService<MovieShow, MovieShowDTOCRUD, MovieShowDTOView, MovieShowMapper, MovieShowRepository> {
    /**
     * @param repository repository des séances
     * @param mapper     mapper séance
     */
    public MovieShowService(MovieShowRepository repository, MovieShowMapper mapper) {
        super(repository, mapper);
    }

    /**
     * Filtre sur le titre du film associé, et sur le tarif si {@code search} est un nombre.
     *
     * @param search texte déjà trimé
     * @return spécification JPA
     */
    @Override
    protected Specification<MovieShow> buildSearchSpecification(String search) {
        String pattern = likePattern(search);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Object, Object> movie = root.join("movie");
            predicates.add(cb.like(cb.lower(movie.get("title")), pattern));
            try {
                Double price = Double.valueOf(search);
                predicates.add(cb.equal(root.get("price"), price));
            } catch (NumberFormatException ignored) {
                // not a numeric price
            }
            return cb.or(predicates.toArray(Predicate[]::new));
        };
    }
}
