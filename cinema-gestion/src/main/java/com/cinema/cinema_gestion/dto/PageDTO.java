package com.cinema.cinema_gestion.dto;

import java.util.List;

public class PageDTO<T> {
    private List<T> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer pageSize;
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
