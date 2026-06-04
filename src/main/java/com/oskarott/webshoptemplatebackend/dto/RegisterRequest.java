package com.oskarott.webshoptemplatebackend.dto;

public record RegisterRequest(String email, String firstName, String lastName, String password) {
}
