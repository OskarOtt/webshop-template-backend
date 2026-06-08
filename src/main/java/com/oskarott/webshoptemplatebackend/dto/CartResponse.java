package com.oskarott.webshoptemplatebackend.dto;

import com.oskarott.webshoptemplatebackend.model.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        List<CartItemResponse> items,
        int itemCount,
        BigDecimal totalPrice
) {
    public static CartResponse from(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(CartItemResponse::from)
                .toList();

        int itemCount = itemResponses.stream().mapToInt(CartItemResponse::quantity).sum();
        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                itemResponses,
                itemCount,
                totalPrice
        );
    }
}
