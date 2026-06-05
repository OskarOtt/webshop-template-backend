package com.oskarott.webshoptemplatebackend.dto;

public record BrandRequest(
        String name,
        String logoUrl,
        String description
) {}
