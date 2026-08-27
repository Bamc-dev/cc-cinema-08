package com.cinema.cinema_gestion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import com.cinema.cinema_gestion.dto.BaseDTO;
import com.cinema.cinema_gestion.dto.PageDTO;
import com.cinema.cinema_gestion.service.GenericService;

/**
 * Contrôleur CRUD générique : lecture paginée, création, mise à jour et suppression.
 * Les chemins {@code /admin/*} nécessitent un JWT (voir {@code SecurityConfig}).
 *
 * @param <D> DTO d'écriture
 * @param <V> DTO de lecture
 * @param <S> service métier
 */
public abstract class GenericCRUDController<D extends BaseDTO, V extends Record, S extends GenericService<?, D, V, ?, ?>>
        extends GenericController<D, V, S> {

    /**
     * @param service service métier de la ressource
     */
    protected GenericCRUDController(S service) {
        super(service);
    }

    /**
     * Recherche une ressource par identifiant.
     *
     * @param id identifiant de l'entité
     * @return vue DTO correspondante
     */
    @GetMapping("/find/{id}")
    public ResponseEntity<V> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Crée une ressource (espace administrateur).
     *
     * @param dto données d'écriture
     * @return vue de l'entité créée
     */
    @PostMapping("/admin/create")
    public ResponseEntity<V> create(@RequestBody D dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    /**
     * Met à jour une ressource (espace administrateur).
     *
     * @param id  identifiant de l'entité
     * @param dto données d'écriture (doit porter le même id)
     * @return 204 No Content
     */
    @PutMapping("/admin/update/{id}")
    public ResponseEntity<V> update(@PathVariable Long id, @RequestBody D dto) {
        service.update(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime une ressource (espace administrateur).
     *
     * @param id identifiant de l'entité
     * @return 204 No Content
     */
    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Liste paginée, avec recherche textuelle optionnelle (index de page 0-based).
     *
     * @param page   numéro de page (0-based)
     * @param size   taille de page
     * @param search filtre libre (optionnel)
     * @return page de vues DTO
     */
    @GetMapping("/list/{page}/{size}")
    public ResponseEntity<PageDTO<V>> list(@PathVariable int page, @PathVariable int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.findAll(search, page, size));
    }
}
