package com.cinema.cinema_gestion.service;

import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.dto.movieshow.ScheduleDTOCRUD;
import com.cinema.cinema_gestion.dto.movieshow.ScheduleDTOView;
import com.cinema.cinema_gestion.entity.Schedule;
import com.cinema.cinema_gestion.mapper.ScheduleMapper;
import com.cinema.cinema_gestion.repository.ScheduleRepository;

@Service
public class ScheduleService extends GenericService<Schedule, ScheduleDTOCRUD, ScheduleDTOView, ScheduleMapper, ScheduleRepository> {

    public ScheduleService(ScheduleRepository repository, ScheduleMapper mapper) {
        super(repository, mapper);
    }
}
