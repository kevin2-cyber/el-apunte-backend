package com.kimikevin.el_apunte.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 60 * 15;
    private final SecretKey secretKey;

    public JwtService(@Value("${JWT_SECRET}") String jwtSecret) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String username) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ACCESS_TOKEN_EXPIRATION_SECONDS)))
                .signWith(secretKey)
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return ACCESS_TOKEN_EXPIRATION_SECONDS;
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}
