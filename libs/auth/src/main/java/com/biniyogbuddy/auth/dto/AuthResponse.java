package com.biniyogbuddy.auth.dto;

public record AuthResponse(
        Long userId,
        String email,
        String role,
        String accessToken,
        String refreshToken,
        String tokenType
) {
    public AuthResponse(Long userId, String email, String role, String accessToken, String refreshToken) {
        this(userId, email, role, accessToken, refreshToken, "Bearer");
    }
}
