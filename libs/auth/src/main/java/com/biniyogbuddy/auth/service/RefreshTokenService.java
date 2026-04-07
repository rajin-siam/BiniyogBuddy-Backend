package com.biniyogbuddy.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String REFRESH_PREFIX = "refresh:token:";

    private final StringRedisTemplate redisTemplate;

    public void store(String token, long ttlMs) {
        redisTemplate.opsForValue().set(REFRESH_PREFIX + token, token, ttlMs, TimeUnit.MILLISECONDS);
    }

    public boolean isValid(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REFRESH_PREFIX + token));
    }

    public void delete(String token) {
        redisTemplate.delete(REFRESH_PREFIX + token);
    }
}
