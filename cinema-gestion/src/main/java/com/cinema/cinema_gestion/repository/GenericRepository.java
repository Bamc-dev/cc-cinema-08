package com.cinema.cinema_gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.cinema.cinema_gestion.entity.BaseEntity;

/**
 * Repository Spring Data générique (CRUD + {@link JpaSpecificationExecutor}).
 * Annoté {@link NoRepositoryBean} : non instancié directement.
 *
 * @param <T> entité JPA héritant de {@link BaseEntity}
 */
@NoRepositoryBean
public interface GenericRepository<T extends BaseEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
}
