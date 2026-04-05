package com.biniyogbuddy.auth.service;

import com.biniyogbuddy.auth.dto.AuthLoginRequest;
import com.biniyogbuddy.auth.dto.AuthRegisterRequest;
import com.biniyogbuddy.auth.dto.AuthResponse;
import com.biniyogbuddy.auth.util.JwtUtil;
import com.biniyogbuddy.common.exception.DuplicateResourceException;
import com.biniyogbuddy.common.exception.InvalidCredentialsException;
import com.biniyogbuddy.users.entity.ExperienceLevel;
import com.biniyogbuddy.users.entity.Role;
import com.biniyogbuddy.users.entity.User;
import com.biniyogbuddy.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final MessageSource messageSource;

    @Transactional
    public AuthResponse register(AuthRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            String message = messageSource.getMessage(
                    "auth.error.email.duplicate",
                    new Object[]{request.email()},
                    LocaleContextHolder.getLocale()
            );
            throw new DuplicateResourceException(message);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .username(request.username())
                .fullName(request.fullName())
                .experienceLevel(ExperienceLevel.BEGINNER)
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
        return new AuthResponse(jwtUtil.generateToken(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthLoginRequest request) {
        String invalidCredentialsMessage = messageSource.getMessage(
                "auth.error.invalid.credentials",
                null,
                LocaleContextHolder.getLocale()
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException(invalidCredentialsMessage));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException(invalidCredentialsMessage);
        }

        return new AuthResponse(jwtUtil.generateToken(user));
    }

    public void logout(String token) {
        long ttlSeconds = (jwtUtil.getExpiration(token).getTime() - System.currentTimeMillis()) / 1000;
        if (ttlSeconds > 0) {
            tokenBlacklistService.blacklist(token, ttlSeconds);
        }
    }
}
