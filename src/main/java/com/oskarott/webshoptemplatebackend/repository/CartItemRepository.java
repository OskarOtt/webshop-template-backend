package com.oskarott.webshoptemplatebackend.repository;

import com.oskarott.webshoptemplatebackend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndArticleId(Long cartId, Long articleId);
}
