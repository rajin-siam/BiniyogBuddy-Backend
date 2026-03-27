# AGENTS.md - BiniyogBuddy Development Guide

This file provides guidance for agentic coding agents working on the BiniyogBuddy project.

## Project Overview

BiniyogBuddy is a Spring Boot 4.0.3 backend API for a stock trading journal aimed at beginner investors in Bangladesh (DSE/CSE markets). The project uses Java 21, PostgreSQL, and follows a multi-module Gradle architecture.

---

## Build, Lint, and Test Commands

### Build Commands
```bash
# Build entire project
./gradlew build

# Build without tests
./gradlew build -x test

# Build a specific module
./gradlew :libs:users:build
./gradlew :apps:api-app:build
```

### Running the Application
```bash
./gradlew :apps:api-app:bootRun
./gradlew :apps:api-app:bootRun --args='--spring.profiles.active=dev'
```

### Test Commands
```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :apps:api-app:test
./gradlew :libs:users:test

# Run a single test class (format: fully.qualified.ClassName)
./gradlew test --tests "com.biniyogbuddy.api.BiniyogBuddyApplicationTests"
./gradlew test --tests "com.biniyogbuddy.users.UsersModuleTests"

# Run a specific test method
./gradlew test --tests "com.biniyogbuddy.api.BiniyogBuddyApplicationTests.contextLoads"
```

---

## Code Style Guidelines

### Project Structure
```
biniyogbuddy/
├── apps/api-app/              # Main Spring Boot application
├── libs/users/                # User module
└── common/                    # Shared code (DTOs, exceptions)
```

### Package Naming
- Base: `com.biniyogbuddy`
- Modules: `com.biniyogbuddy.{module}.{layer}` (entity, repository, service, controller, dto, config, exception)

### Java Version
- **Java 21 required** (configured in `build.gradle.kts`)

### Naming Conventions

| Type | Convention | Examples |
|------|------------|----------|
| Classes/Interfaces | PascalCase | `UserService`, `StockController`, `ApiResponse` |
| Methods | camelCase | `registerUser()`, `findByEmail()`, `getStocksByUserId()` |
| Variables | camelCase | `userId`, `email`, `passwordHash` |
| Entities | PascalCase | `User`, `Stock`, `Trade` |
| DTOs | PascalCase or Record | `LoginRequest`, `UserResponse` |

- Query methods: `findBy*`, `get*`, `search*`
- Mutate methods: `create*`, `update*`, `delete*`

---

## Implementation Patterns

### Entity (with Lombok)
```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    protected void onCreate() { }

    @PreUpdate
    protected void onUpdate() { }
}
```

### Record DTOs (immutable)
```java
public record ApiResponse<T>(String message, String status, T data) {}

public record RegisterRequest(
    @NotBlank String email,
    @NotBlank String password,
    String username
) {}
```

### Service Layer
```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse create(RegisterRequest request) {
        // validation, business logic, save
        return response;
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) { }
}
```

### Controller Layer
```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Created", "success", response));
    }
}
```

---

## Key Guidelines

- **DTOs**: Use records for immutability; avoid `@Data` on DTOs
- **Transactions**: Always use `@Transactional` for database operations
- **Exceptions**: Use `@RestControllerAdvice` for global handling, return `ApiResponse` with `"error"` status
- **Validation**: Use `@Valid` on request bodies, Jakarta validation annotations
- **Security**: Public endpoints: `/api/v1/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- **Database**: PostgreSQL dialect; use `ddl-auto: update` only in development

---

## Important Files

- `build.gradle.kts` - Root build config
- `gradle.properties` - Versions (Spring Boot 4.0.3, Java 21, Lombok 1.18.36)
- `settings.gradle.kts` - Module includes: `common`, `libs:users`, `apps:api-app`
- `apps/api-app/src/main/resources/application.yaml` - App config

---

## Dependencies

- Spring Boot: 4.0.3
- SpringDoc OpenAPI: 2.0.2
- JUnit: 5.10.0
- Lombok: 1.18.36
- PostgreSQL Driver (managed by Spring Boot BOM)
