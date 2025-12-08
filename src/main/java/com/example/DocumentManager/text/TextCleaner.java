package com.example.DocumentManager.text;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextCleaner {

    private final StopWords stopWords;

    // ✔ Manual constructor replacing @RequiredArgsConstructor
    public TextCleaner(StopWords stopWords) {
        this.stopWords = stopWords;
    }

    /**
     * Takes raw text (from Vision) and returns:
     * - only alphabetic tokens
     * - no stopwords
     * - no very short words
     */
    public List<String> extractKeywords(String text) {
        if (text == null) return List.of();

        // 1. Lowercase
        text = text.toLowerCase();

        // 2. Remove everything except letters and spaces
        text = text.replaceAll("[^a-zA-Z ]", " ");

        // 3. Split on one or more spaces
        String[] parts = text.split("\\s+");

        List<String> keywords = new ArrayList<>();
        for (String word : parts) {
            if (word.length() <= 2) continue;          // ignore tiny words
            if (stopWords.isStopWord(word)) continue;  // ignore stopwords
            keywords.add(word);
        }

        return keywords;
    }

    /**
     * Build cleaned text from extracted keywords.
     */
    public String buildCleanedText(String text) {
        List<String> keywords = extractKeywords(text);
        return String.join(" ", keywords);
    }
}
