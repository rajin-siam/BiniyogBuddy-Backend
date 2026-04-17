package com.biniyogbuddy.auth.util;

import com.biniyogbuddy.users.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(User user) {
        return buildToken(user, expirationMs, "access");
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpirationMs, "refresh");
    }

    private String buildToken(User user, long expirationMs, String tokenType) {
        return Jwts.builder()
                .subject(user.getEmail())
                .id(UUID.randomUUID().toString())
                .claim("userId", user.getId())
                .claim("roleId", user.getRole().getId())
                .claim("tokenType", tokenType)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractJti(String token) {
        return extractClaims(token).getId();
    }

    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    public Long extractRoleId(String token) {
        return extractClaims(token).get("roleId", Long.class);
    }

    public String extractTokenType(String token) {
        return extractClaims(token).get("tokenType", String.class);
    }

    public Date getExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}
