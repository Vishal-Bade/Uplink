package com.example.Uplink_Backend.modules.ocr.repository;

import com.example.Uplink_Backend.modules.ocr.entity.OcrJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OcrJobRepository extends JpaRepository<OcrJob, UUID> {
    Page<OcrJob> findByUploadedByIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    List<OcrJob> findByStatus(String status);
}
