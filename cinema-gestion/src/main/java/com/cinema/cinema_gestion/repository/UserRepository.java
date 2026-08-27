package com.cinema.cinema_gestion.repository;

import java.util.Optional;

import com.cinema.cinema_gestion.entity.security.User;

/**
 * Accès persistance des {@link User}.
 */
public interface UserRepository extends GenericRepository<User> {
    /**
     * @param email adresse e-mail (unique)
     * @return utilisateur correspondant, s'il existe
     */
    Optional<User> findByEmail(String email);
}
