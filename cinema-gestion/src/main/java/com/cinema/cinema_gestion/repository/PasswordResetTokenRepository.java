package com.cinema.cinema_gestion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cinema.cinema_gestion.entity.security.PasswordResetToken;
import com.cinema.cinema_gestion.entity.security.User;

/**
 * Accès persistance des {@link PasswordResetToken}.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    /**
     * @param token valeur brute du jeton de réinitialisation
     * @return jeton correspondant, s'il existe
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * @param user propriétaire du jeton
     * @return jeton de l'utilisateur, s'il existe
     */
    Optional<PasswordResetToken> findByUser(User user);

    /**
     * Supprime le(s) jeton(s) de réinitialisation de l'utilisateur.
     *
     * @param user propriétaire
     */
    void deleteByUser(User user);
}
