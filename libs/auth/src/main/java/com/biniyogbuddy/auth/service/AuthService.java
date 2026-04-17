package com.biniyogbuddy.auth.service;

import com.biniyogbuddy.auth.dto.AuthLoginRequest;
import com.biniyogbuddy.auth.dto.AuthRegisterRequest;
import com.biniyogbuddy.auth.dto.AuthResponse;
import com.biniyogbuddy.auth.dto.ResetPasswordRequest;
import com.biniyogbuddy.auth.dto.ResetTokenResponse;
import com.biniyogbuddy.auth.util.JwtUtil;
import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.dto.MessageResponse;
import com.biniyogbuddy.common.exception.DuplicateResourceException;
import com.biniyogbuddy.common.exception.InvalidCredentialsException;
import com.biniyogbuddy.common.exception.InvalidTokenException;
import com.biniyogbuddy.common.exception.PasswordMismatchException;
import com.biniyogbuddy.common.exception.PendingVerificationException;
import com.biniyogbuddy.common.exception.UserNotFoundException;
import com.biniyogbuddy.users.entity.ExperienceLevel;
import com.biniyogbuddy.users.entity.Role;
import com.biniyogbuddy.users.entity.User;
import com.biniyogbuddy.users.repository.RoleRepository;
import com.biniyogbuddy.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String RESET_TOKEN_PREFIX = "reset:token:";
    private static final long RESET_TOKEN_TTL_MINUTES = 10;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final OtpRateLimitService otpRateLimitService;
    private final EmailService emailService;
    private final MessageResource messageResource;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public MessageResponse register(AuthRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            String message = messageResource.getMessage("auth.error.email.duplicate", request.email());
            throw new DuplicateResourceException(message);
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found"));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .username(request.username())
                .fullName(request.fullName())
                .experienceLevel(ExperienceLevel.BEGINNER)
                .role(userRole)
                .build();

        userRepository.save(user);

        String otp = otpService.generateAndStoreOtp(request.email(), "registration");
        emailService.sendOtpEmail(request.email(), otp, "registration");

        String message = messageResource.getMessage("auth.register.success");
        return new MessageResponse(message);
    }

    @Transactional
    public AuthResponse verifyRegistration(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(messageResource.getMessage("auth.error.user.not.found")));

        otpService.validateOtp(email, otp, "registration");
        otpService.deleteOtp(email, "registration");
        otpRateLimitService.reset("registration", email);

        user.setVerified(true);
        userRepository.save(user);

        return generateTokenPair(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthLoginRequest request) {
        String invalidCredentialsMessage = messageResource.getMessage("auth.error.invalid.credentials");

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException(invalidCredentialsMessage));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException(invalidCredentialsMessage);
        }

        if (!user.isVerified()) {
            String otp = otpService.generateAndStoreOtp(request.email(), "registration");
            emailService.sendOtpEmail(request.email(), otp, "registration");
            throw new PendingVerificationException(messageResource.getMessage("auth.error.pending.verification"));
        }

        return generateTokenPair(user);
    }

    public AuthResponse refresh(Long userId, String refreshToken) {
        String invalidRefreshMessage = messageResource.getMessage("auth.error.refresh.invalid");

        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new InvalidTokenException(invalidRefreshMessage);
        }

        if (!"refresh".equals(jwtUtil.extractTokenType(refreshToken))) {
            throw new InvalidTokenException(invalidRefreshMessage);
        }

        if (!refreshTokenService.isValid(userId, refreshToken)) {
            throw new InvalidTokenException(invalidRefreshMessage);
        }

        String email = jwtUtil.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException(invalidRefreshMessage));

        String newAccessToken = jwtUtil.generateToken(user);
        refreshTokenService.updateAccessToken(userId, refreshToken, newAccessToken);

        return new AuthResponse(user.getId(), user.getEmail(), user.getRole().getName(), newAccessToken, refreshToken);
    }

    public void logout(String accessToken, String refreshToken) {
        Long accessUserId = jwtUtil.extractUserId(accessToken);
        Long refreshUserId = jwtUtil.extractUserId(refreshToken);

        if (!accessUserId.equals(refreshUserId)) {
            throw new InvalidTokenException(messageResource.getMessage("auth.error.token.ownership"));
        }

        long ttlSeconds = (jwtUtil.getExpiration(accessToken).getTime() - System.currentTimeMillis()) / 1000;
        if (ttlSeconds > 0) {
            tokenBlacklistService.blacklist(accessToken, ttlSeconds);
        }

        refreshTokenService.deleteSession(accessUserId, refreshToken);
    }

    public MessageResponse forgotPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(messageResource.getMessage("auth.error.user.not.found")));

        String otp = otpService.generateAndStoreOtp(email, "forgot-password");
        emailService.sendOtpEmail(email, otp, "password reset");

        String message = messageResource.getMessage("auth.forgot.password.success");
        return new MessageResponse(message);
    }

    public ResetTokenResponse verifyForgotPassword(String email, String otp) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(messageResource.getMessage("auth.error.user.not.found")));

        otpService.validateOtp(email, otp, "forgot-password");
        otpService.deleteOtp(email, "forgot-password");
        otpRateLimitService.reset("forgot-password", email);

        String resetToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(RESET_TOKEN_PREFIX + resetToken, email, RESET_TOKEN_TTL_MINUTES, TimeUnit.MINUTES);

        return new ResetTokenResponse(resetToken);
    }

    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new PasswordMismatchException(messageResource.getMessage("auth.error.password.mismatch"));
        }

        String email = redisTemplate.opsForValue().get(RESET_TOKEN_PREFIX + request.resetToken());
        if (email == null) {
            throw new InvalidTokenException(messageResource.getMessage("auth.error.reset.token.invalid"));
        }

        redisTemplate.delete(RESET_TOKEN_PREFIX + request.resetToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(messageResource.getMessage("auth.error.user.not.found")));

        // Blacklist all existing access tokens for this user
        List<String> accessTokens = refreshTokenService.getAllAccessTokens(user.getId());
        long ttlSeconds = jwtUtil.getExpirationMs() / 1000;
        tokenBlacklistService.blacklistMultiple(accessTokens, ttlSeconds);

        // Wipe all sessions
        refreshTokenService.deleteAllSessions(user.getId());

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return generateTokenPair(user);
    }

    public MessageResponse resendOtp(String email, String type) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(messageResource.getMessage("auth.error.user.not.found")));

        otpRateLimitService.checkAndIncrement(type, email);

        String purpose = "registration".equals(type) ? "registration" : "password reset";
        String otp = otpService.generateAndStoreOtp(email, type);
        emailService.sendOtpEmail(email, otp, purpose);

        String message = messageResource.getMessage("auth.resend.otp.success");
        return new MessageResponse(message);
    }

    private AuthResponse generateTokenPair(User user) {
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        refreshTokenService.storeSession(user.getId(), refreshToken, accessToken, jwtUtil.getRefreshExpirationMs());
        return new AuthResponse(user.getId(), user.getEmail(), user.getRole().getName(), accessToken, refreshToken);
    }
}
