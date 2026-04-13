package com.cinema.cinema_gestion.mapper;

import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.cinema.cinema_gestion.dto.movieshow.MovieShowDTOCRUD;
import com.cinema.cinema_gestion.dto.movieshow.MovieShowDTOView;
import com.cinema.cinema_gestion.entity.Movie;
import com.cinema.cinema_gestion.entity.MovieShow;
import com.cinema.cinema_gestion.entity.Room;
import com.cinema.cinema_gestion.entity.Schedule;

@Mapper(componentModel = "spring")
public interface MovieShowMapper extends GenericMapper<MovieShow, MovieShowDTOCRUD, MovieShowDTOView> {

    @Override
    @Mapping(target = "movieId", expression = "java(entity.getMovie() != null ? entity.getMovie().getId() : null)")
    @Mapping(target = "roomId", expression = "java(entity.getRoom() != null ? entity.getRoom().getId() : null)")
    @Mapping(target = "scheduleIds", expression = "java(entitiesToIds(entity.getSchedules()))")
    MovieShowDTOCRUD toDTO(MovieShow entity);

    @Override
    @Mapping(target = "movie", expression = "java(mapMovieIdToMovie(dto.getMovieId()))")
    @Mapping(target = "room", expression = "java(mapRoomIdToRoom(dto.getRoomId()))")
    @Mapping(target = "schedules", expression = "java(mapScheduleIdsToSchedules(dto.getScheduleIds()))")
    MovieShow toEntity(MovieShowDTOCRUD dto);

    @AfterMapping
    default void linkSchedulesOwningSide(MovieShowDTOCRUD dto, @MappingTarget MovieShow movieShow) {
        if (movieShow.getSchedules() == null) {
            return;
        }
        for (Schedule s : movieShow.getSchedules()) {
            s.setMovieShow(movieShow);
        }
    }

    @Override
    @Mapping(target = "movieId", expression = "java(entity.getMovie() != null ? entity.getMovie().getId() : null)")
    @Mapping(target = "roomId", expression = "java(entity.getRoom() != null ? entity.getRoom().getId() : null)")
    @Mapping(target = "scheduleIds", expression = "java(entitiesToIds(entity.getSchedules()))")
    MovieShowDTOView toView(MovieShow entity);

    @Override
    MovieShowDTOView toView(MovieShowDTOCRUD dto);

    default Movie mapMovieIdToMovie(Long movieId) {
        if (movieId == null) {
            return null;
        }
        Movie movie = new Movie();
        movie.setId(movieId);
        return movie;
    }

    default Room mapRoomIdToRoom(Long roomId) {
        if (roomId == null) {
            return null;
        }
        Room room = new Room();
        room.setId(roomId);
        return room;
    }

    default Set<Schedule> mapScheduleIdsToSchedules(Set<Long> ids) {
        return idsToEntities(ids, Schedule::new);
    }
}
