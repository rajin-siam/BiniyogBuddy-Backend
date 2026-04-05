package com.biniyogbuddy.api.controller.users;

import com.biniyogbuddy.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final MessageSource messageSource;

    @GetMapping("/home")
    public ResponseEntity<MessageResponse> home() {
        String message = messageSource.getMessage("general.welcome", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
