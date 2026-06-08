package com.oskarott.webshoptemplatebackend.repository;

import com.oskarott.webshoptemplatebackend.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
