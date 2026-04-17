package com.biniyogbuddy.common.exception;

public class OtpResendLimitExceededException extends RuntimeException {
    public OtpResendLimitExceededException(String message) {
        super(message);
    }
}
