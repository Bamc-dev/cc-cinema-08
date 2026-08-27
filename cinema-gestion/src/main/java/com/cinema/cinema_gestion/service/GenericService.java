package com.cinema.cinema_gestion.service;

import org.springframework.data.jpa.domain.Specification;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.cinema.cinema_gestion.entity.BaseEntity;
import com.cinema.cinema_gestion.dto.BaseDTO;
import com.cinema.cinema_gestion.mapper.GenericMapper;
import com.cinema.cinema_gestion.repository.GenericRepository;
import com.cinema.cinema_gestion.dto.PageDTO;

/**
 * Service CRUD générique : pagination, mapping entité/DTO et recherche optionnelle.
 *
 * @param <E> entité JPA
 * @param <D> DTO d'écriture
 * @param <V> DTO de lecture
 * @param <M> mapper MapStruct
 * @param <R> repository Spring Data
 */
public abstract class GenericService<E extends BaseEntity, D extends BaseDTO, V extends Record, M extends GenericMapper<E, D, V>, R extends GenericRepository<E>> {
    /** Accès persistance de la ressource. */
    protected R repository;
    /** Conversion entité / DTO / vue. */
    protected M mapper;

    /**
     * @param repository repository JPA
     * @param mapper     mapper MapStruct
     */
    protected GenericService(R repository, M mapper) {
        this.mapper = mapper;
        this.repository = repository;
    }

    /**
     * Liste paginée sans filtre.
     *
     * @param page numéro de page (0-based)
     * @param size taille de page
     * @return page de vues
     */
    public PageDTO<V> findAll(int page, int size) {
        Page<E> pageEntity = repository.findAll(PageRequest.of(page, size));
        return new PageDTO<>(pageEntity.getContent().stream().map(mapper::toView).collect(Collectors.toList()), pageEntity.getTotalElements(), pageEntity.getTotalPages(), pageEntity.getSize(), pageEntity.getSort().isSorted());
    }

    /**
     * Liste paginée avec recherche : si {@code search} est vide, délègue à {@link #findAll(int, int)}.
     *
     * @param search texte de recherche (peut être null)
     * @param page   numéro de page (0-based)
     * @param size   taille de page
     * @return page de vues
     */
    public PageDTO<V> findAll(String search, int page, int size) {
        if (search == null || search.isBlank()) {
            return findAll(page, size);
        }
        return findAll(buildSearchSpecification(search.trim()), page, size);
    }

    /**
     * Liste paginée filtrée par une {@link Specification} JPA.
     *
     * @param specification critère JPA
     * @param page          numéro de page (0-based)
     * @param size          taille de page
     * @return page de vues
     */
    public PageDTO<V> findAll(Specification<E> specification, int page, int size) {
        Page<E> pageEntity = repository.findAll(specification, PageRequest.of(page, size));
        return new PageDTO<>(pageEntity.getContent().stream().map(mapper::toView).collect(Collectors.toList()), pageEntity.getTotalElements(), pageEntity.getTotalPages(), pageEntity.getSize(), pageEntity.getSort().isSorted());
    }

    /**
     * Construit le critère de recherche textuelle. Par défaut : aucun filtre (conjonction vraie).
     * Les sous-classes redéfinissent cette méthode pour chercher sur leurs champs.
     *
     * @param search texte déjà trimé
     * @return spécification JPA
     */
    protected Specification<E> buildSearchSpecification(String search) {
        return (root, query, cb) -> cb.conjunction();
    }

    /**
     * Transforme un texte en motif SQL {@code LIKE} insensible à la casse ({@code %valeur%}).
     *
     * @param search texte de recherche
     * @return motif LIKE en minuscules
     */
    protected static String likePattern(String search) {
        return "%" + search.toLowerCase() + "%";
    }

    /**
     * @param id identifiant de l'entité
     * @return vue DTO, ou {@code null} si absente
     */
    public V findById(Long id) {
        return repository.findById(id).map(mapper::toView).orElse(null);
    }

    /**
     * Persiste une nouvelle entité à partir du DTO d'écriture.
     *
     * @param dto données d'écriture
     * @return vue de l'entité sauvegardée
     */
    public V save(D dto) {
        return mapper.toView(repository.save(mapper.toEntity(dto)));
    }

    /**
     * @param id identifiant de l'entité à supprimer
     */
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    /**
     * Remplace l'entité persistée par le mapping du DTO (même identifiant).
     *
     * @param dto données d'écriture
     */
    public void update(D dto) {
        repository.save(mapper.toEntity(dto));
    }

}
