package com.biniyogbuddy.auth.service;

import com.biniyogbuddy.common.exception.OtpResendLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpRateLimitService {

    private static final String RESEND_PREFIX = "resend:count:";
    private static final int MAX_RESEND = 3;
    private static final long WINDOW_MINUTES = 5;

    private final StringRedisTemplate redisTemplate;

    public void checkAndIncrement(String type, String email) {
        String key = RESEND_PREFIX + type + ":" + email;
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;

        if (count >= MAX_RESEND) {
            throw new OtpResendLimitExceededException("OTP resend limit exceeded. Please try again later.");
        }

        redisTemplate.opsForValue().increment(key);
        if (count == 0) {
            redisTemplate.expire(key, WINDOW_MINUTES, TimeUnit.MINUTES);
        }
    }

    public void reset(String type, String email) {
        redisTemplate.delete(RESEND_PREFIX + type + ":" + email);
    }
}
