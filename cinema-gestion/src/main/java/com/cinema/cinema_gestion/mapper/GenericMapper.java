package com.cinema.cinema_gestion.mapper;

import com.cinema.cinema_gestion.dto.BaseDTO;
import com.cinema.cinema_gestion.entity.BaseEntity;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.LinkedHashSet;

public interface GenericMapper<T extends BaseEntity, D extends BaseDTO, V extends Record> {
    D toDTO(T entity);

    T toEntity(D dto);

    V toView(T entity);

    V toView(D dto);

    default <E extends BaseEntity> Set<Long> entitiesToIds(Set<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptySet();
        }
        return entities.stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    
    default <E extends BaseEntity> Set<E> idsToEntities(Set<Long> ids, Supplier<E> factory) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        return ids.stream()
                .map(id -> {
                    E entity = factory.get();
                    entity.setId(id);
                    return entity;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
