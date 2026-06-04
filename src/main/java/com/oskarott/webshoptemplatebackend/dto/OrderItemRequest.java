package com.oskarott.webshoptemplatebackend.dto;

public record OrderItemRequest(
        Long articleId,
        int quantity
) {}
