package com.biniyogbuddy.api.controller.auth;

import com.biniyogbuddy.auth.dto.AuthLoginRequest;
import com.biniyogbuddy.auth.dto.AuthRegisterRequest;
import com.biniyogbuddy.auth.dto.AuthResponse;
import com.biniyogbuddy.auth.service.AuthService;
import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.common.dto.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody AuthRegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        String message = messageSource.getMessage("auth.register.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(message, "success", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        String message = messageSource.getMessage("auth.login.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(new ApiResponse<>(message, "success", authResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request) {
        authService.logout(request.getHeader("Authorization").substring(7));
        String message = messageSource.getMessage("auth.logout.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
