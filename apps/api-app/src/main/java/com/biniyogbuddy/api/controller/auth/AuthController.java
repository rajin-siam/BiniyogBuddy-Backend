package com.biniyogbuddy.api.controller.auth;

import com.biniyogbuddy.auth.dto.AuthLoginRequest;
import com.biniyogbuddy.auth.dto.AuthRegisterRequest;
import com.biniyogbuddy.auth.dto.AuthResponse;
import com.biniyogbuddy.auth.dto.ForgotPasswordRequest;
import com.biniyogbuddy.auth.dto.LogoutRequest;
import com.biniyogbuddy.auth.dto.RefreshTokenRequest;
import com.biniyogbuddy.auth.dto.ResendOtpRequest;
import com.biniyogbuddy.auth.dto.ResetPasswordRequest;
import com.biniyogbuddy.auth.dto.ResetTokenResponse;
import com.biniyogbuddy.auth.dto.VerifyForgotPasswordRequest;
import com.biniyogbuddy.auth.dto.VerifyOtpRequest;
import com.biniyogbuddy.auth.service.AuthService;
import com.biniyogbuddy.auth.util.JwtUtil;
import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.common.dto.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MessageResource messageResource;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        MessageResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-registration")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyRegistration(@Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse authResponse = authService.verifyRegistration(request.email(), request.otp());
        String message = messageResource.getMessage("auth.verify.registration.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        String message = messageResource.getMessage("auth.login.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", authResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        Long userId = jwtUtil.extractUserId(request.refreshToken());
        AuthResponse authResponse = authService.refresh(userId, request.refreshToken());
        String message = messageResource.getMessage("auth.refresh.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", authResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request, @Valid @RequestBody LogoutRequest logoutRequest) {
        String accessToken = request.getHeader("Authorization").substring(7);
        authService.logout(accessToken, logoutRequest.refreshToken());
        String message = messageResource.getMessage("auth.logout.success");
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        MessageResponse response = authService.forgotPassword(request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-forgot-password")
    public ResponseEntity<ApiResponse<ResetTokenResponse>> verifyForgotPassword(@Valid @RequestBody VerifyForgotPasswordRequest request) {
        ResetTokenResponse resetTokenResponse = authService.verifyForgotPassword(request.email(), request.otp());
        String message = messageResource.getMessage("auth.verify.forgot.password.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", resetTokenResponse));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<AuthResponse>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        AuthResponse authResponse = authService.resetPassword(request);
        String message = messageResource.getMessage("auth.reset.password.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", authResponse));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        MessageResponse response = authService.resendOtp(request.email(), request.type());
        return ResponseEntity.ok(response);
    }
}
