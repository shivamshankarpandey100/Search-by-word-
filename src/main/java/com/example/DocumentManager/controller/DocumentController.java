package com.example.DocumentManager.controller;

import com.example.DocumentManager.dto.DocumentSearchDto;
import com.example.DocumentManager.entity.DocumentEntity;
import com.example.DocumentManager.service.DocumentService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    // ✔ Manual constructor replacing @RequiredArgsConstructor
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentEntity> upload(@RequestPart("file") MultipartFile file) throws Exception {
        DocumentEntity saved = documentService.uploadDocument(file);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentSearchDto>> search(@RequestParam("q") String query) {
        List<DocumentEntity> docs = documentService.search(query);

        List<DocumentSearchDto> result = docs.stream()
                .map(d -> new DocumentSearchDto(
                        d.getId(),
                        d.getFileName(),
                        d.getContentType(),
                        createSnippet(d.getCleanedText(), query)
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
        DocumentEntity doc = documentService.getById(id);
        Path path = Path.of(doc.getFilePath());
        byte[] bytes = Files.readAllBytes(path);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(bytes);
    }

    private String createSnippet(String cleanedText, String query) {
        if (cleanedText == null || cleanedText.isBlank()) return "";
        String lower = cleanedText.toLowerCase();
        String qLower = query.toLowerCase();

        int idx = lower.indexOf(qLower);
        if (idx == -1) {
            return cleanedText.length() > 150
                    ? cleanedText.substring(0, 150) + "..."
                    : cleanedText;
        }

        int start = Math.max(0, idx - 40);
        int end = Math.min(cleanedText.length(), idx + qLower.length() + 40);

        return (start > 0 ? "..." : "") +
                cleanedText.substring(start, end) +
                (end < cleanedText.length() ? "..." : "");
    }

    @GetMapping("/search/meta")
    public ResponseEntity<List<DocumentSearchDto>> searchMeta(@RequestParam("q") String query) {

        List<DocumentEntity> docs = documentService.searchInCleanedText(query);

        List<DocumentSearchDto> result = docs.stream()
                .map(d -> new DocumentSearchDto(
                        d.getId(),
                        d.getFileName(),
                        d.getContentType(),
                        ""  // no snippet, only metadata
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    }



