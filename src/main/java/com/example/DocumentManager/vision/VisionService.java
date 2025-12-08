package com.example.DocumentManager.vision;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class VisionService {

    @Value("${google.vision.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or null");
        }

        try {
            // 1️⃣ Convert file to Base64
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());

            // 2️⃣ Build JSON request for Vision API
            Map<String, Object> requestBody = Map.of(
                    "requests", List.of(
                            Map.of(
                                    "image", Map.of("content", base64),
                                    "features", List.of(
                                            Map.of("type", "DOCUMENT_TEXT_DETECTION")
                                    )
                            )
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 3️⃣ Vision API endpoint
            String url = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Vision API error: HTTP " + response.getStatusCode() + " - " + response.getBody());
            }

            // 4️⃣ Parse JSON response
            String body = response.getBody();
            if (body == null || body.isBlank()) {
                return "";
            }

            JsonNode root = objectMapper.readTree(body);
            JsonNode responses = root.path("responses");
            if (!responses.isArray() || responses.isEmpty()) {
                return "";
            }

            JsonNode first = responses.get(0);

            // 5️⃣ Extract fullTextAnnotation.text
            JsonNode fullText = first.path("fullTextAnnotation").path("text");
            if (!fullText.isMissingNode() && !fullText.asText().isBlank()) {
                return fullText.asText();
            }

            // 6️⃣ Fallback: use textAnnotations[0].description
            JsonNode textAnnotations = first.path("textAnnotations");
            if (textAnnotations.isArray() && !textAnnotations.isEmpty()) {
                return textAnnotations.get(0).path("description").asText("");
            }

            return "";

        } catch (IllegalArgumentException e) {
            throw e; // rethrow invalid file error
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract text from Vision API: " + e.getMessage(), e);
        }
    }
}
