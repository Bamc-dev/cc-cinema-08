package com.cinema.cinema_gestion.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.cinema.cinema_gestion.mapper.RoomMapper;
import com.cinema.cinema_gestion.repository.RoomRepository;
import com.cinema.cinema_gestion.entity.Room;
import com.cinema.cinema_gestion.dto.room.RoomDTOCRUD;
import com.cinema.cinema_gestion.dto.room.RoomDTOView;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService extends GenericService<Room, RoomDTOCRUD, RoomDTOView, RoomMapper, RoomRepository> {

    public RoomService(RoomRepository repository, RoomMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected Specification<Room> buildSearchSpecification(String search) {
        String pattern = likePattern(search);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Object, Object> cinema = root.join("cinema");
            predicates.add(cb.like(cb.lower(cinema.get("name")), pattern));
            try {
                Integer capacity = Integer.valueOf(search);
                predicates.add(cb.equal(root.get("capacity"), capacity));
            } catch (NumberFormatException ignored) {
                // not a numeric capacity
            }
            return cb.or(predicates.toArray(Predicate[]::new));
        };
    }
}
