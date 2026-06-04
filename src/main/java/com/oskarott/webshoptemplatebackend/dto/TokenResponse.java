package com.oskarott.webshoptemplatebackend.dto;

public record TokenResponse(String token, String type, long expiresIn) {
    public static TokenResponse bearer(String token, long expiresIn) {
        return new TokenResponse(token, "Bearer", expiresIn);
    }
}
