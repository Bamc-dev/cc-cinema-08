package com.cinema.cinema_gestion.mapper;

import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.cinema.cinema_gestion.dto.movie.MovieDTOCRUD;
import com.cinema.cinema_gestion.dto.movie.MovieDTOView;
import com.cinema.cinema_gestion.entity.Movie;
import com.cinema.cinema_gestion.entity.MovieShow;

/**
 * Mapping MapStruct entre {@link Movie}, DTO d'écriture et vue.
 * Les séances sont représentées par leurs identifiants.
 */
@Mapper(componentModel = "spring")
public interface MovieMapper extends GenericMapper<Movie, MovieDTOCRUD, MovieDTOView> {

    @Override
    @Mapping(target = "movieShowIds", expression = "java(entitiesToIds(entity.getMovieShows()))")
    MovieDTOCRUD toDTO(Movie entity);

    @Override
    @Mapping(target = "movieShows", expression = "java(mapMovieShowIdsToMovieShows(dto.getMovieShowIds()))")
    Movie toEntity(MovieDTOCRUD dto);

    /**
     * Rétablit le côté propriétaire de l'association film → séances après {@link #toEntity}.
     *
     * @param dto   DTO d'écriture
     * @param movie film cible du mapping
     */
    @AfterMapping
    default void linkMovieShowsOwningSide(MovieDTOCRUD dto, @MappingTarget Movie movie) {
        if (movie.getMovieShows() == null) {
            return;
        }
        for (MovieShow ms : movie.getMovieShows()) {
            ms.setMovie(movie);
        }
    }

    @Override
    MovieDTOView toView(Movie entity);

    @Override
    MovieDTOView toView(MovieDTOCRUD dto);

    /**
     * Reconstruit des références {@link MovieShow} à partir d'identifiants.
     *
     * @param ids identifiants de séances (peut être null)
     * @return séances id-only
     */
    default Set<MovieShow> mapMovieShowIdsToMovieShows(Set<Long> ids) {
        return idsToEntities(ids, MovieShow::new);
    }
}
