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

@Service
public class ScheduleService extends GenericService<Schedule, ScheduleDTOCRUD, ScheduleDTOView, ScheduleMapper, ScheduleRepository> {

    public ScheduleService(ScheduleRepository repository, ScheduleMapper mapper) {
        super(repository, mapper);
    }

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
