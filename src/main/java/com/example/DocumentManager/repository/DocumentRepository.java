package com.example.DocumentManager.repository;

import com.example.DocumentManager.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    @Query(value = "SELECT * FROM documents " +
            "WHERE lower(cleaned_text) LIKE lower(concat('%', :keyword, '%'))",
            nativeQuery = true)
    List<DocumentEntity> searchByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM documents " +
            "WHERE search_vector @@ plainto_tsquery('english', :keyword)",
            nativeQuery = true)
    List<DocumentEntity> searchByTsvector(@Param("keyword") String keyword);


    /**
     * Phrase Search - exact phrase matching using tsvector
     * Works with PostgreSQL full-text search for exact phrase matching
     */
    @Query(value = """
        SELECT d.* FROM documents d
        WHERE to_tsvector('english', convert_from(lo_get(d.cleaned_text), 'UTF8')) 
              @@ phraseto_tsquery('english', :phrase)
        """, nativeQuery = true)
    List<DocumentEntity> phraseSearch(@Param("phrase") String phrase);

    /**
     * Alternative phrase search using LIKE with exact phrase (simpler but less efficient)
     * Use this if the tsvector approach doesn't work with your OID setup
     */
    @Query(value = """
        SELECT d.* FROM documents d
        WHERE convert_from(lo_get(d.cleaned_text), 'UTF8') ILIKE concat('%', :phrase, '%')
        """, nativeQuery = true)
    List<DocumentEntity> phraseSearchSimple(@Param("phrase") String phrase);
}