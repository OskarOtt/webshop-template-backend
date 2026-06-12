package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.model.Cart;
import com.oskarott.webshoptemplatebackend.model.UserEntity;
import com.oskarott.webshoptemplatebackend.repository.CartRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CartCreator {

    private final CartRepository cartRepository;

    public CartCreator(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Cart createForUser(UserEntity user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
}
