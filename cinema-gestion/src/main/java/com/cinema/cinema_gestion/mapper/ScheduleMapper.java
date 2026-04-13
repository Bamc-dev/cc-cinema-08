package com.cinema.cinema_gestion.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cinema.cinema_gestion.dto.movieshow.ScheduleDTOCRUD;
import com.cinema.cinema_gestion.dto.movieshow.ScheduleDTOView;
import com.cinema.cinema_gestion.entity.MovieShow;
import com.cinema.cinema_gestion.entity.Schedule;

@Mapper(componentModel = "spring")
public interface ScheduleMapper extends GenericMapper<Schedule, ScheduleDTOCRUD, ScheduleDTOView> {

    @Override
    @Mapping(target = "movieShowId", expression = "java(entity.getMovieShow() != null ? entity.getMovieShow().getId() : null)")
    ScheduleDTOCRUD toDTO(Schedule entity);

    @Override
    @Mapping(target = "movieShow", expression = "java(mapMovieShowIdToMovieShow(dto.getMovieShowId()))")
    Schedule toEntity(ScheduleDTOCRUD dto);

    @Override
    @Mapping(target = "movieShowId", expression = "java(entity.getMovieShow() != null ? entity.getMovieShow().getId() : null)")
    ScheduleDTOView toView(Schedule entity);

    @Override
    ScheduleDTOView toView(ScheduleDTOCRUD dto);

    default MovieShow mapMovieShowIdToMovieShow(Long movieShowId) {
        if (movieShowId == null) {
            return null;
        }
        MovieShow movieShow = new MovieShow();
        movieShow.setId(movieShowId);
        return movieShow;
    }
}
