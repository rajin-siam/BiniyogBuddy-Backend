package com.biniyogbuddy.common.exception;

import com.biniyogbuddy.common.MessageParser;
import com.biniyogbuddy.common.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<MessageResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<MessageResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<MessageResponse> handleInvalidTokenException(InvalidTokenException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }

    @ExceptionHandler(PendingVerificationException.class)
    public ResponseEntity<MessageResponse> handlePendingVerificationException(PendingVerificationException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }

    @ExceptionHandler(OtpInvalidException.class)
    public ResponseEntity<MessageResponse> handleOtpInvalidException(OtpInvalidException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<MessageResponse> handleOtpExpiredException(OtpExpiredException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<MessageResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }

    @ExceptionHandler(OtpResendLimitExceededException.class)
    public ResponseEntity<MessageResponse> handleOtpResendLimitExceededException(OtpResendLimitExceededException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageResponse> handleUserNotFoundException(UserNotFoundException ex) {
        MessageParser.ParsedMessage parsed = MessageParser.parse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(parsed.message(), null, parsed.errorCode()));
    }
}
