package com.example.DocumentManager.text;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Component
public class StopWords {

    private final Set<String> stopWords = new HashSet<>();

    // ✔ Manual getter replacing @Getter
    public Set<String> getStopWords() {
        return stopWords;
    }

    @PostConstruct
    public void load() {
        try {
            ClassPathResource resource = new ClassPathResource("stopwords.txt");

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = br.readLine()) != null) {
                    String word = line.trim().toLowerCase();
                    if (!word.isEmpty()) {
                        stopWords.add(word);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load stopwords", e);
        }
    }

    public boolean isStopWord(String word) {
        return stopWords.contains(word.toLowerCase());
    }
}
