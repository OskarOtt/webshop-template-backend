package com.oskarott.webshoptemplatebackend.dto;

public record TokenResponse(String token, String refreshToken, String type, long expiresIn) {
    public static TokenResponse bearer(String token, String refreshToken, long expiresIn) {
        return new TokenResponse(token, refreshToken, "Bearer", expiresIn);
    }
}
