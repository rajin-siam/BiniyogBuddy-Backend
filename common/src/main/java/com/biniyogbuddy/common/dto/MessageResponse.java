package com.biniyogbuddy.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageResponse(String message, Long id, Integer errorCode) {

    public MessageResponse(String message) {
        this(message, null, null);
    }

    public MessageResponse(String message, Long id) {
        this(message, id, null);
    }
}
