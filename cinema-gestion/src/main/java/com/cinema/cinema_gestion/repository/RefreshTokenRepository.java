package com.cinema.cinema_gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import com.cinema.cinema_gestion.entity.security.RefreshToken;
import com.cinema.cinema_gestion.entity.security.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
}
