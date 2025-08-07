package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.config.properties.JwtProperties;
import com.bmcho.hightrafficboard.controller.user.dto.UserResponse;
import com.bmcho.hightrafficboard.domain.UserDomain;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final UserService userService;
    private final JwtProperties jwtProperties;

    public Boolean validateToken(String accessToken) {
        try {
            Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(accessToken);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public UserDomain getUserByAccessToken(String accessToken) {
        return userService.getUserById(
            parseClaims(accessToken).get("id", Long.class)
        );
    }

    private PrivateKey getPrivateKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getPrivateKey());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Invalid private key", e);
        }
    }

    private PublicKey getPublicKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getPublicKey());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Invalid public key", e);
        }
    }

    public String generatedToken(long id) {
        Date now = new Date();
        Instant instant = now.toInstant();

        return Jwts.builder()
            .claim("id", id)
            .issuedAt(now)
            .expiration(Date.from(instant.plus(Duration.ofHours(jwtProperties.getExpire().getAccessToken()))))
            .signWith(getPrivateKey(), Jwts.SIG.RS256)
            .compact();
    }

    public String generatedToken(long id, Duration expireAt) {
        Date now = new Date();
        Instant instant = now.toInstant();

        return Jwts.builder()
            .claim("id", id)
            .issuedAt(now)
            .expiration(Date.from(instant.plus(expireAt)))
            .signWith(getPrivateKey(), Jwts.SIG.RS256)
            .compact();
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
