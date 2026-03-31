package com.biniyogbuddy.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRegisterRequest(
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String username,
        @NotBlank String fullName
) {}
