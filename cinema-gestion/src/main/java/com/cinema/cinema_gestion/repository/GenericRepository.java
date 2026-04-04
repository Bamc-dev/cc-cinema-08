package com.cinema.cinema_gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

import com.cinema.cinema_gestion.entity.BaseEntity;

@NoRepositoryBean
public interface GenericRepository<T extends BaseEntity> extends JpaRepository<T, Long>{

    Page<T> findAll(Pageable pageable);
    Page<T> findAll(Specification<T> specification, Pageable pageable);
}
