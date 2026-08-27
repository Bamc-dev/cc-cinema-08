package com.cinema.cinema_gestion.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.dto.movie.MovieDTOCRUD;
import com.cinema.cinema_gestion.dto.movie.MovieDTOView;
import com.cinema.cinema_gestion.entity.Movie;
import com.cinema.cinema_gestion.enumerator.MovieGenre;
import com.cinema.cinema_gestion.mapper.MovieMapper;
import com.cinema.cinema_gestion.repository.MovieRepository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service métier des films : CRUD générique et recherche par titre ou genre.
 */
@Service
public class MovieService extends GenericService<Movie, MovieDTOCRUD, MovieDTOView, MovieMapper, MovieRepository> {

    /**
     * @param repository repository des films
     * @param mapper     mapper film
     */
    public MovieService(MovieRepository repository, MovieMapper mapper) {
        super(repository, mapper);
    }

    /**
     * Filtre sur le titre (LIKE) et, si {@code search} correspond à un {@link MovieGenre}, sur le genre.
     *
     * @param search texte déjà trimé
     * @return spécification JPA
     */
    @Override
    protected Specification<Movie> buildSearchSpecification(String search) {
        String pattern = likePattern(search);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("title")), pattern));
            try {
                MovieGenre genre = MovieGenre.valueOf(search.toUpperCase().replace(' ', '_'));
                predicates.add(cb.equal(root.get("genre"), genre));
            } catch (IllegalArgumentException ignored) {
                // not a valid genre name
            }
            return cb.or(predicates.toArray(Predicate[]::new));
        };
    }
}
