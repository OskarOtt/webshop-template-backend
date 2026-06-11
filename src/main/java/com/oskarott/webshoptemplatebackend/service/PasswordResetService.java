package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.exception.UnauthorizedException;
import com.oskarott.webshoptemplatebackend.model.PasswordResetToken;
import com.oskarott.webshoptemplatebackend.model.UserEntity;
import com.oskarott.webshoptemplatebackend.repository.PasswordResetTokenRepository;
import com.oskarott.webshoptemplatebackend.repository.RefreshTokenRepository;
import com.oskarott.webshoptemplatebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${app.password-reset.expiration-minutes:30}")
    private int expirationMinutes;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                RefreshTokenRepository refreshTokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Generates a reset token and emails the link. Silently does nothing if the email
     * is not registered — this prevents user-enumeration attacks.
     */
    @Transactional
    public void initiateReset(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return;
        }
        UserEntity user = userOpt.get();

        tokenRepository.deleteAllByUser(user);

        String rawToken = UUID.randomUUID().toString();
        PasswordResetToken token = new PasswordResetToken();
        token.setTokenHash(hash(rawToken));
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        tokenRepository.save(token);

        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;
        emailService.sendPasswordResetEmail(user, resetLink);
    }

    /**
     * Validates the raw token, updates the user's password, marks the token as used,
     * and revokes all existing refresh tokens (forces re-login on all devices).
     */
    @Transactional
    public void confirmReset(String rawToken, String newPassword) {
        PasswordResetToken token = tokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired reset token"));

        if (token.isUsed()) {
            throw new UnauthorizedException("Reset token has already been used");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Reset token has expired");
        }

        UserEntity user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        refreshTokenRepository.revokeAllForUser(user.getId());
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
