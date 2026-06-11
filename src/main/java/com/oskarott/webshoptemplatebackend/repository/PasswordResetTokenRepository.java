package com.oskarott.webshoptemplatebackend.repository;

import com.oskarott.webshoptemplatebackend.model.PasswordResetToken;
import com.oskarott.webshoptemplatebackend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    void deleteAllByUser(UserEntity user);
}
