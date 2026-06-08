package com.oskarott.webshoptemplatebackend.dto;

import com.oskarott.webshoptemplatebackend.model.CartItem;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long articleId,
        String articleName,
        String mainImageUrl,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {
    public static CartItemResponse from(CartItem item) {
        BigDecimal unitPrice = item.getArticle().getPrice();
        return new CartItemResponse(
                item.getId(),
                item.getArticle().getId(),
                item.getArticle().getName(),
                item.getArticle().getImages().isEmpty() ? null : item.getArticle().getImages().get(0),
                unitPrice,
                item.getQuantity(),
                unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()))
        );
    }
}
