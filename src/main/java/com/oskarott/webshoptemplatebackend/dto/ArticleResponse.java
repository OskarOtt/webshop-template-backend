package com.oskarott.webshoptemplatebackend.dto;

import com.oskarott.webshoptemplatebackend.model.Article;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ArticleResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        CategoryResponse category,
        BrandResponse brand,
        List<String> images,
        String sku,
        String size,
        BigDecimal weight,
        String color,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public String mainImageUrl() {
        return (images != null && !images.isEmpty()) ? images.get(0) : null;
    }

    public static ArticleResponse from(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getName(),
                article.getDescription(),
                article.getPrice(),
                article.getStockQuantity(),
                article.getCategory() != null ? CategoryResponse.from(article.getCategory()) : null,
                article.getBrand() != null ? BrandResponse.from(article.getBrand()) : null,
                article.getImages(),
                article.getSku(),
                article.getSize(),
                article.getWeight(),
                article.getColor(),
                article.getTags(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}

