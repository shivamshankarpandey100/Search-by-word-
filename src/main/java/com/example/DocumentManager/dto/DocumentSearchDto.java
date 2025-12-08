package com.example.DocumentManager.dto;



import lombok.AllArgsConstructor;
import lombok.Data;


public class DocumentSearchDto {
    private Long id;
    private String fileName;
    private String contentType;
    private String snippet;

    public DocumentSearchDto(Long id, String fileName, String contentType, String snippet) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.snippet = snippet;
    }

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

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}
