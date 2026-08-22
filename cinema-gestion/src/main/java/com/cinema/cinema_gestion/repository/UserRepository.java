package com.cinema.cinema_gestion.repository;

import java.util.Optional;

import com.cinema.cinema_gestion.entity.security.User;

public interface UserRepository extends GenericRepository<User> {
    Optional<User> findByEmail(String email);
}
