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

/**
 * Service métier des salles : CRUD générique et recherche par nom de cinéma ou capacité.
 */
@Service
public class RoomService extends GenericService<Room, RoomDTOCRUD, RoomDTOView, RoomMapper, RoomRepository> {

    /**
     * @param repository repository des salles
     * @param mapper     mapper salle
     */
    public RoomService(RoomRepository repository, RoomMapper mapper) {
        super(repository, mapper);
    }

    /**
     * Filtre sur le nom du cinéma associé, et sur la capacité si {@code search} est un entier.
     *
     * @param search texte déjà trimé
     * @return spécification JPA
     */
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
