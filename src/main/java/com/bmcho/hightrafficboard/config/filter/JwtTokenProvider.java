package com.bmcho.hightrafficboard.config.filter;

import com.bmcho.hightrafficboard.config.properties.JwtProperties;
import com.bmcho.hightrafficboard.domain.UserDomain;
import com.bmcho.hightrafficboard.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserService userService;
    private final JwtProperties jwtProperties;

    public Authentication getAuthentication(String accessToken) {

        if (!validateToken(accessToken)) {
            throw new JwtException("token validation filed");
        }

        long id = parseClaims(accessToken).get("id", Long.class);
        UserDomain user = userService.getUserById(id);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails principal = new User(user.getUsername(), null, authorities);

        return new UsernamePasswordAuthenticationToken(principal, id, authorities);
    }

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

    public long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object credentials = authentication.getCredentials();
        if (credentials instanceof Long id) {
            return id;
        }
        throw new IllegalStateException("Invalid authentication credentials");
    }

}
