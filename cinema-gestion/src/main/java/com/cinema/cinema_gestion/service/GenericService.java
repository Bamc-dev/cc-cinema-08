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

public abstract class GenericService<E extends BaseEntity, D extends BaseDTO, V extends Record, M extends GenericMapper<E, D, V>, R extends GenericRepository<E>> {
    protected R repository;
    protected M mapper;
    protected GenericService(R repository, M mapper) {
        this.mapper = mapper;
        this.repository = repository;
    }
    
    public PageDTO<V> findAll(int page, int size) {
        Page<E> pageEntity = repository.findAll(PageRequest.of(page, size));
        return new PageDTO<>(pageEntity.getContent().stream().map(mapper::toView).collect(Collectors.toList()), pageEntity.getTotalElements(), pageEntity.getTotalPages(), pageEntity.getSize(), pageEntity.getSort().isSorted());
    }

    public PageDTO<V> findAll(String search, int page, int size) {
        if (search == null || search.isBlank()) {
            return findAll(page, size);
        }
        return findAll(buildSearchSpecification(search.trim()), page, size);
    }

    public PageDTO<V> findAll(Specification<E> specification, int page, int size) {
        Page<E> pageEntity = repository.findAll(specification, PageRequest.of(page, size));
        return new PageDTO<>(pageEntity.getContent().stream().map(mapper::toView).collect(Collectors.toList()), pageEntity.getTotalElements(), pageEntity.getTotalPages(), pageEntity.getSize(), pageEntity.getSort().isSorted());
    }

    protected Specification<E> buildSearchSpecification(String search) {
        return (root, query, cb) -> cb.conjunction();
    }

    protected static String likePattern(String search) {
        return "%" + search.toLowerCase() + "%";
    }

    public V findById(Long id) {
        return repository.findById(id).map(mapper::toView).orElse(null);
    }

    public V save(D dto) {
        return mapper.toView(repository.save(mapper.toEntity(dto)));
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public void update(D dto) {
        repository.save(mapper.toEntity(dto));
    }

}
