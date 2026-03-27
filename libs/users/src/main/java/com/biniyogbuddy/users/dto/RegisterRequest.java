package com.biniyogbuddy.users.dto;

import com.biniyogbuddy.users.entity.ExperienceLevel;

public record RegisterRequest (
        String email,
        String password,
        String username,
        String fullName,
        ExperienceLevel experienceLevel
) {
}