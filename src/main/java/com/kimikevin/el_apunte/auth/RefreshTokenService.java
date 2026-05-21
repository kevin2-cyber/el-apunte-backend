package com.kimikevin.el_apunte.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 60L * 60 * 24 * 30;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public String createRefreshToken(AppUser user) {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken(
                token,
                user,
                Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS)
        );

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    @Transactional(readOnly = true)
    public AppUser validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid refresh token"
                ));

        if (refreshToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        return refreshToken.getUser();
    }
}
