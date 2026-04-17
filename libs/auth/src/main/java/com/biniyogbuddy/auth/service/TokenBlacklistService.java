package com.biniyogbuddy.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklisted:";

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String token, long ttlSeconds) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "1", ttlSeconds, TimeUnit.SECONDS);
    }

    public void blacklistMultiple(List<String> tokens, long ttlSeconds) {
        for (String token : tokens) {
            blacklist(token, ttlSeconds);
        }
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
