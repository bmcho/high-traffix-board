package com.bmcho.hightrafficboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "jwt_blacklist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtBlacklistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true,  columnDefinition = "TEXT")
    private String token;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @Column(nullable = false)
    private String email;

    public JwtBlacklistEntity(String token, LocalDateTime expirationTime, String email) {
        this.token = token;
        this.expirationTime = expirationTime;
        this.email = email;
    }
}