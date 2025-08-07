package com.bmcho.hightrafficboard.repository;

import com.bmcho.hightrafficboard.entity.JwtBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklistEntity, Long> {

    Optional<JwtBlacklistEntity> findByToken(String token);

    @Query(value = "SELECT * FROM jwt_blacklist WHERE email = :email ORDER BY expiration_time LIMIT 1", nativeQuery = true)
    Optional<JwtBlacklistEntity> findTopByEmailOrderByExpirationTime(@Param("email") String email);
}