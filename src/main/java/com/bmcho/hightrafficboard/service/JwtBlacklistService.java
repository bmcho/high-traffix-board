
package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.entity.JwtBlacklistEntity;
import com.bmcho.hightrafficboard.repository.JwtBlacklistRepository;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final JwtBlacklistRepository jwtBlackListRepository;
    private final TokenService tokenService;

    public void blacklistToken(String currentToken) {
        TokenClaims tokenClaims = new TokenClaims(currentToken);
        JwtBlacklistEntity JwtBlacklist = new JwtBlacklistEntity(
            currentToken,
            tokenClaims.getExpirationTime(),
            tokenClaims.getEmail()
        );
        jwtBlackListRepository.save(JwtBlacklist);
    }

    public boolean isTokenBlacklisted(String currentToken) {
        TokenClaims tokenClaims = new TokenClaims(currentToken);
        Optional<JwtBlacklistEntity> blacklistedToken = jwtBlackListRepository.findTopByEmailOrderByExpirationTime(tokenClaims.getEmail());
        if (blacklistedToken.isEmpty()) {
            return false;
        }
        LocalDateTime localDateTime = tokenClaims.getExpirationTime();
        return blacklistedToken.get().getExpirationTime().isAfter(localDateTime.minusMinutes(60));
    }

    @Data
    @Getter
    private class TokenClaims {
        private long id;
        private String email;
        private LocalDateTime expirationTime;

        TokenClaims(String accessToken) {
            Claims claims = tokenService.parseClaims(accessToken);
            this.id = claims.get("id", Long.class);
            this.email = claims.get("email", String.class);
            this.expirationTime = LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
        }
    }
}