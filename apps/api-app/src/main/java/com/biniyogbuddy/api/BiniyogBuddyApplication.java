package com.biniyogbuddy.api;

import com.biniyogbuddy.users.entity.ExperienceLevel;
import com.biniyogbuddy.users.entity.Role;
import com.biniyogbuddy.users.entity.User;
import com.biniyogbuddy.users.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackages = "com.biniyogbuddy")
@EnableJpaRepositories(basePackages = "com.biniyogbuddy.users.repository")
@EntityScan(basePackages = "com.biniyogbuddy.users.entity")
public class BiniyogBuddyApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BiniyogBuddyApplication.class, args);

        UserRepository userRepository = context.getBean(UserRepository.class);
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);

        if (userRepository.count() == 0) {
            User testUser = User.builder()
                    .email("test@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .username("testuser")
                    .fullName("Test User")
                    .experienceLevel(ExperienceLevel.BEGINNER)
                    .role(Role.ROLE_USER)
                    .build();
            userRepository.save(testUser);
            System.out.println("Test user created: test@example.com / password123");
        }
    }
}


