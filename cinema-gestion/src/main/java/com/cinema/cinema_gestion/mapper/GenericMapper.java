package com.cinema.cinema_gestion.mapper;

import com.cinema.cinema_gestion.dto.BaseDTO;
import com.cinema.cinema_gestion.entity.BaseEntity;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Contrat MapStruct commun : conversion entre entité, DTO d'écriture et vue de lecture.
 *
 * @param <T> entité JPA
 * @param <D> DTO d'écriture
 * @param <V> DTO de lecture (record)
 */
public interface GenericMapper<T extends BaseEntity, D extends BaseDTO, V extends Record> {
    /**
     * @param entity entité persistée
     * @return DTO d'écriture
     */
    D toDTO(T entity);

    /**
     * @param dto DTO d'écriture
     * @return entité (références d'association souvent limitées à l'id)
     */
    T toEntity(D dto);

    /**
     * @param entity entité persistée
     * @return vue de lecture
     */
    V toView(T entity);

    /**
     * @param dto DTO d'écriture
     * @return vue de lecture
     */
    V toView(D dto);

    /**
     * Extrait les identifiants d'un ensemble d'entités, en conservant l'ordre.
     *
     * @param <E>      type d'entité
     * @param entities ensemble source (peut être null)
     * @return identifiants, ou ensemble vide
     */
    default <E extends BaseEntity> Set<Long> entitiesToIds(Set<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptySet();
        }
        return entities.stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    
    /**
     * Reconstruit des références d'entités à partir d'identifiants (proxy id-only).
     *
     * @param <E>     type d'entité
     * @param ids     identifiants (peut être null)
     * @param factory fournisseur d'instance vide
     * @return entités avec id renseigné, ou ensemble vide
     */
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
