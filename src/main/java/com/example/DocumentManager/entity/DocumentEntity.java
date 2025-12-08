package com.example.DocumentManager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    // Raw text from Vision API
    @Lob
    private String rawText;

    @Lob
    private String cleanedText;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // No-arg constructor required by JPA
    public DocumentEntity() {
    }

    // All-args constructor (you already had this)
    public DocumentEntity(Long id,
                          String fileName,
                          String contentType,
                          String filePath,
                          String rawText,
                          String cleanedText,
                          LocalDateTime createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.filePath = filePath;
        this.rawText = rawText;
        this.cleanedText = cleanedText;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getCleanedText() {
        return cleanedText;
    }

    public void setCleanedText(String cleanedText) {
        this.cleanedText = cleanedText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}