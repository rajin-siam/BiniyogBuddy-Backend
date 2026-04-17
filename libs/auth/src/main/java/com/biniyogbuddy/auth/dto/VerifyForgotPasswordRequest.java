package com.biniyogbuddy.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyForgotPasswordRequest(
        @NotBlank @Email String email,
        @NotBlank String otp
) {}
