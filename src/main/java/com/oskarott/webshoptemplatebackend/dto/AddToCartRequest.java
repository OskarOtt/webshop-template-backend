package com.oskarott.webshoptemplatebackend.dto;

public record AddToCartRequest(
        Long articleId,
        int quantity
) {}
