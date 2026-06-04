package com.oskarott.webshoptemplatebackend.dto;

import java.math.BigDecimal;

public record ArticleRequest(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String category,
        String imageUrl
) {}
