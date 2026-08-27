package com.cinema.cinema_gestion.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.dto.movieshow.ScheduleDTOCRUD;
import com.cinema.cinema_gestion.dto.movieshow.ScheduleDTOView;
import com.cinema.cinema_gestion.entity.Schedule;
import com.cinema.cinema_gestion.mapper.ScheduleMapper;
import com.cinema.cinema_gestion.repository.ScheduleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Service métier des horaires : CRUD générique et recherche par date de début.
 */
@Service
public class ScheduleService extends GenericService<Schedule, ScheduleDTOCRUD, ScheduleDTOView, ScheduleMapper, ScheduleRepository> {

    /**
     * @param repository repository des horaires
     * @param mapper     mapper horaire
     */
    public ScheduleService(ScheduleRepository repository, ScheduleMapper mapper) {
        super(repository, mapper);
    }

    /**
     * Si {@code search} est une date ISO ({@code yyyy-MM-dd}), restreint aux horaires de ce jour.
     * Sinon, aucun filtre (conjonction vraie).
     *
     * @param search texte déjà trimé
     * @return spécification JPA
     */
    @Override
    protected Specification<Schedule> buildSearchSpecification(String search) {
        return (root, query, cb) -> {
            try {
                LocalDate date = LocalDate.parse(search);
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.atTime(LocalTime.MAX);
                return cb.between(root.get("startTime"), start, end);
            } catch (Exception e) {
                return cb.conjunction();
            }
        };
    }
}
