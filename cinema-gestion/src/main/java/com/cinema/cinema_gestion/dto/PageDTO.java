package com.cinema.cinema_gestion.dto;

import java.util.List;

/**
 * Enveloppe de pagination pour les listes renvoyées par l'API.
 *
 * @param <T> type des éléments de la page
 */
public class PageDTO<T> {
    /** Éléments de la page courante. */
    private List<T> content;
    /** Nombre total d'éléments, toutes pages confondues. */
    private Long totalElements;
    /** Nombre total de pages. */
    private Integer totalPages;
    /** Taille de page demandée. */
    private Integer pageSize;
    /** Indique si le résultat est trié. */
    private Boolean sorted;



    public PageDTO(List<T> content, Long totalElements, Integer totalPages, Integer pageSize, Boolean sorted) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageSize = pageSize;
        this.sorted = sorted;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Boolean getSorted() {
        return sorted;
    }

    public void setSorted(Boolean sorted) {
        this.sorted = sorted;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
