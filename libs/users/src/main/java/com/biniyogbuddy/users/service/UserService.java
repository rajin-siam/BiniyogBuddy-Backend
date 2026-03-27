package com.biniyogbuddy.users.service;

import com.biniyogbuddy.users.entity.Role;
import com.biniyogbuddy.users.entity.User;
import com.biniyogbuddy.users.repository.UserRepository;
import com.biniyogbuddy.users.dto.LoginRequest;
import com.biniyogbuddy.users.dto.LoginResponse;
import com.biniyogbuddy.users.dto.RegisterRequest;
import com.biniyogbuddy.users.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered: " + request.email());
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User newUser = User.builder()
                .username(request.username())
                .email(request.email())
                .password(hashedPassword)
                .fullName(request.fullName())
                .experienceLevel(request.experienceLevel())
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(newUser);
        return toUserResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        return new LoginResponse(toUserResponse(user));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUserName(Long userId, String fullName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setFullName(fullName);
        return userRepository.save(user);
    }

    public boolean validateCredentials(String email, String rawPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, user.get().getPassword());
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getExperienceLevel().getDisplayName()
        );
    }
}