package com.biniyogbuddy.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String USER_TOKENS_PREFIX = "user:tokens:";

    private final StringRedisTemplate redisTemplate;

    public void storeSession(Long userId, String refreshToken, String accessToken, long refreshTtlMs) {
        String key = USER_TOKENS_PREFIX + userId;
        redisTemplate.opsForHash().put(key, refreshToken, accessToken);
        redisTemplate.expire(key, refreshTtlMs, TimeUnit.MILLISECONDS);
    }

    public boolean isValid(Long userId, String refreshToken) {
        String key = USER_TOKENS_PREFIX + userId;
        return redisTemplate.opsForHash().hasKey(key, refreshToken);
    }

    public String getAccessToken(Long userId, String refreshToken) {
        String key = USER_TOKENS_PREFIX + userId;
        Object value = redisTemplate.opsForHash().get(key, refreshToken);
        return value != null ? value.toString() : null;
    }

    public void updateAccessToken(Long userId, String refreshToken, String newAccessToken) {
        String key = USER_TOKENS_PREFIX + userId;
        if (redisTemplate.opsForHash().hasKey(key, refreshToken)) {
            redisTemplate.opsForHash().put(key, refreshToken, newAccessToken);
        }
    }

    public void deleteSession(Long userId, String refreshToken) {
        String key = USER_TOKENS_PREFIX + userId;
        redisTemplate.opsForHash().delete(key, refreshToken);
    }

    public List<String> getAllAccessTokens(Long userId) {
        String key = USER_TOKENS_PREFIX + userId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        return new ArrayList<>(entries.values().stream().map(Object::toString).toList());
    }

    public Set<Object> getAllSessions(Long userId) {
        String key = USER_TOKENS_PREFIX + userId;
        return redisTemplate.opsForHash().keys(key);
    }

    public void deleteAllSessions(Long userId) {
        redisTemplate.delete(USER_TOKENS_PREFIX + userId);
    }
}
