package com.biniyogbuddy.auth.config;

import com.biniyogbuddy.auth.service.RoleCacheService;
import com.biniyogbuddy.users.entity.ExperienceLevel;
import com.biniyogbuddy.users.entity.Role;
import com.biniyogbuddy.users.entity.User;
import com.biniyogbuddy.users.repository.RoleRepository;
import com.biniyogbuddy.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleCacheService roleCacheService;

    @Value("${app.super-admin.email:admin@biniyogbuddy.com}")
    private String superAdminEmail;

    @Value("${app.super-admin.password:admin123}")
    private String superAdminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedRoles();
        seedSuperAdmin();
        roleCacheService.loadRoles();
    }

    private void seedRoles() {
        List<String> roleNames = List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN");
        for (String roleName : roleNames) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = Role.builder().name(roleName).build();
                roleRepository.save(role);
            }
        }
    }

    private void seedSuperAdmin() {
        if (userRepository.findByEmail(superAdminEmail).isEmpty()) {
            Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ROLE_SUPER_ADMIN not found"));

            User superAdmin = User.builder()
                    .email(superAdminEmail)
                    .password(passwordEncoder.encode(superAdminPassword))
                    .username("superadmin")
                    .fullName("Super Admin")
                    .experienceLevel(ExperienceLevel.EXPERT)
                    .role(superAdminRole)
                    .isVerified(true)
                    .build();

            userRepository.save(superAdmin);
        }
    }
}
