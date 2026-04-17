package com.biniyogbuddy.auth.service;

import com.biniyogbuddy.common.exception.OtpExpiredException;
import com.biniyogbuddy.common.exception.OtpInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final String OTP_REG_PREFIX = "otp:reg:";
    private static final String OTP_FP_PREFIX = "otp:fp:";
    private static final long OTP_TTL_MINUTES = 5;
    private static final int OTP_LENGTH = 6;

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateAndStoreOtp(String email, String type) {
        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        String key = getKey(type, email);
        redisTemplate.opsForValue().set(key, otp, OTP_TTL_MINUTES, TimeUnit.MINUTES);
        return otp;
    }

    public void validateOtp(String email, String otp, String type) {
        String key = getKey(type, email);
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null) {
            throw new OtpExpiredException("OTP has expired. Please request a new one.");
        }

        if (!storedOtp.equals(otp)) {
            throw new OtpInvalidException("Invalid OTP. Please try again.");
        }
    }

    public void deleteOtp(String email, String type) {
        redisTemplate.delete(getKey(type, email));
    }

    private String getKey(String type, String email) {
        return ("registration".equals(type) ? OTP_REG_PREFIX : OTP_FP_PREFIX) + email;
    }
}
