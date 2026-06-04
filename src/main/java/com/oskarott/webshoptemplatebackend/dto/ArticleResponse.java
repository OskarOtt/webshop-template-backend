package com.oskarott.webshoptemplatebackend.dto;

import com.oskarott.webshoptemplatebackend.model.Article;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String category,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ArticleResponse from(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getName(),
                article.getDescription(),
                article.getPrice(),
                article.getStockQuantity(),
                article.getCategory(),
                article.getImageUrl(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}
