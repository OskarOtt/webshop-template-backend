package com.oskarott.webshoptemplatebackend.dto;

import java.math.BigDecimal;
import java.util.List;

public record ArticleRequest(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        Long categoryId,
        Long brandId,
        List<String> images,
        String sku,
        String size,
        BigDecimal weight,
        String color,
        List<String> tags
) {}

