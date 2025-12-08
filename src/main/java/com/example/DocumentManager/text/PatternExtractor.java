package com.example.DocumentManager.text;



import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PatternExtractor {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "\\b\\d{10}\\b");

    private static final Pattern DATE_PATTERN = Pattern.compile(
            "\\b\\d{1,2}[/-]\\d{1,2}[/-]\\d{4}\\b");

    public String extractEmail(String text) {
        if (text == null) return null;
        Matcher m = EMAIL_PATTERN.matcher(text);
        return m.find() ? m.group() : null;
    }

    public String extractPhone(String text) {
        if (text == null) return null;
        Matcher m = PHONE_PATTERN.matcher(text);
        return m.find() ? m.group() : null;
    }

    public String extractDate(String text) {
        if (text == null) return null;
        Matcher m = DATE_PATTERN.matcher(text);
        return m.find() ? m.group() : null;
    }
}

