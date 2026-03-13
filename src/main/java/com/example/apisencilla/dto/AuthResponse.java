package com.example.apisencilla.dto;

public record AuthResponse(
        String token,
        String username,
        String role
) {
}
