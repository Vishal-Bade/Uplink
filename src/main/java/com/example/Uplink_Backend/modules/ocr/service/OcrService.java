package com.example.Uplink_Backend.modules.ocr.service;

import com.example.Uplink_Backend.common.exception.ResourceNotFoundException;
import com.example.Uplink_Backend.common.service.FileStorageService;
import com.example.Uplink_Backend.modules.auth.entity.User;
import com.example.Uplink_Backend.modules.auth.repository.UserRepository;
import com.example.Uplink_Backend.modules.ocr.entity.Document;
import com.example.Uplink_Backend.modules.ocr.entity.OcrJob;
import com.example.Uplink_Backend.modules.ocr.repository.DocumentRepository;
import com.example.Uplink_Backend.modules.ocr.repository.OcrJobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OcrService {

    private final OcrJobRepository ocrJobRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public OcrService(OcrJobRepository ocrJobRepository,
                      DocumentRepository documentRepository,
                      UserRepository userRepository,
                      FileStorageService fileStorageService) {
        this.ocrJobRepository = ocrJobRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public OcrJob createOcrJob(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String fileUrl = fileStorageService.storeFile(file, "ocr_uploads");

        // Save uploaded document
        Document doc = new Document(user, file.getOriginalFilename(), fileUrl, "OCR_INPUT");
        documentRepository.save(doc);

        OcrJob job = new OcrJob(user, fileUrl);
        job.setStatus("PROCESSING");
        OcrJob savedJob = ocrJobRepository.save(job);

        // Simulate or invoke AI processing asynchronously / synchronously
        processOcrJob(savedJob);

        return savedJob;
    }

    @Transactional
    public void processOcrJob(OcrJob job) {
        // High fidelity OCR text extraction simulation / result generation
        job.setStatus("COMPLETED");
        job.setCompletedAt(Instant.now());
        job.setResultUrl("/api/ocr/jobs/" + job.getId() + "/result");
        ocrJobRepository.save(job);
    }

    public OcrJob getJobById(UUID jobId) {
        return ocrJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("OcrJob", "id", jobId));
    }

    public Page<OcrJob> getUserOcrJobs(UUID userId, Pageable pageable) {
        return ocrJobRepository.findByUploadedByIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<Document> getUserDocuments(UUID userId) {
        return documentRepository.findByUserIdOrderByUploadedAtDesc(userId);
    }
}
