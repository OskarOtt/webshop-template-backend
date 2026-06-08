package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.exception.UnauthorizedException;
import com.oskarott.webshoptemplatebackend.model.RefreshToken;
import com.oskarott.webshoptemplatebackend.model.UserEntity;
import com.oskarott.webshoptemplatebackend.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /** Generates a new opaque refresh token, stores its hash, and returns the raw token. */
    @Transactional
    public String createRefreshToken(UserEntity user) {
        String rawToken = UUID.randomUUID().toString();
        RefreshToken entity = new RefreshToken();
        entity.setTokenHash(hash(rawToken));
        entity.setUser(user);
        entity.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));
        refreshTokenRepository.save(entity);
        return rawToken;
    }

    /**
     * Validates the raw token: looks up by hash, checks not revoked and not expired.
     * Returns the stored entity so callers can access the associated user.
     */
    @Transactional
    public RefreshToken validate(String rawToken) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (stored.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }
        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }
        return stored;
    }

    /** Revokes the given token and issues a new one (rotation). Returns a result with the new raw token and the associated user. */
    @Transactional
    public RotateResult rotate(String rawToken) {
        RefreshToken old = validate(rawToken);
        old.setRevoked(true);
        refreshTokenRepository.save(old);
        UserEntity user = old.getUser();
        String newRawToken = createRefreshToken(user);
        return new RotateResult(newRawToken, user);
    }

    public record RotateResult(String newRawToken, UserEntity user) {}

    /** Revokes the given token (logout). Silently does nothing if the token is unknown. */
    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken)).ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
    }

    private String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
