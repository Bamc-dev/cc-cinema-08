package com.cinema.cinema_gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import com.cinema.cinema_gestion.entity.security.RefreshToken;
import com.cinema.cinema_gestion.entity.security.User;

/**
 * Accès persistance des {@link RefreshToken} JWT.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
     * @param token valeur brute du refresh token
     * @return jeton correspondant, s'il existe
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * @param user propriétaire du jeton
     * @return jeton de l'utilisateur, s'il existe
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * Supprime le(s) refresh token(s) de l'utilisateur (déconnexion).
     *
     * @param user propriétaire
     */
    void deleteByUser(User user);
}
