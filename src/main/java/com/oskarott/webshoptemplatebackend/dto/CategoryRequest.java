package com.oskarott.webshoptemplatebackend.dto;

public record CategoryRequest(
        String name,
        Long parentId
) {}
