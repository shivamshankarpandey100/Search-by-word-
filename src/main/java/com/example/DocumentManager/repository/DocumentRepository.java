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

    @Query(value = """
        SELECT d.* FROM documents d
        WHERE convert_from(lo_get(d.cleaned_text), 'UTF8') ILIKE concat('%', :keyword, '%')
        """, nativeQuery = true)
    List<DocumentEntity> searchByKeywordInLO(@Param("keyword") String keyword);

}
