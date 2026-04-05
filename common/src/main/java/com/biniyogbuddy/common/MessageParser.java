package com.biniyogbuddy.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

    private static final Pattern CODE_PREFIX = Pattern.compile("^(\\d{4}):(.+)$", Pattern.DOTALL);

    private MessageParser() {
    }

    public record ParsedMessage(String message, Integer errorCode) {
    }

    public static ParsedMessage parse(String raw) {
        if (raw == null) {
            return new ParsedMessage("", null);
        }
        Matcher matcher = CODE_PREFIX.matcher(raw);
        if (matcher.matches()) {
            return new ParsedMessage(matcher.group(2).trim(), Integer.parseInt(matcher.group(1)));
        }
        return new ParsedMessage(raw, null);
    }
}
