package com.example.DocumentManager.service;

import com.example.DocumentManager.entity.DocumentEntity;
import com.example.DocumentManager.repository.DocumentRepository;
import com.example.DocumentManager.text.TextCleaner;
import com.example.DocumentManager.vision.PdfTextExtractor;
import com.example.DocumentManager.vision.VisionService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final VisionService visionService;
    private final TextCleaner textCleaner;
    private final PdfTextExtractor pdfTextExtractor;

    @Value("${file.storage.location}")
    private String storageLocation;

    public DocumentService(DocumentRepository documentRepository,
                           VisionService visionService,
                           TextCleaner textCleaner, PdfTextExtractor pdfTextExtractor) {
        this.documentRepository = documentRepository;
        this.visionService = visionService;
        this.textCleaner = textCleaner;
        this.pdfTextExtractor = pdfTextExtractor;
    }

    public DocumentEntity uploadDocument(MultipartFile file) throws Exception {
        // 1. Save file to disk
        Path uploadDir = Paths.get(storageLocation);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String originalFileName = file.getOriginalFilename();
        String savedFileName = System.currentTimeMillis() + "_" + originalFileName;
        Path target = uploadDir.resolve(savedFileName);
        Files.copy(file.getInputStream(), target);

        // 2. Extract text using Vision API
        String rawText;
        if (file.getContentType().equalsIgnoreCase("application/pdf")) {
            rawText = pdfTextExtractor.extractText(file);
        } else {
            rawText = visionService.extractText(file);
        }

        // 3. Clean text
        String cleaned = textCleaner.buildCleanedText(rawText);

        // 4. Save to DB
        DocumentEntity entity = new DocumentEntity();
        entity.setFileName(originalFileName);
        entity.setContentType(file.getContentType());
        entity.setFilePath(target.toString());
        entity.setRawText(rawText);
        entity.setCleanedText(cleaned);
        entity.setCreatedAt(LocalDateTime.now());

        return documentRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> searchInCleanedText(String query) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (query == null || query.isBlank()) {
            result.put("error", "Query is empty");
            return result;
        }

        query = query.trim();

        // 1️⃣ Phrase Search (exact phrase in quotes)
        if (query.startsWith("\"") && query.endsWith("\"")) {
            String phrase = query.substring(1, query.length() - 1);
            List<DocumentEntity> phraseResults = documentRepository.phraseSearch(phrase);
            result.put("search_type", "phrase_search");
            result.put("query", phrase);
            result.put("results", phraseResults);
            return result;
        }

        // 2️⃣ Regular search (your existing functionality)
        List<DocumentEntity> regularResults = documentRepository.searchByTsvector(query.toLowerCase());
        result.put("search_type", "regular_search");
        result.put("query", query);
        result.put("results", regularResults);

        return result;
    }

    public List<DocumentEntity> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return documentRepository.searchByKeyword(query.toLowerCase());
    }

    public DocumentEntity getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found: " + id));
    }
}