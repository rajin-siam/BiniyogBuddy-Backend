# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BiniyogBuddy is a Spring Boot 4.0.3 REST API backend for a stock trading journal targeting beginner investors in Bangladesh (DSE/CSE markets). It is in early MVP development.

## Commands

```bash
# Build
./gradlew build                    # Full build with tests
./gradlew build -x test            # Build without tests
./gradlew :libs:users:build        # Build a specific module

# Run
./gradlew :apps:api-app:bootRun

# Test
./gradlew test                     # All tests
./gradlew :libs:users:test         # Module-specific tests
./gradlew test --tests "com.biniyogbuddy.api.BiniyogBuddyApplicationTests"          # Single class
./gradlew test --tests "com.biniyogbuddy.api.BiniyogBuddyApplicationTests.contextLoads"  # Single method
```

No lint tool is configured.

## Architecture

Multi-module Gradle project (Java 21, Spring Boot 4.0.3, PostgreSQL):

- **`common/`** — Shared cross-cutting code: `BaseEntity` (audit fields, soft delete via `@SQLRestriction`), `ApiResponse<T>` record, `GlobalExceptionHandler` (`@RestControllerAdvice`), `CommonConstants`
- **`libs/users/`** — User domain: JPA entity, Spring Data repository, `UserService`, `CustomUserDetailsService`, DTOs (records), `PasswordEncoderConfig`, `HomeController`
- **`libs/auth/`** — JWT authentication: `JwtUtil`, `JwtAuthenticationFilter`, `AuthService`, `AuthController` (`POST /api/v1/auth/register`, `POST /api/v1/auth/login`), `SecurityConfig` (stateless, JWT)
- **`apps/api-app/`** — Spring Boot entry point with `@SpringBootApplication(scanBasePackages="com.biniyogbuddy")`, `@EnableJpaRepositories`, `@EntityScan`. Bootstraps a test user on first run.

Planned but not yet implemented: `libs:stocks`, `libs:trades`, `libs:notes`, `apps:web-app`.

### Package naming

`com.biniyogbuddy.{module}.{layer}` — layers: `entity`, `repository`, `service`, `controller`, `dto`, `config`, `exception`

## Key Patterns

**Entities** extend `BaseEntity` (provides `id`, `createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`). Use `@Data`, `@SuperBuilder`, `@NoArgsConstructor`, `@AllArgsConstructor` from Lombok.

**DTOs** are immutable Java records. Never use `@Data` on DTOs.

**Services** use `@RequiredArgsConstructor` for injection. Write operations use `@Transactional`, reads use `@Transactional(readOnly = true)`.

**Controllers** return `ResponseEntity<ApiResponse<T>>`. Map to `/api/v1/{resource}`.

**Exception handling** goes in `GlobalExceptionHandler`; return `ApiResponse` with `"error"` status.

## Security

Spring Security is stateless JWT. The `JwtAuthenticationFilter` reads `Authorization: Bearer <token>`, validates it, and sets the `SecurityContext`. Public endpoints: `/api/v1/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`. All others require a valid token.

JWT config lives in `application.yaml` under `jwt.secret` (Base64-encoded) and `jwt.expiration-ms` (86400000 = 24 h). Token claims: `sub` (email), `userId`, `role`.

## Database

`apps/api-app/src/main/resources/application.yaml` configures the datasource. Development uses `ddl-auto: create-drop`. PostgreSQL dialect is `org.hibernate.dialect.PostgreSQLDialect`.

For Docker: `docker-compose.yml` in `docker/` starts PostgreSQL 16 + the API app.

## Dependency Versions

Managed in `gradle.properties`: Spring Boot 4.0.3, Lombok 1.18.36, SpringDoc OpenAPI 3.0.2, JUnit 5.10.0.
