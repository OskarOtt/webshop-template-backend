package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.dto.AddToCartRequest;
import com.oskarott.webshoptemplatebackend.dto.CartResponse;
import com.oskarott.webshoptemplatebackend.dto.UpdateCartItemRequest;
import com.oskarott.webshoptemplatebackend.model.*;
import com.oskarott.webshoptemplatebackend.repository.CartItemRepository;
import com.oskarott.webshoptemplatebackend.repository.CartRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ArticleService articleService;
    private final CartCreator cartCreator;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ArticleService articleService,
                       CartCreator cartCreator) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.articleService = articleService;
        this.cartCreator = cartCreator;
    }

    public CartResponse getCart(UserEntity user) {
        return CartResponse.from(getOrCreateCart(user));
    }

    public CartResponse addItem(UserEntity user, AddToCartRequest request) {
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Article article = articleService.findOrThrow(request.articleId());
        if (article.getStatus() != ArticleStatus.ACTIVE) {
            throw new IllegalArgumentException("Article is not available for purchase");
        }

        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository
                .findByCartIdAndArticleId(cart.getId(), article.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setArticle(article);
                    newItem.setQuantity(0);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + request.quantity());
        cartItemRepository.save(item);

        return CartResponse.from(cartRepository.findByUserId(user.getId()).orElseThrow());
    }

    public CartResponse updateItem(UserEntity user, Long articleId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository
                .findByCartIdAndArticleId(cart.getId(), articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not in cart: " + articleId));

        if (request.quantity() <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(request.quantity());
        }

        return CartResponse.from(cart);
    }

    public CartResponse removeItem(UserEntity user, Long articleId) {
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository
                .findByCartIdAndArticleId(cart.getId(), articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not in cart: " + articleId));

        cart.getItems().remove(item);

        return CartResponse.from(cart);
    }

    public CartResponse clearCart(UserEntity user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
        return CartResponse.from(cart);
    }

    private Cart getOrCreateCart(UserEntity user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            try {
                return cartCreator.createForUser(user);
            } catch (DataIntegrityViolationException e) {
                return cartRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new IllegalStateException("Cart not found after concurrent creation", e));
            }
        });
    }
}
